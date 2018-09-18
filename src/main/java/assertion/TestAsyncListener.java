package assertion;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Listener for hacking test result after test is complete,
 * if async task is complete. Else it should be processed
 * after all suites completed.
 */
public class TestAsyncListener implements ITestListener {
    @Override
    public void onTestStart(final ITestResult iTestResult) {

    }

    @Override
    public final void onTestSuccess(final ITestResult iTestResult) {
        EditResultHelper.updateTestsData(iTestResult);
    }

    @Override
    public final void onTestFailure(final ITestResult iTestResult) {
        EditResultHelper.updateTestsData(iTestResult);
    }

    @Override
    public void onTestSkipped(final ITestResult iTestResult) {

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(
            final ITestResult iTestResult) {

    }

    @Override
    public void onStart(final ITestContext iTestContext) {

    }

    @Override
    public void onFinish(final ITestContext iTestContext) {

    }
}
