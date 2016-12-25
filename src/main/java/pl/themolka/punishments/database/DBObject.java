package pl.themolka.punishments.database;

import pl.themolka.commons.storage.Query;

public interface DBObject {
    Query fetch(Database database);

    int getId();

    boolean isFetched();

    Query push(Database database);

    void setFetched(boolean fetched);

    void setId(int id);

    Query update(Database database);
}
