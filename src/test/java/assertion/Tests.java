package assertion;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class Tests {

    @Test
    public void success() {
        AsyncAssert.aAssert("Some description",
                TestDataHelper.waitAndGetStrData(5000, "response data"),
                TestDataHelper.verifyDataEquals("response data"));

        Assert.assertTrue(TestDataHelper.isAssertPresent("success"),
                "No res in table after AAssert creation.");
        Assert.assertTrue(!TestDataHelper.isAssertStateSucess("success"),
                "Wait not complete but result already received!");
        uglyDelay(10000);
        Assert.assertTrue(TestDataHelper.isAssertStateSucess("success"),
                "Wait complete but result not successful");
    }

    @Test(dataProvider = "testDataProvider")
    public void fail(final int number, final String smth) {
        AsyncAssert.aAssert("Some description fail",
                TestDataHelper.waitAndGetStrData(1000, "response data bad"),
                TestDataHelper.verifyDataEquals("response data"));
        uglyDelay(5000);
        Assert.assertTrue(TestDataHelper.isAssertPresent("fail"),
                "No res in table after AAssert creation.");
        Assert.assertTrue(!TestDataHelper.isAssertStateSucess("fail"),
                "Wait complete but result not successful");
    }

    private void uglyDelay(final long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @DataProvider
    public static Object[][] testDataProvider() {
        return new Object[][] {{1, "a"}, {2, "b"}};
    }
}
