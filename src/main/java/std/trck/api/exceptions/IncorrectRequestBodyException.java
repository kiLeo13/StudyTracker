package std.trck.api.exceptions;

import std.trck.api.responses.UserAPIErrors;

public class IncorrectRequestBodyException extends IncorrectRequestException {

    public IncorrectRequestBodyException(String msg, Object... args) {
        super(String.format(msg, args));
    }

    public IncorrectRequestBodyException(UserAPIErrors err) {
        this(err.message());
    }
}