package assertion;

import org.testng.Assert;
import org.testng.annotations.Test;

public class Tests {

    @Test
    public void success() {
        AsyncAssert.aAssert("Some description", () -> {
            uglyDelay(5000);
            return "response data";
        }, data -> Assert.assertEquals(data, "response data"));

        Assert.assertTrue(AsyncAssert.getAssertRecords().stream().anyMatch(r -> r.getTestId().contains("success")),
                "No res in table after AAssert creation.");
        Assert.assertTrue(AsyncAssert.getAssertRecords().stream()
                        .filter(r -> r.getTestId().contains("success"))
                        .map(r -> r.isSuccess())
                        .findFirst().get() == false,
                "Wait not complete but result already received!");
        uglyDelay(5000);
        Assert.assertTrue(AsyncAssert.getAssertRecords().stream()
                        .filter(r -> r.getTestId().contains("success"))
                        .map(r -> r.isSuccess())
                        .findFirst().get() == true,
                "Wait complete but result not successful");
    }

    @Test
    public void fail() {
        AsyncAssert.aAssert("Some description fail", () -> {
            uglyDelay(1000);
            return "response data bad";
        }, data -> Assert.assertEquals(data, "response data"));
        uglyDelay(5000);
        Assert.assertTrue(AsyncAssert.getAssertRecords().stream()
                        .filter(r -> r.getTestId().contains("fail"))
                        .map(r -> r.isSuccess())
                        .findFirst().get() == false,
                "Wait complete but result not successful");
    }

    private void uglyDelay(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
