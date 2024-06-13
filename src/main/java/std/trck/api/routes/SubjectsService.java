package std.trck.api.routes;

import net.dv8tion.jda.api.utils.data.DataObject;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import std.trck.database.records.SubjectRec;
import std.trck.database.repositories.SubjectsRepository;

import java.util.List;

import static spark.Spark.*;

public class SubjectsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubjectsService.class);
    private static final SubjectsRepository rep = new SubjectsRepository();

    public static void setupGetSubjects(String route) {

        get(route, (req, res) -> {

            res.type("application/json");
            res.status(200);

            try {
                DataObject json = DataObject.empty();
                List<SubjectDTO> subjects = rep.fetchAll()
                        .stream()
                        .map(SubjectDTO::new)
                        .toList();

                return json.put("count", subjects.size())
                        .put("subjects", subjects)
                        .toString();

            } catch (DataAccessException err) {
                res.status(500);
                LOGGER.error("Could not fetch subjects", err);
                return "";
            }
        });
    }

    private record SubjectDTO(
            int id,
            int professor_id,
            String name
    ) {
        public SubjectDTO(SubjectRec rec) {
            this(rec.getId(), rec.getProfessorId(), rec.getSimpleName());
        }
    }
}