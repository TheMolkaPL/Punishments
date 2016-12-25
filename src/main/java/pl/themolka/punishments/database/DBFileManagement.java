package pl.themolka.punishments.database;

import pl.themolka.commons.storage.IStorageFileManagement;
import pl.themolka.commons.storage.Storage;
import pl.themolka.commons.storage.StorageProvider;
import pl.themolka.punishments.PunishmentsPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class DBFileManagement implements IStorageFileManagement {
    public static final String STORAGE_ID = "__db";

    private final PunishmentsPlugin plugin;

    public DBFileManagement(PunishmentsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public File getDefaultFile() {
        throw new UnsupportedOperationException("Not supported here");
    }

    @Override
    public List<Storage> readDefaultFile() throws IOException {
        String provider = this.plugin.getConfig().getString("database.provider", StorageProvider.SQLITE.name());
        StorageProvider creator = StorageProvider.find(provider);

        if (creator == null) {
            return null;
        }

        Properties properties = this.readProperties("database.properties");

        // ugly
        if (creator.equals(StorageProvider.SQLITE) && !properties.stringPropertyNames().contains("file")) {
            properties.setProperty("file", new File(this.plugin.getDataFolder(), "sqlite.db").getPath());
        }

        return Collections.singletonList(creator.createStorage(STORAGE_ID, properties));
    }

    @Override
    public List<Storage> readFile(File file) throws IOException {
        throw new UnsupportedOperationException("Not supported here");
    }

    private Properties readProperties(String section) {
        Properties properties = new Properties();

        if (this.plugin.getConfig().get(section) != null) {
            for (String data : this.plugin.getConfig().getStringList(section)) {
                String[] split = data.split("=", 2);
                properties.setProperty(split[0].trim(), split[1].trim());
            }
        }

        return properties;
    }
}
