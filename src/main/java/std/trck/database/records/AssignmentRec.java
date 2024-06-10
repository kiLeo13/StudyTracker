package std.trck.database.records;

import org.jooq.impl.TableRecordImpl;
import std.trck.database.tables.Assignments;

public class AssignmentRec extends TableRecordImpl<AssignmentRec> {

    private static final Assignments ASSIGNMENTS = Assignments.ASSIGNMENTS;

    public AssignmentRec() {
        super(ASSIGNMENTS);
    }

    public AssignmentRec(int professorId, int subjectId, String title, String description, long dueDate) {
        this();

        set(ASSIGNMENTS.PROFESSOR_ID, professorId);
        set(ASSIGNMENTS.SUBJECT_ID, subjectId);
        set(ASSIGNMENTS.TITLE, title);
        set(ASSIGNMENTS.DESCRIPTION, description);
        set(ASSIGNMENTS.DUE_DATE, dueDate);
    }

    public int getId() {
        return get(ASSIGNMENTS.ID);
    }

    public String getTitle() {
        return get(ASSIGNMENTS.TITLE);
    }

    public String getDescription() {
        return get(ASSIGNMENTS.DESCRIPTION);
    }

    public long getDueDate() {
        return get(ASSIGNMENTS.DUE_DATE);
    }

    public long getTimeCreated() {
        return get(ASSIGNMENTS.CREATED_AT);
    }
}