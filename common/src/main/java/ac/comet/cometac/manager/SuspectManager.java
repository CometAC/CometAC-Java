package ac.comet.cometac.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// Server-wide, in-memory record of players who recently flagged a check.
// Deduped per player, aggregating which checks they hit. Entries expire 12h
// after their last flag so this never turns into a growing database.
public final class SuspectManager {
    private SuspectManager() {}

    public static final long TTL_MS = 12L * 60 * 60 * 1000; // 12 hours
    private static final Map<UUID, Suspect> SUSPECTS = new ConcurrentHashMap<>();

    public static void record(UUID uuid, String name, String checkName) {
        if (uuid == null || checkName == null) return;
        long now = System.currentTimeMillis();
        Suspect s = SUSPECTS.computeIfAbsent(uuid, u -> new Suspect(name, now));
        if (name != null) s.name = name;
        s.lastFlag = now;
        s.checks.merge(checkName, 1, Integer::sum);
    }

    // Active suspects, most-recently-flagged first. Prunes expired entries on read.
    public static List<Suspect> active() {
        prune();
        List<Suspect> list = new ArrayList<>(SUSPECTS.values());
        list.sort((a, b) -> Long.compare(b.lastFlag, a.lastFlag));
        return list;
    }

    public static void prune() {
        long cutoff = System.currentTimeMillis() - TTL_MS;
        SUSPECTS.values().removeIf(s -> s.lastFlag < cutoff);
    }

    public static void clear() {
        SUSPECTS.clear();
    }

    public static final class Suspect {
        public volatile String name;
        public final long firstFlag;
        public volatile long lastFlag;
        public final Map<String, Integer> checks = new ConcurrentHashMap<>();

        Suspect(String name, long now) {
            this.name = name;
            this.firstFlag = now;
            this.lastFlag = now;
        }

        public int totalFlags() {
            int total = 0;
            for (int v : checks.values()) total += v;
            return total;
        }
    }
}
