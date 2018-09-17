package assertion;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.util.ArrayList;
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

public class AsyncAssert {
    private static final Logger LOGGER = Logger.getLogger(AsyncAssert.class.getCanonicalName());
    private static ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("Async-Assert-%d")
            .setDaemon(true)
            .build();
    private static ExecutorService executor = Executors.newCachedThreadPool(threadFactory);
    private static List<AssertRecord> assertContainer = Collections.synchronizedList(new ArrayList<>());
    private static long maxTimeout = 10;

    private AsyncAssert() {

    }

    public static <T> void aAssert(final Supplier<T> waitSupplier, final Consumer<T> action) {
        String line = Reporter.getCurrentTestResult().getInstance().toString() + "(" +
                Reporter.getCurrentTestResult().getMethod().getMethodName() + ")";
        final String finalLine = line;
        if (getAssertRecords().stream().anyMatch(rec -> rec.getDescription().equals(finalLine))) {
            line = line + "[" + (getAssertRecords().stream()
                    .filter(rec -> rec.getDescription().contains(finalLine)).count() + 1) + "]";
        }
        aAssert(line, waitSupplier, action);
    }

    public static String idGenerator(final ITestResult result) {
        return result.getInstance().toString() + "." + result.getName() + "(" + result.getParameters() + ")";
    }

    public static <T> void aAssert(final String description, final Supplier<T> waitSupplier,
                                   final Consumer<T> action) {
        String testId = idGenerator(Reporter.getCurrentTestResult());
        LOGGER.info("New assertion for test: " + testId + " " + description);
        AssertRecord rec = new AssertRecord(testId, description, false,
                new AssertionError("Unknown Execution Exception!"));
        assertContainer.add(rec);
        CompletableFuture.supplyAsync(supplierBox(waitSupplier, rec), executor).thenAccept(t -> {
            try {
                action.accept(t);
                rec.setSuccess(true);
                rec.setException(null);
            } catch (AssertionError assertionError) {
                rec.setSuccess(false);
                rec.setException(assertionError);
            } catch (Throwable ex) {
                rec.setSuccess(false);
                AssertionError error = new AssertionError("Execution Exception! " + ex.toString() +
                        ex.getMessage());
                ex.printStackTrace();
                error.setStackTrace(ex.getStackTrace());
                rec.setException(error);
            }

        });
    }

    private static <T> Supplier<T> supplierBox(final Supplier<T> waitSupplier, final AssertRecord rec) {
        return () -> {
            try {
                return waitSupplier.get();
            } catch (Exception ex) {
                rec.setSuccess(false);
                AssertionError error = new AssertionError("Unhandled Wait Exception! " + ex.toString() +
                        ex.getMessage());
                ex.printStackTrace();
                error.setStackTrace(ex.getStackTrace());
                rec.setException(error);
                throw new RuntimeException("Unhandled Wait Exception! " + ex.toString() +
                        ex.getMessage() + "for " + rec.getTestId(), error);
            }
        };
    }

    public static void executorShutDown() {
        LOGGER.info("Close Assert thread executor.");
        executor.shutdown();
        try {
            executor.awaitTermination(maxTimeout, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException("Error closing Assert executor!", e);
        }
        LOGGER.info("Assert thread executor closed!");
    }

    public static List<AssertRecord> getAssertRecords() {
        List<AssertRecord> copyList;
        synchronized (assertContainer) {
            copyList = new ArrayList<>(assertContainer);
        }
        return copyList;
    }

    public static void setMaxTimeout(final long maxTimeOut) {
        maxTimeout = maxTimeOut;
    }
}
