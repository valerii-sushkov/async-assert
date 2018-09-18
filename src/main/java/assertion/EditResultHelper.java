package assertion;

import com.google.common.collect.Iterables;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.util.logging.Logger;

/**
 * Methods for editing
 * result of test cases that already complete.
 */
public final class EditResultHelper {
    /**
     * Logger of current class.
     */
    public static final Logger LOGGER =
            Logger.getLogger(EditResultHelper.class.getCanonicalName());

    /**
     * Block regular constructor.
     */
    private EditResultHelper() {

    }

    /**
     * Update test cases results, after all cases complete.
     * @param context - test context of suite.
     */
    public static void updateSuiteTestsData(final ITestContext context) {
        LOGGER.info("Start post processing of tests with async asserts.");
        AsyncAssert.getAssertRecords().stream()
                .filter(rec -> !rec.isProcessed())
                .forEach(rec -> {
                    rec.setProcessed(true);
                    if (rec.isSuccess()) {
                        for (ITestResult res : Iterables.concat(
                                context.getPassedTests().getAllResults(),
                                context.getFailedTests().getAllResults())) {
                            if (AsyncAssert.idGenerator(res)
                                    .equals(rec.getTestId())) {
                                reportSuccess(res, rec);
                                break;
                            }
                        }
                    } else {
                        for (ITestResult res : Iterables.concat(
                                context.getPassedTests().getAllResults(),
                                context.getFailedTests().getAllResults())) {
                            if (AsyncAssert.idGenerator(res)
                                    .equals(rec.getTestId())) {
                                reportFail(res, rec);
                                addErrorThrowable(res, rec);
                                updateThrowable(res, rec);
                                moveResultToFailed(context, res);
                                break;
                            }
                        }
                    }
                });
        LOGGER.info("Post processing of tests with async asserts complete.");
    }

    /**
     * Update test cases results after test case is complete,
     * if results is ready.
     * @param iTestResult - test result.
     */
    public static void updateTestsData(final ITestResult iTestResult) {
        AsyncAssert.getAssertRecords().stream()
                .filter(rec -> !rec.isProcessed())
                .filter(AssertRecord::isComplete)
                .filter(rec -> AsyncAssert.idGenerator(iTestResult)
                        .equals(rec.getTestId()))
                .findFirst()
                .ifPresent(rec -> {
                    rec.setProcessed(true);
                    if (rec.isSuccess()) {
                        reportSuccess(iTestResult, rec);
                    } else {
                        reportFail(iTestResult, rec);
                        addErrorThrowable(iTestResult, rec);
                        updateThrowable(iTestResult, rec);
                        moveResultToFailed(iTestResult.getTestContext(),
                                iTestResult);
                    }
                });
    }

    /**
     * Add log line to test result.
     * @param iTestResult - testNg testResult
     * @param record - async assert result record.
     */
    private static void reportSuccess(final ITestResult iTestResult,
                               final AssertRecord record) {
        Reporter.setCurrentTestResult(iTestResult);
        LOGGER.info("Verification of \""
                + record.getDescription() + "\" is OK.");
        Reporter.setCurrentTestResult(null);
    }

    /**
     * Add log line to test result.
     * @param iTestResult - testNg testResult
     * @param record - async assert result record.
     */
    private static void reportFail(final ITestResult iTestResult,
                            final AssertRecord record) {
        Reporter.setCurrentTestResult(iTestResult);
        LOGGER.severe("Verification of \""
                + record.getDescription() + "\" is Failed!!!");
        Reporter.setCurrentTestResult(null);
    }

    /**
     * Move result to list of failed test cases if not there.
     * @param context - test context
     * @param iTestResult - test result
     */
    private static void moveResultToFailed(final ITestContext context,
                                    final ITestResult iTestResult) {
        if (!context.getFailedTests().getAllResults()
                .contains(iTestResult)
                && !context.getSkippedTests().getAllResults()
                .contains(iTestResult)) {
            iTestResult.setStatus(ITestResult.FAILURE);
            context.getPassedTests().removeResult(iTestResult);
            context.getFailedTests().addResult(iTestResult,
                    iTestResult.getMethod());
        }
    }

    /**
     * Update throwable of testcase.
     * @param iTestResult - testNg testResult
     * @param record - async assert result record.
     */
    private static void updateThrowable(final ITestResult iTestResult,
                                 final AssertRecord record) {
        String failMessage = "Verification of \""
                + record.getDescription() + "\" is Failed!!!";
        if (iTestResult.getThrowable() != null) {
            AssertionError thr1 = new AssertionError(failMessage
                    + "; " + iTestResult.getThrowable().getMessage());
            thr1.setStackTrace(iTestResult.getThrowable().getStackTrace());
            iTestResult.setThrowable(thr1);
        } else {
            AssertionError thr1 = new AssertionError(failMessage);
            thr1.setStackTrace(record.getException().getStackTrace());
            iTestResult.setThrowable(thr1);
        }
    }

    /**
     * Add new throwable to test case.
     * @param iTestResult - testNg testResult
     * @param record - async assert result record.
     */
    private static void addErrorThrowable(final ITestResult iTestResult,
                                   final AssertRecord record) {
        Reporter.setCurrentTestResult(iTestResult);
        LOGGER.severe(record.getException().toString());
        Reporter.setCurrentTestResult(null);
    }
}
