package assertion;


public class AssertRecord {
    private String testId;
    private String description;
    private boolean isSuccess;
    private Error exception;

    public AssertRecord(String testId, String description, boolean isSuccess, Error exception) {
        this.testId = testId;
        this.description = description;
        this.isSuccess = isSuccess;
        this.exception = exception;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public Error getException() {
        return exception;
    }

    public void setException(Error exception) {
        this.exception = exception;
    }
}
