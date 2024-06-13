package std.trck.api.responses;

import net.dv8tion.jda.api.utils.data.DataObject;
import std.trck.api.exceptions.IncorrectRequestException;

public class Responses {

    public static String message(String val) {
        return DataObject.empty()
                .put("message", val)
                .toString();
    }

    public static String fail(IncorrectRequestException err) {
        return DataObject.empty()
                .put("message", err.getMessage())
                .put("code", err.getCode())
                .toString();
    }

    public static String fail(UserAPIErrors err) {
        return DataObject.empty()
                .put("message", err.message())
                .put("code", err.code())
                .toString();
    }

    public static String messagef(String format, Object... args) {
        return message(String.format(format, args));
    }
}