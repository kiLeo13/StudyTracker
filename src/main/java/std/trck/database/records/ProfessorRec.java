package std.trck.database.records;

import org.jooq.impl.TableRecordImpl;
import std.trck.database.tables.Professors;

public class ProfessorRec extends TableRecordImpl<ProfessorRec> {

    private static final Professors PROFESSORS = Professors.PROFESSORS;

    public ProfessorRec() {
        super(PROFESSORS);
    }

    public ProfessorRec(int id, String name, String fullName) {
        this();
        set(PROFESSORS.ID, id);
        set(PROFESSORS.NAME, name);
        set(PROFESSORS.FULL_NAME, fullName);
    }

    public int getId() {
        return get(PROFESSORS.ID);
    }

    public String getName() {
        return get(PROFESSORS.NAME);
    }

    public String getFullName() {
        return get(PROFESSORS.FULL_NAME);
    }
}