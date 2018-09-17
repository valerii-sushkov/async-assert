package assertion;

import com.google.common.collect.Iterables;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.util.logging.Logger;

/**
 * TestNg listener that should run after all suites and update
 * result of test cases if necessary.
 */
public class AsyncAssertListener implements ISuiteListener {

    /**
     * Logger of current class.
     */
    public static final Logger LOGGER =
            Logger.getLogger(AsyncAssertListener.class.getCanonicalName());

    @Override
    public final void onStart(final ISuite iSuite) {

    }

    @Override
    public final void onFinish(final ISuite iSuite) {
        AsyncAssert.executorShutDown();
        iSuite.getResults().entrySet().stream()
                .forEach(suiteResult -> updateSuiteTestsData(
                        suiteResult.getValue().getTestContext()));

    }

    /**
     * Update test cases results.
     * @param context - test context of suite.
     */
    public final void updateSuiteTestsData(final ITestContext context) {
        AsyncAssert.getAssertRecords().stream().forEach(rec -> {
            if (rec.isSuccess()) {
                for (ITestResult res : Iterables.concat(context.getPassedTests()
                                .getAllResults(),
                        context.getFailedTests().getAllResults())) {
                    if (AsyncAssert.idGenerator(res).equals(rec.getTestId())) {
                        reportSuccess(res, rec);
                        break;
                    }
                }
            } else {
                for (ITestResult res : Iterables.concat(context.getPassedTests()
                                .getAllResults(),
                        context.getFailedTests().getAllResults())) {
                    if (AsyncAssert.idGenerator(res).equals(rec.getTestId())) {
                        LOGGER.info("Update logs:"
                                + res.getInstance().toString()
                                + " " + res.getMethod().getMethodName());
                        reportFail(res, rec);
                        addErrorThrowable(res, rec);
                        updateThrowable(res, rec);
                        moveResultToFailed(context, res);
                        break;
                    }
                }
            }
        });
    }

    /**
     * Add log line to test result.
     * @param iTestResult - testNg testResult
     * @param record - async assert result record.
     */
    private void reportSuccess(final ITestResult iTestResult,
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
    private void reportFail(final ITestResult iTestResult,
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
    private void moveResultToFailed(final ITestContext context,
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
    private void updateThrowable(final ITestResult iTestResult,
                                 final AssertRecord record) {
        String failMessage = "Verification of \""
                + record.getDescription() + "\" is Failed!!!";
        if (iTestResult.getThrowable() != null) {
            Throwable thr1 = new Throwable(failMessage
                    + "; " + iTestResult.getThrowable().getMessage());
            thr1.setStackTrace(iTestResult.getThrowable().getStackTrace());
            iTestResult.setThrowable(thr1);
        } else {
            Throwable thr1 = new Throwable(failMessage);
            thr1.setStackTrace(record.getException().getStackTrace());
            iTestResult.setThrowable(thr1);
        }
    }

    /**
     * Add new throwable to test case.
     * @param iTestResult - testNg testResult
     * @param record - async assert result record.
     */
    private void addErrorThrowable(final ITestResult iTestResult,
                                   final AssertRecord record) {
        Reporter.setCurrentTestResult(iTestResult);
        LOGGER.severe(record.getException().toString());
        Reporter.setCurrentTestResult(null);
    }


}
