package std.trck.database.tables;

import org.jetbrains.annotations.NotNull;
import org.jooq.Field;
import org.jooq.impl.SQLDataType;
import std.trck.database.AbstractTable;
import std.trck.database.records.AssignmentRec;

import static org.jooq.impl.DSL.name;

public class Assignments extends AbstractTable<String, AssignmentRec> {

    public static final String DATE_FORMATTER = "dd/MM/yyyy";
    public static final String DATETIME_FORMATTER = "dd/MM/yyyy HH:mm";

    public static final Assignments ASSIGNMENTS = new Assignments();

    public final Field<String> ID            = createField(name("uuid"),           SQLDataType.CHAR.notNull());
    public final Field<Integer> PROFESSOR_ID = createField(name("professor_id"), SQLDataType.INTEGER.notNull());
    public final Field<Integer> SUBJECT_ID   = createField(name("subject_id"),   SQLDataType.INTEGER.notNull());
    public final Field<String> TITLE         = createField(name("title"),        SQLDataType.CHAR.notNull());
    public final Field<String> DESCRIPTION   = createField(name("description"),  SQLDataType.CHAR.notNull());
    public final Field<Long> DUE_DATE        = createField(name("due_date"),     SQLDataType.BIGINT.notNull());
    public final Field<Long> CREATED_AT      = createField(name("created_at"),   SQLDataType.BIGINT.notNull());

    public Assignments() {
        super("assignments");
    }

    @Override
    public Field<String> getIdKey() {
        return ID;
    }

    @NotNull
    @Override
    public Class<AssignmentRec> getRecordType() {
        return AssignmentRec.class;
    }
}