package std.trck.api.exceptions;

import std.trck.api.responses.UserAPIErrors;

public class IncorrectRequestException extends IllegalStateException {
    private final int code;

    public IncorrectRequestException(String msg) {
        super(msg);
        this.code = 0;
    }

    public IncorrectRequestException(UserAPIErrors err) {
        super(err.message());
        this.code = err.code();
    }

    public int getCode() {
        return this.code;
    }
}