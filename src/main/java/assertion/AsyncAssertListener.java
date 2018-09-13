package assertion;

import com.google.common.collect.Iterables;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.util.logging.Logger;

public class AsyncAssertListener implements ISuiteListener {

    public static final Logger LOGGER = Logger.getLogger(AsyncAssertListener.class.getCanonicalName());
    @Override
    public void onStart(final ISuite iSuite) {

    }

    @Override
    public void onFinish(final ISuite iSuite) {
        AsyncAssert.executorShutDown();
        iSuite.getResults().entrySet().stream().forEach(e -> onFinish1(e.getValue().getTestContext()));

    }

    public void onFinish1(final ITestContext context) {
        AsyncAssert.getAssertRecords().stream().forEach(rec -> {
            if (rec.isSuccess()) {
                for (ITestResult res : Iterables.concat(context.getPassedTests().getAllResults(),
                        context.getFailedTests().getAllResults())) {
                    if (AsyncAssert.idGenerator(res).equals(rec.getTestId())) {
                        reportSuccess(res, rec);
                        break;
                    }
                }
            } else {
                for (ITestResult res : Iterables.concat(context.getPassedTests().getAllResults(),
                        context.getFailedTests().getAllResults())) {
                    if (AsyncAssert.idGenerator(res).equals(rec.getTestId())) {
                        LOGGER.info("Update logs:" + res.getInstance().toString() + " " +
                                res.getMethod().getMethodName());
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

    private void reportSuccess(final ITestResult iTestResult, final AssertRecord record) {
        Reporter.setCurrentTestResult(iTestResult);
        LOGGER.info("Verification of \"" + record.getDescription() + "\" is OK.");
        Reporter.setCurrentTestResult(null);
    }

    private void reportFail(final ITestResult iTestResult, final AssertRecord record) {
        Reporter.setCurrentTestResult(iTestResult);
        LOGGER.severe("Verification of \"" + record.getDescription() + "\" is Failed!!!");
        Reporter.setCurrentTestResult(null);
    }

    private void moveResultToFailed(final ITestContext context, final ITestResult iTestResult) {
        iTestResult.setStatus(ITestResult.FAILURE);
        if (!context.getFailedTests().getAllResults().contains(iTestResult)) {
            context.getPassedTests().removeResult(iTestResult);
            context.getFailedTests().addResult(iTestResult, iTestResult.getMethod());
        }
    }

    private void updateThrowable(final ITestResult iTestResult, final AssertRecord record) {
        String failMessage = "Verification of \"" + record.getDescription() + "\" is Failed!!!";
        if (iTestResult.getThrowable() != null) {
            Throwable thr1 = new Throwable(failMessage + "; " + iTestResult.getThrowable().getMessage());
            thr1.setStackTrace(iTestResult.getThrowable().getStackTrace());
            iTestResult.setThrowable(thr1);
        } else {
            Throwable thr1 = new Throwable(failMessage);
            thr1.setStackTrace(record.getException().getStackTrace());
            iTestResult.setThrowable(thr1);
        }
    }

    private void addErrorThrowable(final ITestResult iTestResult, final AssertRecord record) {
        Reporter.setCurrentTestResult(iTestResult);
        LOGGER.severe(record.getException().toString());
        Reporter.setCurrentTestResult(null);
    }


}
