package std.trck.database.tables;

import org.jetbrains.annotations.NotNull;
import org.jooq.Field;
import org.jooq.impl.SQLDataType;
import std.trck.database.AbstractTable;
import std.trck.database.records.SubjectRec;

import static org.jooq.impl.DSL.name;

public class Subjects extends AbstractTable<Integer, SubjectRec> {

    public static final Subjects SUBJECTS = new Subjects();

    public final Field<Integer> ID            = createField(name("id"),           SQLDataType.INTEGER.identity(true));
    public final Field<Integer> PROFESSOR_ID  = createField(name("professor_id"), SQLDataType.INTEGER.notNull());
    public final Field<String> SIMPLE_NAME    = createField(name("simple_name"),  SQLDataType.CHAR.notNull());
    public final Field<String> NAME           = createField(name("name"),         SQLDataType.CHAR.notNull());

    public Subjects() {
        super("subjects");
    }

    @Override
    public Field<Integer> getIdKey() {
        return ID;
    }

    @NotNull
    @Override
    public Class<SubjectRec> getRecordType() {
        return SubjectRec.class;
    }
}