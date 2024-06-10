package std.trck.database.tables;

import org.jetbrains.annotations.NotNull;
import org.jooq.Field;
import org.jooq.impl.SQLDataType;
import std.trck.database.AbstractTable;
import std.trck.database.records.ProfessorRec;

import static org.jooq.impl.DSL.name;

public class Professors extends AbstractTable<Integer, ProfessorRec> {

    public static final Professors PROFESSORS = new Professors();

    public final Field<Integer> ID        = createField(name("id"),         SQLDataType.INTEGER.identity(true));
    public final Field<String> NAME       = createField(name("name"),       SQLDataType.CHAR.notNull());
    public final Field<String> FULL_NAME  = createField(name("full_name"),  SQLDataType.CHAR.notNull());

    public Professors() {
        super("professors");
    }

    @Override
    public Field<Integer> getIdKey() {
        return ID;
    }

    @NotNull
    @Override
    public Class<ProfessorRec> getRecordType() {
        return ProfessorRec.class;
    }
}