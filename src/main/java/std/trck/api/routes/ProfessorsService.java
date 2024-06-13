package std.trck.api.routes;

import net.dv8tion.jda.api.utils.data.DataObject;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import std.trck.database.records.ProfessorRec;
import std.trck.database.repositories.ProfessorsRepository;

import java.util.List;

import static spark.Spark.*;

public class ProfessorsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessorsService.class);
    private static final ProfessorsRepository rep = new ProfessorsRepository();

    public static void setupGetProfessors(String route) {

        get(route, (req, res) -> {

            res.type("application/json");
            res.status(200);

            try {
                DataObject json = DataObject.empty();
                List<ProfessorDTO> subjects = rep.fetchAll()
                        .stream()
                        .map(ProfessorDTO::new)
                        .toList();

                return json.put("count", subjects.size())
                        .put("professors", subjects)
                        .toString();

            } catch (DataAccessException err) {
                res.status(500);
                LOGGER.error("Could not fetch from 'professors' table", err);
                return "";
            }
        });
    }

    private record ProfessorDTO(
            int id,
            String name
    ) {
        public ProfessorDTO(ProfessorRec rec) {
            this(rec.getId(), rec.getName());
        }
    }
}