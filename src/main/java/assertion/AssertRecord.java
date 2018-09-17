package assertion;


public class AssertRecord {
    private String testId;
    private String description;
    private boolean isSuccess;
    private Error exception;

    public AssertRecord(final String testId, final String description, final boolean isSuccess, final Error exception) {
        this.testId = testId;
        this.description = description;
        this.isSuccess = isSuccess;
        this.exception = exception;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(final String testId) {
        this.testId = testId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(final boolean success) {
        isSuccess = success;
    }

    public Error getException() {
        return exception;
    }

    public void setException(final Error exception) {
        this.exception = exception;
    }
}
