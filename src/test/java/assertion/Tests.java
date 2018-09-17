package assertion;

import org.testng.Assert;
import org.testng.annotations.Test;

public class Tests {

    @Test
    public void success() {
        AsyncAssert.aAssert("Some description",
                TestDataHelper.waitAndGetStrData(5000, "response data"),
                TestDataHelper.verifyDataEquals("response data"));

        Assert.assertTrue(AsyncAssert.getAssertRecords().stream().anyMatch(r -> r.getTestId().contains("success")),
                "No res in table after AAssert creation.");
        Assert.assertTrue(!AsyncAssert.getAssertRecords().stream()
                        .filter(r -> r.getTestId().contains("success"))
                        .map(r -> r.isSuccess())
                        .findFirst().get(),
                "Wait not complete but result already received!");
        uglyDelay(10000);
        Assert.assertTrue(AsyncAssert.getAssertRecords().stream()
                        .filter(r -> r.getTestId().contains("success"))
                        .map(r -> r.isSuccess())
                        .findFirst().get(),
                "Wait complete but result not successful");
    }

    @Test
    public void fail() {
        AsyncAssert.aAssert("Some description fail",
                TestDataHelper.waitAndGetStrData(1000, "response data bad"),
                TestDataHelper.verifyDataEquals("response data"));
        uglyDelay(5000);
        Assert.assertTrue(AsyncAssert.getAssertRecords().stream().anyMatch(r -> r.getTestId().contains("fail")),
                "No res in table after AAssert creation.");
        Assert.assertTrue(!AsyncAssert.getAssertRecords().stream()
                        .filter(r -> r.getTestId().contains("fail"))
                        .map(r -> r.isSuccess())
                        .findFirst().get(),
                "Wait complete but result not successful");
    }

    private void uglyDelay(final long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
