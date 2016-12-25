package pl.themolka.punishments;

import org.bukkit.plugin.java.JavaPlugin;
import pl.themolka.commons.Commons;
import pl.themolka.commons.event.Event;
import pl.themolka.commons.session.Session;
import pl.themolka.commons.storage.Storage;
import pl.themolka.commons.storage.Storages;
import pl.themolka.punishments.ban.BanHandler;
import pl.themolka.punishments.command.GeneralCommands;
import pl.themolka.punishments.command.PunishmentsCommand;
import pl.themolka.punishments.database.DBFileManagement;
import pl.themolka.punishments.database.DBQuery;
import pl.themolka.punishments.database.Database;
import pl.themolka.punishments.event.PluginReadyEvent;
import pl.themolka.punishments.kick.KickHandler;
import pl.themolka.punishments.mute.MuteHandler;
import pl.themolka.punishments.punishment.Punishment;
import pl.themolka.punishments.punishment.PunishmentHandler;
import pl.themolka.punishments.session.OnlineSession;
import pl.themolka.punishments.warn.WarningHandler;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PunishmentsPlugin extends JavaPlugin implements Runnable {
    private Commons commons;

    private final List<PunishmentHandler> handlers = new ArrayList<>();
    private String serverName;
    private Database storage;

    @Override
    public void onEnable() {
        Instant init = Instant.now();
        this.commons = new PunishmentsCommons.PunishmentsCommonsFactory(this).build();

        Event.setAutoEventPoster(this.getCommons().getEvents());
        this.getCommons().getCommands().registerCommandObjects(
                new GeneralCommands(this),
                new PunishmentsCommand(this));

        try {
            this.loadConfiguration();
            this.loadHandlers();
            this.loadStorage();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }

        this.serverName = this.getServer().getServerName();
        if (this.serverName != null && this.serverName.length() > 64) {
            this.serverName = this.serverName.substring(0, 64);
        }

        try {
            this.getLogger().info("Using " + this.getStorage().getProvider().getDbName() + " as the database model.");
            this.getStorage().getProvider().connect();
            this.getStorage().getProvider().getThread().start();

            for (DBQuery query : DBQuery.values()) {
                if (query.isTableCreation()) {
                    this.getStorage().query(query).handle(); // sync
                }
            }
        } catch (SQLException sql) {
            sql.printStackTrace();
        }

        for (PunishmentHandler handler : this.getHandlers()) {
            handler.onStart();
        }

        this.getCommons().getEvents().post(new PluginReadyEvent(this));

        this.getLogger().info(String.format("Punishments version %s enabled in %s second(s).",
                this.getDescription().getVersion(), (Instant.now().toEpochMilli() - init.toEpochMilli()) / 1000D));
    }

    @Override
    public void onDisable() {
        Instant init = Instant.now();

        for (PunishmentHandler handler : this.getHandlers()) {
            handler.onShutdown();
        }
        this.handlers.clear();

        this.getStorage().getProvider().getThread().interrupt();

        this.getLogger().info(String.format("Punishments version %s disabled in %s second(s).",
                this.getDescription().getVersion(), (Instant.now().toEpochMilli() - init.toEpochMilli() / 1000D)));
    }

    @Override
    public void run() {
        for (PunishmentHandler handler : this.getHandlers()) {
            handler.onUpdate();
        }
    }

    public void addHandler(PunishmentHandler handler) {
        this.handlers.add(handler);
    }

    public Punishment createPunishment(ResultSet result) {
        try {
            int type = result.getInt(Punishment.FIELD_TYPE);

            for (PunishmentHandler handler : this.getHandlers()) {
                if (type == handler.getPunishmentType()) {
                    return handler.createPunishment(result);
                }
            }
        } catch (SQLException sql) {
            sql.printStackTrace();
        }

        return null;
    }

    public Commons getCommons() {
        return this.commons;
    }

    public List<PunishmentHandler> getHandlers() {
        return this.handlers;
    }

    public String getServerName() {
        return this.serverName;
    }

    public OnlineSession getSession(int sessionId) {
        Collection<Session> sessions = this.getCommons().getSessions().getSessions();
        for (Session session : sessions) {
            if (session.getSessionId() == sessionId) {
                return (OnlineSession) session;
            }
        }

        return null;
    }

    public Database getStorage() {
        return this.storage;
    }

    private void loadConfiguration() {
        this.saveDefaultConfig();
        this.reloadConfig();
    }

    private void loadHandlers() {
        this.addHandler(new BanHandler(this));
        this.addHandler(new KickHandler(this));
        this.addHandler(new MuteHandler(this));
        this.addHandler(new WarningHandler(this));
    }

    private void loadStorage() {
        Storages storages = this.getCommons().getStorages();
        storages.setFileManagement(new DBFileManagement(this));

        try {
            storages.insertStoragesFromDefaultFile();
        } catch (IOException ignored) {
        }

        Storage provider = storages.getStorage(DBFileManagement.STORAGE_ID);
        if (provider == null) {
            throw new RuntimeException("Database is not set");
        }

        File queryDirectory = new File(this.getDataFolder(), "sql");
        if (!queryDirectory.exists()) {
            queryDirectory.mkdir();
        }

        this.storage = new Database(this, provider, queryDirectory);
        this.getStorage().saveDefaultSQLQueries();
        this.getStorage().readSQLQueries();
    }
}
