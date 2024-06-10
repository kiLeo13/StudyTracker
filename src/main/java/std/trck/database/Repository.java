package std.trck.database;

import org.jooq.*;
import org.jooq.Record;
import std.trck.utils.Bot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.noCondition;

public interface Repository<K, T extends AbstractTable<K, R>, R extends Record> {

    T getTable();

    default void save(R rec) {
        save(rec, true);
    }

    default void save(R rec, boolean override) {

        DSLContext ctx = DBManager.getContext();
        Map<Field<?>, Object> values = getValues(rec);
        InsertSetMoreStep<R> insert = ctx.insertInto(getTable())
                .set(values)
                .set(field("created_at"), Bot.unixNow());

        if (override)
            insert.onDuplicateKeyUpdate().set(values).execute();
        else
            insert.onDuplicateKeyIgnore().execute();
    }

    default int delete(R rec) {

        Field<K> idField = getTable().getIdKey();
        K id = idField.getValue(rec);

        if (id == null)
            throw new IllegalStateException("Primary key may not be null when deleting rows for table [" + getTable().getName() + "]");

        DSLContext ctx = DBManager.getContext();

        return ctx.deleteFrom(getTable())
                .where(idField.eq(id))
                .execute();
    }

    default List<R> fetchAll() {

        DSLContext ctx = DBManager.getContext();

        return ctx.fetch(getTable());
    }

    default int countAll() {
        return count((t) -> noCondition());
    }

    default int count(Function<T, ? extends Condition> condition) {

        DSLContext ctx = DBManager.getContext();

        return ctx.fetchCount(getTable(), condition.apply(getTable()));
    }

    default R fetchById(K id) {

        DSLContext ctx = DBManager.getContext();

        return ctx.selectFrom(getTable())
                .where(getTable().getIdKey().eq(id))
                .fetchOne();
    }

    default List<R> fetchWhere(Function<T, ? extends Condition> condition) {
        return fetchWhere(-1, condition);
    }

    default List<R> fetchWhere(int limit, Function<T, ? extends Condition> condition) {
        return fetchWhere(0, limit, condition);
    }

    default List<R> fetchWhere(int offset, int limit, Function<T, ? extends Condition> condition) {

        DSLContext ctx = DBManager.getContext();

        return ctx.selectFrom(getTable())
                .where(condition.apply(getTable()))
                .limit(limit)
                .offset(offset)
                .fetch();
    }

    private Map<Field<?>, Object> getValues(R rec) {

        Field<?>[] fields = rec.fields();
        Map<Field<?>, Object> mappedValues = new HashMap<>(fields.length);

        for (Field<?> f : fields) {

            // Skip fields that are auto-increment (identity) and have a null value
            if (f.getDataType().identity() && f.getValue(rec) == null)
                continue;

            if (!f.getName().equals("created_at"))
                mappedValues.put(f, f.get(rec));
        }

        return mappedValues;
    }
}