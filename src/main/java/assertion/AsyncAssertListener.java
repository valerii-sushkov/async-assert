package assertion;

import org.testng.ISuite;
import org.testng.ISuiteListener;

/**
 * TestNg listener that should run after all suites and update
 * result of test cases if necessary.
 */
public class AsyncAssertListener implements ISuiteListener {



    @Override
    public final void onStart(final ISuite iSuite) {

    }

    @Override
    public final void onFinish(final ISuite iSuite) {
        AsyncAssert.executorShutDown();
        iSuite.getResults().entrySet().stream()
                .forEach(suiteResult -> EditResultHelper.updateSuiteTestsData(
                        suiteResult.getValue().getTestContext()));

    }




}
