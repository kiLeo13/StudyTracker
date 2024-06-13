package std.trck.database.records;

import org.jooq.impl.TableRecordImpl;
import std.trck.database.tables.Assignments;

import java.util.UUID;

public class AssignmentRec extends TableRecordImpl<AssignmentRec> {

    private static final Assignments ASSIGNMENTS = Assignments.ASSIGNMENTS;

    public AssignmentRec() {
        super(ASSIGNMENTS);
    }

    public AssignmentRec(int professorId, int subjectId, String title, String desc, long dueDate) {
        this();

        set(ASSIGNMENTS.ID, UUID.randomUUID().toString());
        set(ASSIGNMENTS.PROFESSOR_ID, professorId);
        set(ASSIGNMENTS.SUBJECT_ID, subjectId);
        set(ASSIGNMENTS.TITLE, title);
        set(ASSIGNMENTS.DESCRIPTION, desc == null ? "" : desc);
        set(ASSIGNMENTS.DUE_DATE, dueDate);
    }

    public String getUUID() {
        return get(ASSIGNMENTS.ID);
    }

    public int getProfessorId() {
        return get(ASSIGNMENTS.PROFESSOR_ID);
    }

    public int getSubjectId() {
        return get(ASSIGNMENTS.SUBJECT_ID);
    }

    public String getTitle() {
        return get(ASSIGNMENTS.TITLE);
    }

    public String getDescription() {
        String desc = get(ASSIGNMENTS.DESCRIPTION);
        return desc == null || desc.isBlank() ? null : desc;
    }

    public long getDueDate() {
        return get(ASSIGNMENTS.DUE_DATE);
    }

    public long getTimeCreated() {
        return get(ASSIGNMENTS.CREATED_AT);
    }
}