package std.trck.internal;

import org.jooq.DSLContext;
import std.trck.database.DBManager;

import java.io.File;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class BotData {
    public static final File DATABASE_FILE = new File("database.db");

    public static String fetch(String key) {

        DSLContext ctx = DBManager.getContext();

        return ctx.select(field("value"))
                .from(table("config"))
                .where(field("key").eq(key))
                .fetchOneInto(String.class);
    }
}