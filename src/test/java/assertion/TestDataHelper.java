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

    public Consumer<String> verifyDataIsPresent() {
        return data -> Assert.assertNotNull(data, "Data not detected while expected!");
    }

    public Consumer<String> verifyDataEquals(final String expectedData) {
        return data -> Assert.assertEquals(data, expectedData, "Data not detected while expected!");
    }
}
