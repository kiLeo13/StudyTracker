package std.trck.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import std.trck.internal.BotData;

public class DBManager {
    private static final HikariConfig config;
    private static final HikariDataSource dataSource;

    public static DSLContext getContext() {
        return DSL.using(dataSource, SQLDialect.SQLITE);
    }

    static {
        config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + BotData.DATABASE_FILE);

        dataSource = new HikariDataSource(config);
    }
}