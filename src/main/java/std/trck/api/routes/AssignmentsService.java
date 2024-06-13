package std.trck.api.routes;

import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import std.trck.api.responses.UserAPIErrors;
import std.trck.api.responses.Responses;
import std.trck.api.StudyAPI;
import std.trck.api.exceptions.IncorrectRequestBodyException;
import std.trck.api.exceptions.IncorrectRequestException;
import std.trck.database.records.AssignmentRec;
import std.trck.database.records.SubjectRec;
import std.trck.database.repositories.AssignmentsRepository;
import std.trck.database.repositories.SubjectsRepository;
import std.trck.utils.Bot;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;

import static spark.Spark.*;

public class AssignmentsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssignmentsService.class);
    private static final SubjectsRepository subjRep = new SubjectsRepository();
    private static final AssignmentsRepository assgnRep = new AssignmentsRepository();
    private static final File INDEX_HTML = new File(StudyAPI.STATIC_DIR, "index.html");

    public static void basePageGet(String route) {
        get(route, (req, res) -> {
            try {
                res.type("text/html");
                res.status(200);
                return Bot.readFile(INDEX_HTML);
            } catch (IOException err) {
                res.status(500);
                LOGGER.error("Could not read file at {}", INDEX_HTML, err);
                return "";
            }
        });
    }

    public static void setupAssignmentsGet(String route) {

        get(route, (req, res) -> {

            res.type("application/json");
            res.status(200);

            String pastParam = req.queryParams("past");
            String limitParam = req.queryParams("limit");
            DataObject json = DataObject.empty();

            try {
                int limit = parseSafeInt(limitParam, 50);

                if (limit < 1 || limit > 10000) {
                    res.status(400);
                    return Responses.fail(UserAPIErrors.ASSIGNMENTS_REQUESTLIMIT);
                }

                List<AssignmentDTO> subjects = assgnRep.fetchAll("true".equals(pastParam), limit)
                        .stream()
                        .map(AssignmentDTO::new)
                        .toList();

                return json.put("count", subjects.size())
                        .put("assignments", subjects)
                        .toString();

            } catch (DataAccessException err) {
                res.status(500);
                LOGGER.error("Could not fetch assignments", err);
                return "";
            } catch (IncorrectRequestException err) {
                res.status(400);
                return Responses.fail(err);
            }
        });
    }

    private static int parseSafeInt(String num, int def) {
        try {
            return Integer.parseInt(num);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static void setupAssignmentsPost(String route) {
        post(route, (req, res) -> {

            res.type("application/json");
            res.status(200);

            try {
                DataObject body = DataObject.fromJson(req.body());

                int subjId      = getSafeValue(body, "subject_id", Integer.class);
                Instant now     = Instant.now();
                Instant dueDate = Instant.parse(getSafeValue(body, "due_date", String.class));
                String title    = getSafeValue(body, "title", String.class).strip();
                String desc     = body.getString("description", null);
                SubjectRec subj = subjRep.fetchById(subjId);
                long dueSecs    = dueDate.getEpochSecond();
                int descLen     = desc == null ? 0 : desc.length();
                int titleLen    = title.length();

                if (subj == null) {
                    res.status(400);
                    return Responses.fail(UserAPIErrors.SUBJECT_NOT_FOUND);
                }

                if (titleLen < 2 || titleLen > 50) {
                    res.status(400);
                    return Responses.fail(UserAPIErrors.TITLE_NOT_IN_RANGE);
                }

                if (desc != null && (descLen < 5 || descLen > 1000)) {
                    res.status(400);
                    return Responses.fail(UserAPIErrors.DESCRIPTION_NOT_IN_RANGE);
                }

                if (dueSecs <= now.getEpochSecond()) {
                    res.status(400);
                    return Responses.fail(UserAPIErrors.DUE_DATE_IS_IN_PAST);
                }

                AssignmentRec assignment = new AssignmentRec(subj.getProfessorId(), subj.getId(), title, desc, dueSecs);

                assgnRep.save(assignment);

                return jsonAssignment(assignment);

            } catch (IncorrectRequestBodyException err) {
                res.status(400);
                return Responses.fail(err);
            } catch (ParsingException err) {
                res.status(400);
                return Responses.fail(UserAPIErrors.INCORRECT_JSON_FORMAT);
            } catch (DateTimeParseException err) {
                res.status(400);
                return Responses.fail(UserAPIErrors.INCORRECT_DATE_FORMAT);
            } catch (DataAccessException err) {
                res.status(500);
                LOGGER.error("Could not save assignment to database", err);
                return "";
            }
        });
    }

    private static String jsonAssignment(AssignmentRec rec) {

        DataObject data = DataObject.empty();
        AssignmentRec assgn = assgnRep.fetchById(rec.getUUID());

        return data
                .put("uuid",         assgn.getUUID())
                .put("professor_id", assgn.getProfessorId())
                .put("subject_id",   assgn.getSubjectId())
                .put("title",        assgn.getTitle())
                .put("description",  assgn.getDescription())
                .put("due_date",     Instant.ofEpochSecond(assgn.getDueDate()).toString())
                .put("timestamp",    Instant.ofEpochSecond(assgn.getTimeCreated()).toString())
                .toString();
    }

    @NotNull
    private static <T> T getSafeValue(DataObject json, String key, Class<T> type) {

        try {
            Object val = json.opt(key).orElseThrow();

            if (!type.isInstance(val))
                throw new IncorrectRequestBodyException("Type of '%s' should be %s, not %s, provided: \"%s\"",
                        key,
                        type.getSimpleName(),
                        val.getClass().getSimpleName(),
                        val
                );

            return type.cast(val);

        } catch (NoSuchElementException err) {
            throw new IncorrectRequestBodyException("Field %s of type %s must be present", key, type.getSimpleName());
        }
    }

    private record AssignmentDTO(
            String uuid,
            String title,
            String description,
            String due_date,
            String timestamp,
            int subject_id,
            int professor_id
    ) {
        public AssignmentDTO(AssignmentRec rec) {
            this(
                    rec.getUUID(), rec.getTitle(), rec.getDescription(),
                    Instant.ofEpochSecond(rec.getDueDate()).toString(),
                    Instant.ofEpochSecond(rec.getTimeCreated()).toString(),
                    rec.getSubjectId(),
                    rec.getProfessorId()
            );
        }
    }
}