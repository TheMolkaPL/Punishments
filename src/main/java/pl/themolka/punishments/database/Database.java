package pl.themolka.punishments.database;

import com.google.common.base.Charsets;
import org.apache.commons.io.FileUtils;
import pl.themolka.commons.storage.Query;
import pl.themolka.commons.storage.Storage;
import pl.themolka.punishments.PunishmentsPlugin;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    public static final String QUERY_FILE_EXTENSION = ".sql";
    public static final String TABLE_PATTERN = "%TABLE_NAME%";

    private final PunishmentsPlugin plugin;

    private final Storage provider;
    private final File queryDirectory;
    private final Map<String, String> queryMap = new HashMap<>();
    private String tablePrefix;

    public Database(PunishmentsPlugin plugin, Storage provider, File queryDirectory) {
        this.plugin = plugin;

        this.provider = provider;
        this.queryDirectory = queryDirectory;
        this.tablePrefix = plugin.getConfig().getString("database.table-prefix", "punishments_");
    }

    public Storage getProvider() {
        return this.provider;
    }

    public String getQuery(String id) {
        return this.queryMap.get(id.toLowerCase());
    }

    public String getQuery(String id, String table) {
        String query = this.getQuery(id);
        if (query != null) {
            query = query.replace(TABLE_PATTERN, this.getTablePrefix() + table);
        }

        return query;
    }

    public File getQueryDirectory() {
        return this.queryDirectory;
    }

    public String getTablePrefix() {
        return this.tablePrefix;
    }

    public Query query() {
        return this.getProvider().query();
    }

    public Query query(DBQuery query) {
        return this.query(query.getId(), query.getTable());
    }

    public Query query(String id) {
        return this.getProvider().query(this.getQuery(id));
    }

    public Query query(String id, String table) {
        return this.getProvider().query(this.getQuery(id, table));
    }

    public Query query(DBQuery query, Object... params) {
        return this.query(query.getId(), query.getTable(), params);
    }

    public Query query(String id, String table, Object... params) {
        return this.getProvider().query(this.getQuery(id, table), params);
    }

    public void readSQLQueries() {
        File[] files = this.getQueryDirectory().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(QUERY_FILE_EXTENSION);
            }
        });

        for (File file : files) {
            try {
                String filename = file.getName().substring(0, file.getName().lastIndexOf(QUERY_FILE_EXTENSION));
                List<String> content = Files.readAllLines(file.toPath(), Charsets.UTF_8);

                StringBuilder builder = new StringBuilder();
                for (String line : content) {
                    builder.append(line.trim()).append(" ");
                }

                this.registerQuery(filename.toLowerCase(), builder.toString().trim());
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }

    public void registerQuery(String id, String query) {
        this.queryMap.put(id, query);
    }

    public void saveDefaultSQLQueries() {
        int saved = 0;
        for (DBQuery query : DBQuery.values()) {
            File file = new File(this.getQueryDirectory(), query.getId() + QUERY_FILE_EXTENSION);
            if (file.exists()) {
                continue;
            }

            try {
                if (this.saveDefaultSQLQuery("sql/" + query.getId() + QUERY_FILE_EXTENSION, file)) {
                    saved++;
                }
            } catch (IOException io) {
                io.printStackTrace();
            }
        }

        if (saved > 0) {
            this.plugin.getLogger().info("Saved " + saved + " queries in " + this.getQueryDirectory().getAbsolutePath());
        }
    }

    public boolean saveDefaultSQLQuery(String resource, File destination) throws IOException {
        URL source = this.getClass().getClassLoader().getResource(resource);
        if (source == null) {
            return false;
        }

        FileUtils.copyURLToFile(source, destination);
        return true;
    }

    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }
}
