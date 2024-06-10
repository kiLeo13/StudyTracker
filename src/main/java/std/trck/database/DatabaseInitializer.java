package std.trck.database;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jooq.impl.DSL.foreignKey;
import static std.trck.database.tables.Assignments.ASSIGNMENTS;
import static std.trck.database.tables.Professors.PROFESSORS;
import static std.trck.database.tables.Subjects.SUBJECTS;

public class DatabaseInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseInitializer.class);

    public static void init() {

        DSLContext ctx = DBManager.getContext();

        ctx.createTableIfNotExists(PROFESSORS)
                .primaryKey(PROFESSORS.ID)
                .columns(PROFESSORS.fields())
                .execute();

        LOGGER.info("Successfully created table \"{}\"", PROFESSORS.getName());

        ctx.createTableIfNotExists(SUBJECTS)
                .primaryKey(SUBJECTS.ID)
                .columns(SUBJECTS.fields())
                .constraint(
                        foreignKey(SUBJECTS.PROFESSOR_ID).references(PROFESSORS)
                )
                .execute();

        LOGGER.info("Successfully created table \"{}\"", SUBJECTS.getName());

        ctx.createTableIfNotExists(ASSIGNMENTS)
                .primaryKey(ASSIGNMENTS.ID)
                .columns(ASSIGNMENTS.fields())
                .constraints(
                        foreignKey(ASSIGNMENTS.PROFESSOR_ID).references(PROFESSORS),
                        foreignKey(ASSIGNMENTS.SUBJECT_ID).references(SUBJECTS)
                )
                .execute();

        LOGGER.info("Successfully created table \"{}\"", ASSIGNMENTS.getName());
    }
}