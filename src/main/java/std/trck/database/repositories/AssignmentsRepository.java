package std.trck.database.repositories;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import std.trck.database.DBManager;
import std.trck.database.Repository;
import std.trck.database.fusions.AssignmentData;
import std.trck.database.records.AssignmentRec;
import std.trck.database.tables.Assignments;
import std.trck.utils.Bot;

import java.util.List;
import java.util.function.Function;

import static org.jooq.impl.DSL.noCondition;
import static std.trck.database.tables.Assignments.ASSIGNMENTS;
import static std.trck.database.tables.Professors.PROFESSORS;
import static std.trck.database.tables.Subjects.SUBJECTS;

public class AssignmentsRepository implements Repository<String, Assignments, AssignmentRec> {

    public int fetchCount(int professorId, int subject, boolean includePast) {

        DSLContext ctx = DBManager.getContext();
        long now = Bot.unixNow();

        return ctx.fetchCount(getTable(),
                professorId <= 0 ? noCondition() : ASSIGNMENTS.PROFESSOR_ID.eq(professorId),
                subject <= 0     ? noCondition() : ASSIGNMENTS.SUBJECT_ID.eq(subject),
                includePast      ? noCondition() : ASSIGNMENTS.DUE_DATE.gt(now)
        );
    }

    public AssignmentData fetchFullAssignment(int professorId, int subject, boolean includePast, int offset) {
        return fetchFullAssignment(null, professorId, subject, includePast, offset);
    }

    public AssignmentData fetchFullAssignment(String assgnId, int professorId, int subject, boolean includePast, int offset) {

        List<AssignmentData> assignments = fetchFullAssignments(assgnId, professorId, subject, includePast, offset, 1, null);

        return assignments.isEmpty()
                ? null
                : assignments.get(0);
    }

    public List<AssignmentData> fetchFullAssignments(boolean past, Function<Assignments, ? extends Condition> cnd) {
        return fetchFullAssignments(null, -1, -1, past, 0, -1, cnd);
    }

    public List<AssignmentRec> fetchAll(boolean past, int limit) {

        long now = Bot.unixNow();
        DSLContext ctx = DBManager.getContext();

        return ctx.selectFrom(getTable())
                .where(past ? noCondition() : ASSIGNMENTS.DUE_DATE.gt(now))
                .orderBy(ASSIGNMENTS.DUE_DATE.asc())
                .limit(limit)
                .fetch();
    }

    public List<AssignmentData> fetchFullAssignments(String assgnId, int professorId, int subjectId, boolean includePast, int offset, int limit, Function<Assignments, ? extends Condition> cnd) {

        long now = Bot.unixNow();
        DSLContext ctx = DBManager.getContext();

        List<Record> result = ctx.select()
                .from(ASSIGNMENTS)
                .join(PROFESSORS).on(PROFESSORS.ID.eq(ASSIGNMENTS.PROFESSOR_ID))
                .join(SUBJECTS).on(SUBJECTS.ID.eq(ASSIGNMENTS.SUBJECT_ID))
                .where(cnd == null    ? noCondition() : cnd.apply(getTable()))
                .and(subjectId <= 0   ? noCondition() : ASSIGNMENTS.SUBJECT_ID.eq(subjectId))
                .and(includePast      ? noCondition() : ASSIGNMENTS.DUE_DATE.gt(now))
                .and(assgnId == null  ? noCondition() : ASSIGNMENTS.ID.eq(assgnId))
                .and(professorId <= 0 ? noCondition() : PROFESSORS.ID.eq(professorId))
                .orderBy(ASSIGNMENTS.CREATED_AT.asc())
                .limit(limit)
                .offset(offset)
                .fetch();

        return result.stream()
                .map(AssignmentData::new)
                .toList();
    }

    @Override
    public Assignments getTable() {
        return ASSIGNMENTS;
    }
}