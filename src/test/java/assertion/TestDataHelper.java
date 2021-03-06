package assertion;

import org.testng.Assert;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class TestDataHelper {

    public static final Logger LOGGER = Logger.getLogger(TestDataHelper.class.getCanonicalName());

    private  TestDataHelper() {

    }

    public static Supplier<String> waitAndGetStrData(final long delay, final String data) {
        return () -> {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException | RuntimeException ex) {
                LOGGER.info("Wait Data interrupted:" + ex.toString());
                return null;
            }
            return data;
        };
    }

    public static Consumer<String> verifyDataIsPresent() {
        return data -> Assert.assertNotNull(data, "Data not detected while expected!");
    }

    public static Consumer<String> verifyDataEquals(String expectedData) {
        verifyDataIsPresent();
        return data -> Assert.assertEquals(data, expectedData, "Data not detected while expected!");
    }

    public static boolean isAssertPresent(final String searchQuery) {
        return AsyncAssert.getAssertRecords().stream().anyMatch(r -> r.getTestId().contains(searchQuery));
    }

    public static boolean isAssertStateSucess(final String searchQuery) {
        return AsyncAssert.getAssertRecords().stream()
                .filter(r -> r.getTestId().contains(searchQuery))
                .map(r -> r.isSuccess())
                .findFirst().get();
    }
}
