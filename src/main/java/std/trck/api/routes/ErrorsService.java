package std.trck.api.routes;

import net.dv8tion.jda.api.utils.data.DataObject;
import std.trck.api.responses.UserAPIErrors;

import java.util.Arrays;
import java.util.List;

import static spark.Spark.*;

public class ErrorsService {
    private static final List<ErrorDTO> API_ERRORS;

    public static void getMapping(String route) {
        get(route, (req, res) -> {

            res.type("application/json");
            res.status(200);

            String userFriendly = req.queryParams("user");
            List<ErrorDTO> errors = API_ERRORS;

            if ("true".equals(userFriendly)) {
                errors = filterFriendly(API_ERRORS);
            }

            return DataObject.empty()
                    .put("errors", errors)
                    .toString();
        });
    }

    private static List<ErrorDTO> filterFriendly(List<ErrorDTO> errs) {
        return errs.stream()
                .filter(ErrorDTO::displayable)
                .toList();
    }

    static {
        API_ERRORS = Arrays.stream(UserAPIErrors.values())
                .map(ErrorDTO::new)
                .toList();
    }

    private record ErrorDTO(
            int code,
            boolean displayable,
            String message
    ) {
        public ErrorDTO(UserAPIErrors err) {
            this(err.code(), err.displayable(), err.message());
        }
    }
}