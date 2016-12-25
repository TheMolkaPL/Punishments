package pl.themolka.punishments.database;

import java.sql.Timestamp;
import java.time.Instant;

public class DBBridge {
    public static Timestamp toTimestamp(Instant instant) {
        if (instant != null) {
            return new Timestamp(instant.toEpochMilli());
        }
        return null;
    }

    public static Instant toInstant(Timestamp timestamp) {
        if (timestamp != null) {
            return Instant.ofEpochMilli(timestamp.getTime());
        }
        return null;
    }
}
