package std.trck.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MapperManager {
    private static MapperManager instance;
    private final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
    private final Map<String, Map<String, Object>> mapping = new HashMap<>();

    private MapperManager() {}

    public static MapperManager getManager() {
        if (instance == null) instance = new MapperManager();
        return instance;
    }

    public Builder create(String id) {
        return new Builder(id);
    }

    public Map<String, Object> getData(String entityId) {
        return mapping.get(entityId);
    }

    private void save(String key, Map<String, Object> values) {
        this.mapping.put(key, values);

        SCHEDULER.schedule(() -> this.mapping.remove(key), 10, TimeUnit.MINUTES);
    }

    public static class Builder {
        private final String id;
        private final Map<String, Object> data = new HashMap<>();

        private Builder(String id) {
            this.id = id;
        }

        public Builder set(String key, Object value) {
            this.data.put(key, value);
            return this;
        }

        public void save() {
            MapperManager.getManager().save(this.id, this.data);
        }
    }
}