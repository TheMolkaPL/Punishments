package pl.themolka.punishments.session;

import pl.themolka.commons.storage.Query;
import pl.themolka.commons.storage.QueryCallback;
import pl.themolka.punishments.PunishmentsPlugin;
import pl.themolka.punishments.database.DBBridge;
import pl.themolka.punishments.database.DBObject;
import pl.themolka.punishments.database.DBQuery;
import pl.themolka.punishments.database.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

public class OfflineSession implements DBObject {
    public static final String FIELD_ID = "_id";
    public static final String FIELD_CREATION = "creation";
    public static final String FIELD_CLIENT_ID = "client_id";
    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_USERNAME_LOWER = "username_lower";
    public static final String FIELD_SERVER_NAME = "server_name";

    private final PunishmentsPlugin plugin;

    private final Client client;
    private boolean fetched;
    private final TreeSet<SessionPart> history = new TreeSet<>(new SessionPartComparator());

    public OfflineSession(PunishmentsPlugin plugin) {
        this(plugin, (UUID) null);
    }

    public OfflineSession(PunishmentsPlugin plugin, UUID uuid) {
        this.plugin = plugin;

        this.client = new Client(uuid);
    }

    public OfflineSession(PunishmentsPlugin plugin, ResultSet result) throws SQLException {
        this.plugin = plugin;

        this.client = new Client();
        this.fromResultSet(result);
    }

    @Override
    public Query fetch(Database database) {
        QueryCallback callback = new QueryCallback() {
            @Override
            public void onResult(ResultSet result, int index, int count) {
                try {
                    OfflineSession.this.fromResultSet(result);
                } catch (SQLException sql) {
                    sql.printStackTrace();
                }
            }
        };

        if (this.getId() != 0) {
            return database.query(DBQuery.SELECT_SESSION_BY_ID, this.getId()).forEach(callback);
        } else if (this.getClient().getId() != 0) {
            return database.query(DBQuery.SELECT_SESSIONS_BY_CLIENT_ID, this.getClient().getId()).forEach(callback);
        } else if (this.getUsername() != null) {
            return database.query(DBQuery.SELECT_SESSIONS_BY_USERNAME, this.getUsername()).forEach(callback);
        } else if (this.getUsernameLower() != null) {
            return database.query(DBQuery.SELECT_SESSIONS_BY_USERNAME_LOWER, this.getUsernameLower()).forEach(callback);
        }

        return null;
    }

    @Override
    public int getId() {
        return this.getSession().getId();
    }

    @Override
    public boolean isFetched() {
        return this.fetched;
    }

    @Override
    public Query push(Database database) {
        return database.query(DBQuery.INSERT_SESSION,
                this.getClient().getId(),
                this.getUsername(),
                this.getUsername().toLowerCase(),
                this.plugin.getServerName()
        ).forEach(new QueryCallback() {
            @Override
            public void onResult(ResultSet result, int index, int count) {
                try {
                    OfflineSession.this.setId(result.getInt(FIELD_ID));
                    OfflineSession.this.setCreation(DBBridge.toInstant(result.getTimestamp(FIELD_CREATION)));
                } catch (SQLException sql) {
                    sql.printStackTrace();
                }

                OfflineSession.this.setFetched(true);
            }
        });
    }

    @Override
    public void setFetched(boolean fetched) {
        this.fetched = fetched;
    }

    @Override
    public void setId(int id) {
        this.getSession().setId(id);
    }

    @Override
    public Query update(Database database) {
        return this.push(database);
    }

    public void addHistoryPart(SessionPart part) {
        this.history.add(part);
    }

    public Client getClient() {
        return this.client;
    }

    public Instant getCreation() {
        return this.getSession().getCreation();
    }

    public SortedSet<SessionPart> getHistory() {
        return this.history;
    }

    public SessionPart getSession() {
        return this.getHistory().last();
    }

    public String getUsername() {
        return this.getSession().getUsername();
    }

    public String getUsernameLower() {
        return this.getSession().getUsername();
    }

    public UUID getUuid() {
        return this.getClient().getUuid();
    }

    public String getServerName() {
        return this.getSession().getServerName();
    }

    public void setCreation(Instant creation) {
        this.getSession().setCreation(creation);
    }

    public void setUsername(String username) {
        this.getSession().setUsername(username);
    }

    public void setUsernameLower(String usernameLower) {
        this.getSession().setUsernameLower(usernameLower);
    }

    public void setServerName(String serverName) {
        this.getSession().setServerName(serverName);
    }

    private void fromResultSet(ResultSet result) throws SQLException {
        this.getClient().setId(result.getInt(FIELD_CLIENT_ID));
        this.addHistoryPart(new SessionPart(result));

        this.setFetched(true);
    }

    public class SessionPart {
        private int id;
        private Instant creation;
        private String username;
        private String usernameLower;
        private String serverName;

        public SessionPart() {
        }

        public SessionPart(ResultSet result) throws SQLException {
            this.id = result.getInt(FIELD_ID);
            this.creation = DBBridge.toInstant(result.getTimestamp(FIELD_CREATION));
            this.username = result.getString(FIELD_USERNAME);
            this.usernameLower = result.getString(FIELD_USERNAME_LOWER);
            this.serverName = result.getString(FIELD_SERVER_NAME);
        }

        public int getId() {
            return this.id;
        }

        public Instant getCreation() {
            return this.creation;
        }

        public String getUsername() {
            return this.username;
        }

        public String getUsernameLower() {
            return this.usernameLower;
        }

        public String getServerName() {
            return this.serverName;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setCreation(Instant creation) {
            this.creation = creation;
        }

        public void setUsername(String username) {
            this.username = username;
            this.usernameLower = username.toLowerCase();
        }

        public void setUsernameLower(String usernameLower) {
            this.usernameLower = usernameLower;
        }

        public void setServerName(String serverName) {
            this.serverName = serverName;
        }
    }

    private class SessionPartComparator implements Comparator<SessionPart> {
        @Override
        public int compare(SessionPart o1, SessionPart o2) {
            return Integer.valueOf(o1.getId()).compareTo(o2.getId());
        }
    }
}
