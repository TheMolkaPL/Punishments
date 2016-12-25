package pl.themolka.punishments.session;

import pl.themolka.commons.storage.Query;
import pl.themolka.commons.storage.QueryCallback;
import pl.themolka.punishments.database.DBObject;
import pl.themolka.punishments.database.DBQuery;
import pl.themolka.punishments.database.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Client implements DBObject {
    public static final String FIELD_ID = "_id";
    public static final String FIELD_UUID = "uuid";

    private boolean fetched = false;
    private int id;

    private UUID uuid;

    public Client() {
    }

    public Client(UUID uuid) {
        this.uuid = uuid;
    }

    public Client(ResultSet result) throws SQLException {
        this.fromResultSet(result);
    }

    @Override
    public Query fetch(Database database) {
        QueryCallback callback = new QueryCallback() {
            @Override
            public void onResult(ResultSet result, int index, int count) {
                try {
                    Client.this.fromResultSet(result);
                } catch (SQLException sql) {
                    sql.printStackTrace();
                }
            }
        };

        if (this.hasUuid()) {
            return database.query(DBQuery.SELECT_CLIENT_BY_UUID, this.getUuid()).forEach(callback);
        } else if (this.id != 0) {
            return database.query(DBQuery.SELECT_CLIENT_BY_ID, this.getId()).forEach(callback);
        }

        return null;
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
        return database.query(DBQuery.INSERT_CLIENT, this.getUuid()).result(new QueryCallback() {
            @Override
            public void onResult(ResultSet result, int index, int count) {
                try {
                    Client.this.setId(result.getInt(FIELD_ID));
                } catch (SQLException sql) {
                    sql.printStackTrace();
                }

                Client.this.setFetched(true);
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
        return this.push(database);
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public boolean hasUuid() {
        return this.uuid != null;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    private void fromResultSet(ResultSet result) throws SQLException {
        this.id = result.getInt(FIELD_ID);
        this.uuid = UUID.fromString(result.getString(FIELD_UUID));

        this.setFetched(true);
    }
}
