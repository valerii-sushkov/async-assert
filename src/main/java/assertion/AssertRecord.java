package assertion;

/**
 * Class container of assert result information.
 * This data than mowed to lister for updating or actual result.
 *
 */
public class AssertRecord {
    /**
     * id of test case.
     */
    private String testId;
    /**
     * description of test case.
     */
    private String description;
    /**
     * success of test case.
     */
    private boolean isSuccess;
    /**
     * exception of test case.
     */
    private Error exception;

    /**
     * is test case Complete.
     */
    private boolean isComplete;

    /**
     * is test case Processed.
     */
    private boolean isProcessed;

    /**
     * Construct new record.
     * @param recTestId - id generated for current test.
     * @param recDescription - text describing current verification
     * @param success - is assert was successful.
     * @param recException - error throw by verification.
     */
    public AssertRecord(final String recTestId, final String recDescription,
                        final boolean success, final Error recException) {
        setTestId(recTestId);
        setDescription(recDescription);
        setSuccess(success);
        setException(recException);
        setComplete(false);
        setProcessed(false);
    }

    /**
     * Getter for testId.
     * @return - id of test in record.
     */
    public final String getTestId() {
        return testId;
    }

    /**
     * Setter of testId.
     * @param recordTestId - id of test in record.
     */
    public final void setTestId(final String recordTestId) {
        testId = recordTestId;
    }

    /**
     * Getter for description.
     * @return - description of test in record.
     */
    public final String getDescription() {
        return description;
    }

    /**
     * Setter of description.
     * @param recordDescription - description of test in record.
     */
    public final void setDescription(final String recordDescription) {
        description = recordDescription;
    }

    /**
     * Getter for isSuccess.
     * @return - isSuccess of test in record.
     */
    public final boolean isSuccess() {
        return isSuccess;
    }

    /**
     * Setter of isSuccess.
     * @param success - isSuccess of test in record.
     */
    public final void setSuccess(final boolean success) {
        isSuccess = success;
    }

    /**
     * Getter for isSuccess.
     * @return - isSuccess of test in record.
     */
    public final Error getException() {
        return exception;
    }

    /**
     * Setter of exception.
     * @param recordException - exception of test in record.
     */
    public final void setException(final Error recordException) {
        exception = recordException;
    }

    /**
     * Setter of isComplete.
     * @return - isComplete of test in record.
     */
    public final boolean isComplete() {
        return isComplete;
    }

    /**
     * Setter of isComplete.
     * @param isRecordComplete - complete of test in record.
     */
    public final void setComplete(final boolean isRecordComplete) {
        isComplete = isRecordComplete;
    }

    /**
     * Setter of isProcessed.
     * @return - isProcessed of test in record.
     */
    public final boolean isProcessed() {
        return isProcessed;
    }

    /**
     * Setter of isProcessed.
     * @param isRecordProcessed - isProcessed of test in record.
     */
    public final void setProcessed(final boolean isRecordProcessed) {
        isProcessed = isRecordProcessed;
    }
}
