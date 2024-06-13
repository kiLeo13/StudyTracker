package std.trck.api.responses;

public enum UserAPIErrors {
    INCORRECT_JSON_FORMAT(   10000, false, "The provided JSON is incorrectly formatted."),
    SUBJECT_NOT_FOUND(       10001, true,  "The provided subject does not exist, please, refresh the page and try again."),
    TITLE_NOT_IN_RANGE(      10002, true,  "Title must be between 2 and 50 characters long."),
    DESCRIPTION_NOT_IN_RANGE(10003, true,  "Description must be between 5 and 1000 characters long."),
    DUE_DATE_IS_IN_PAST(     10004, true,  "Due date cannot refer to a moment in the past."),
    INCORRECT_DATE_FORMAT(   10007, false, "Incorrect date format, use ISO-8601: '2007-12-03T10:15:30.00Z'."),
    ASSIGNMENTS_REQUESTLIMIT(10008, false, "Limit returned must be an integer in range of 1 and 10,000.");

    private final int code;
    private final boolean user;
    private final String message;

    UserAPIErrors(int code, boolean user, String message) {
        this.code = code;
        this.user = user;
        this.message = message;
    }

    public int code() {
        return this.code;
    }

    public boolean displayable() {
        return this.user;
    }

    public String message() {
        return this.message;
    }
}