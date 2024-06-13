package std.trck.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    public static String readFile(File file) throws IOException {
        return String.join(System.lineSeparator(), Files.readAllLines(Path.of(file.getAbsolutePath())));
    }
}