package pl.themolka.punishments.database;

public enum DBTable {
    CLIENTS("clients"),
    PUNISHMENTS("punishments"),
    SESSIONS("sessions"),
    ;

    private String name;

    DBTable(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
