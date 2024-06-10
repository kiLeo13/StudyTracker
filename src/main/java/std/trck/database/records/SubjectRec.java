package std.trck.database.records;

import org.jooq.impl.TableRecordImpl;
import std.trck.database.tables.Subjects;

public class SubjectRec extends TableRecordImpl<SubjectRec> {

    private static final Subjects SUBJECTS = Subjects.SUBJECTS;

    public SubjectRec() {
        super(SUBJECTS);
    }

    public SubjectRec(int id, String simpleName, String name) {
        this();
        set(SUBJECTS.ID, id);
        set(SUBJECTS.SIMPLE_NAME, simpleName);
        set(SUBJECTS.NAME, name);
    }

    public int getId() {
        return get(SUBJECTS.ID);
    }

    public int getProfessorId() {
        return get(SUBJECTS.PROFESSOR_ID);
    }

    public String getSimpleName() {
        return get(SUBJECTS.SIMPLE_NAME);
    }

    public String getName() {
        return get(SUBJECTS.NAME);
    }
}