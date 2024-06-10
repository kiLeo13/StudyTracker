package std.trck.utils;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class Bot {

    public static long unixNow(int plusDuration, TimeUnit unit) {
        return Instant.now()
                .plusSeconds(unit.toSeconds(plusDuration))
                .getEpochSecond();
    }

    public static long unixNow() {
        return unixNow(0, TimeUnit.SECONDS);
    }

    public static int calcMaxPages(int entries, int pageSize) {

        int maxPages = entries / pageSize;

        if (entries % pageSize > 0)
            maxPages++;

        return Math.max(maxPages, 1);
    }
}