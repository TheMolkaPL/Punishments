package pl.themolka.punishments.database;

public enum DBQuery {
    CREATE_TABLE_CLIENTS("create_table_clients", DBTable.CLIENTS, true),
    INSERT_CLIENT("insert_client", DBTable.CLIENTS),
    SELECT_CLIENT_BY_ID("select_client_by_id", DBTable.CLIENTS),
    SELECT_CLIENT_BY_UUID("select_client_by_uuid", DBTable.CLIENTS),

    CREATE_TABLE_PUNISHMENTS("create_table_punishment", DBTable.PUNISHMENTS, true),
    INSERT_PUNISHMENT("insert_punishment", DBTable.PUNISHMENTS),
    SELECT_ACTIVE_PUNISHMENTS_BY("select_active_punishments_by", DBTable.PUNISHMENTS),
    SELECT_ACTIVE_PUNISHMENTS_FOR("select_active_punishments_for", DBTable.PUNISHMENTS),
    SELECT_ALL_PUNISHMENTS_BY("select_all_punishments_by", DBTable.PUNISHMENTS),
    SELECT_ALL_PUNISHMENTS_FOR("select_all_punishments_for", DBTable.PUNISHMENTS),
    SELECT_PUNISHMENT_BY_ID("select_punishment_by_id", DBTable.PUNISHMENTS),
    SELECT_PUNISHMENTS_BY("select_punishments_by", DBTable.PUNISHMENTS),
    SELECT_PUNISHMENTS_FOR("select_punishments_for", DBTable.PUNISHMENTS),
    UPDATE_PUNISHMENT_BY_ID("update_punishment_by_id", DBTable.PUNISHMENTS),

    CREATE_TABLE_SESSIONS("create_table_sessions", DBTable.SESSIONS, true),
    INSERT_SESSION("insert_session", DBTable.SESSIONS),
    SELECT_SESSION_BY_ID("select_session_by_id"),
    SELECT_SESSIONS_BY_CLIENT_ID("select_sessions_by_client_id", DBTable.SESSIONS),
    SELECT_SESSIONS_BY_USERNAME("select_sessions_by_username"),
    SELECT_SESSIONS_BY_USERNAME_LOWER("select_sessions_by_username_lower"),
    ;

    private final String id;
    private final String table;
    private final boolean tableCreation;

    DBQuery(String id) {
        this(id, (String) null);
    }

    DBQuery(String id, DBTable table) {
        this(id, table, false);
    }

    DBQuery(String id, DBTable table, boolean tableCreation) {
        this(id, table.getName(), tableCreation);
    }

    DBQuery(String id, String table) {
        this(id, table, false);
    }

    DBQuery(String id, String table, boolean tableCreation) {
        this.id = id;
        this.table = table;
        this.tableCreation = tableCreation;
    }

    public String getId() {
        return this.id;
    }

    public String getTable() {
        return this.table;
    }

    public boolean hasTable() {
        return this.table != null;
    }

    public boolean isTableCreation() {
        return this.tableCreation;
    }
}
