package assertion;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Class for creation async asserts and manage theirs waiting.
 */
public final class AsyncAssert {
    /**
     * Logger of current class.
     */
    private static final Logger LOGGER =
            Logger.getLogger(AsyncAssert.class.getCanonicalName());

    /**
     * Custom thread factory with naming.
     */
    private static ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("Async-Assert-%d")
            .setDaemon(true)
            .build();

    /**
     * Executor for running waiting for condition of assert.
     */
    private static ExecutorService executor =
            Executors.newCachedThreadPool(threadFactory);

    /**
     * List for holding all created records with assert results.
     */
    private static List<AssertRecord> assertContainer =
            Collections.synchronizedList(new ArrayList<>());

    /**
     * Default value for maximal time before feature
     * be interrupted during closing of executor.
     */
    private static final long MAX_MINUTES = 10;

    /**
     * Maximal time before feature be interrupted during closing of executor.
     * With default value.
     */
    private static long maxTimeout = MAX_MINUTES;

    /**
     * No public constructor.
     */
    private AsyncAssert() {

    }

    /**
     * Create new Async Assert.
     * Wait for condition provided by supplier which should
     * provide data to consumer.
     * Consumer should perform some regular assert with provided data.
     * Description generated based on test case name.
     *
     * @param waitSupplier - supplier with waiting of some data.
     * @param action - regular assert with provided data
     * @param <T> - data type.
     */
    public static <T> void aAssert(final Supplier<T> waitSupplier,
                                   final Consumer<T> action) {
        String line = String.format("%s(%s)",
                Reporter.getCurrentTestResult().getInstance().toString(),
                Reporter.getCurrentTestResult().getMethod().getMethodName());
        final String finalLine = line;
        if (getAssertRecords().stream()
                .anyMatch(rec -> rec.getDescription().equals(finalLine))) {
            line =  String.format("%s[%s]", line,
                    (getAssertRecords().stream()
                    .filter(rec -> rec.getDescription().contains(finalLine))
                    .count() + 1));
        }
        aAssert(line, waitSupplier, action);
    }


    /**
     * Create new Async Assert.
     * Wait for condition provided by supplier which should provide
     * data to consumer.
     * Consumer should perform some regular assert with provided data.
     *
     * @param description - description of current verification.
     * @param waitSupplier - supplier with waiting of some data.
     * @param action - regular assert with provided data
     * @param <T> - data type.
     */
    public static <T> void aAssert(final String description,
                                   final Supplier<T> waitSupplier,
                                   final Consumer<T> action) {
        String testId = idGenerator(Reporter.getCurrentTestResult());
        LOGGER.info("New assertion for test: " + testId + " " + description);
        AssertRecord rec = new AssertRecord(testId, description, false,
                new AssertionError("Unknown Execution Exception!"));
        assertContainer.add(rec);
        CompletableFuture.supplyAsync(supplierBox(waitSupplier, rec), executor)
                .thenAccept(t -> {
                    try {
                        action.accept(t);
                        rec.setSuccess(true);
                        rec.setException(null);
                    } catch (AssertionError assertionError) {
                        rec.setSuccess(false);
                        rec.setException(assertionError);
                    } catch (Throwable ex) {
                        rec.setSuccess(false);
                        AssertionError error = new AssertionError(
                                String.format("Execution Exception! %s(%s)",
                                        ex.toString(), ex.getMessage()));
                        ex.printStackTrace();
                        error.setStackTrace(ex.getStackTrace());
                        rec.setException(error);
                    } finally {
                        rec.setComplete(true);
                    }
                });
    }

    /**
     * id gen.
     * @param result - id of current test.
     * @return text.
     */
    protected static String idGenerator(final ITestResult result) {
        String params = result.getParameters() == null ? "" :
                Arrays.asList(result.getParameters())
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining(";"));
        return String.format("%s.%s(%s)",
                result.getInstance().toString(),
                result.getName(),
                params);
    }

    /**
     * Pack provided supplier with try/catch to handle
     * situation when supplier failed.
     *
     * @param waitSupplier - custom wait supplier.
     * @param rec - record for adding result data or exception.
     * @param <T> - type of data that supplier should supply to consumer.
     * @return - new supplier boxed with try catch.
     */
    private static <T> Supplier<T> supplierBox(final Supplier<T> waitSupplier,
                                               final AssertRecord rec) {
        return () -> {
            try {
                return waitSupplier.get();
            } catch (Exception ex) {
                rec.setSuccess(false);
                String errorText = String.format(
                        "Unhandled Wait Exception! %s(%s) for %s.",
                        ex.toString(), ex.getMessage(), rec.getTestId());
                AssertionError error = new AssertionError(errorText);
                ex.printStackTrace();
                error.setStackTrace(ex.getStackTrace());
                rec.setException(error);
                throw new RuntimeException(errorText, error);
            }
        };
    }

    /**
     * Shut down executor for wait futures.
     */
    protected static void executorShutDown() {
        LOGGER.info("Close Assert thread executor.");
        executor.shutdown();
        try {
            executor.awaitTermination(maxTimeout, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException("Error closing Assert executor!", e);
        }
        LOGGER.info("Assert thread executor closed!");
    }

    /**
     * Synchronized getter for current records list.
     * @return - List of records.
     */
    protected static List<AssertRecord> getAssertRecords() {
        List<AssertRecord> copyList;
        synchronized (assertContainer) {
            copyList = new ArrayList<>(assertContainer);
        }
        return copyList;
    }

    /**
     * Set maximal time before feature be interrupted
     * during closing of executor.
     * Should be ~equals than expected max possible wait for condition.
     * @param maxTimeOut - max time out in minutes.
     */
    public static void setMaxTimeout(final long maxTimeOut) {
        maxTimeout = maxTimeOut;
    }
}
