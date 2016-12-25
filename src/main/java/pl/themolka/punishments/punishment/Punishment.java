package pl.themolka.punishments.punishment;

import pl.themolka.commons.storage.Query;
import pl.themolka.commons.storage.QueryCallback;
import pl.themolka.punishments.database.DBBridge;
import pl.themolka.punishments.database.DBObject;
import pl.themolka.punishments.database.DBQuery;
import pl.themolka.punishments.database.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Objects;

public abstract class Punishment implements DBObject {
    public static final int CONSOLE_PUNISHER_ID = 0;

    public static final String FIELD_ID = "_id";
    public static final String FIELD_ACTIVE = "active";
    public static final String FIELD_CREATION = "creation";
    public static final String FIELD_EXPIRES = "expires";
    public static final String FIELD_PLAYER_ID = "player_id";
    public static final String FIELD_PUNISHER_ID = "punisher_id";
    public static final String FIELD_REASON = "reason";
    public static final String FIELD_SERVER_NAME = "server_name";
    public static final String FIELD_TYPE = "type";

    private boolean fetched = false;
    private int id;

    private boolean active;
    private Instant creation;
    private Instant expires;
    private int playerId;
    private int punisherId = CONSOLE_PUNISHER_ID;
    private String reason;
    private String serverName;
    private int type = -1;

    public Punishment() {
    }

    public Punishment(ResultSet result) throws SQLException {
        this.fromResultSet(result);
    }

    @Override
    public Query fetch(Database database) {
        return database.query(DBQuery.SELECT_PUNISHMENT_BY_ID, this.getId()).forEach(new QueryCallback() {
            @Override
            public void onResult(ResultSet result, int index, int count) {
                try {
                    Punishment.this.fromResultSet(result);
                } catch (SQLException sql) {
                    sql.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public boolean isFetched() {
        return this.fetched;
    }

    @Override
    public Query push(Database database) {
        PunishmentCreateEvent event = new PunishmentCreateEvent(this);
        if (event.post() || !this.onCreate()) {
            return null;
        }

        return database.query(DBQuery.INSERT_PUNISHMENT,
                DBBridge.toTimestamp(this.getExpires()),
                this.getPlayerId(),
                this.getPunisherId(),
                this.getReason(),
                this.getServerName(),
                this.getPunishmentType()
        ).result(new QueryCallback() {
            @Override
            public void onResult(ResultSet result, int index, int count) {
                try {
                    Punishment.this.setId(result.getInt(Punishment.FIELD_ID));
                    Punishment.this.setActive(result.getBoolean(Punishment.FIELD_ACTIVE));
                    Punishment.this.setCreation(DBBridge.toInstant(result.getTimestamp(Punishment.FIELD_CREATION)));
                } catch (SQLException sql) {
                    sql.printStackTrace();
                }

                Punishment.this.setFetched(true);
                Punishment.this.onHandle();

                PunishmentHandleEvent event = new PunishmentHandleEvent(Punishment.this);
                event.post();
            }
        });
    }

    @Override
    public void setFetched(boolean fetched) {
        this.fetched = fetched;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Query update(Database database) {
        return database.query(DBQuery.UPDATE_PUNISHMENT_BY_ID, this.getId()).result(new QueryCallback() {
            @Override
            public void onResult(ResultSet result, int index, int count) {
                Punishment.this.setFetched(true);
            }
        });
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }

    public boolean expires() {
        return this.expires != null;
    }

    public Instant getCreation() {
        return this.creation;
    }

    public Instant getExpires() {
        return this.expires;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public int getPunisherId() {
        return this.punisherId;
    }

    public String getReason() {
        return this.reason;
    }

    public String getPunishmentName() {
        return "Punishment";
    }

    public abstract int getPunishmentType();

    public String getServerName() {
        return this.serverName;
    }

    public int getType() {
        if (this.hasType()) {
            return this.type;
        }

        return this.getPunishmentType();
    }

    public boolean hasType() {
        return this.type != -1;
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean isConsole() {
        return this.punisherId == CONSOLE_PUNISHER_ID;
    }

    public boolean isPlayerBlackListed() {
        return this.isPunishmentActive();
    }

    public boolean isPunishmentActive() {
        if (this.expires()) {
            return this.isActive() && Instant.now().isBefore(this.getExpires());
        }

        return this.isActive();
    }

    public boolean onCreate() {
        return true;
    }

    public void onHandle() {
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setCreation(Instant creation) {
        this.creation = creation;
    }

    public void setExpires(Instant expires) {
        this.expires = expires;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setPunisherId(int punisherId) {
        this.punisherId = punisherId;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setType(int type) {
        this.type = type;
    }

    private void fromResultSet(ResultSet result) throws SQLException {
        this.id = result.getInt(FIELD_ID);
        this.active = result.getBoolean(FIELD_ACTIVE);
        this.creation = DBBridge.toInstant(result.getTimestamp(FIELD_CREATION));
        this.expires = DBBridge.toInstant(result.getTimestamp(FIELD_EXPIRES));
        this.playerId = result.getInt(FIELD_PLAYER_ID);
        this.punisherId = result.getInt(FIELD_PUNISHER_ID);
        this.reason = result.getString(FIELD_REASON);
        this.serverName = result.getString(FIELD_SERVER_NAME);
        this.type = result.getInt(FIELD_TYPE);

        this.setFetched(true);
    }
}
