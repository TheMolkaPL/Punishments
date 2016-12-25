package pl.themolka.punishments.punishment;

import org.bukkit.event.Listener;
import pl.themolka.punishments.PunishmentsPlugin;
import pl.themolka.punishments.command.CommandHelper;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class PunishmentHandler<T extends Punishment> implements Listener {
    protected final PunishmentsPlugin plugin;

    protected final CommandHelper helper;

    public PunishmentHandler(PunishmentsPlugin plugin) {
        this.plugin = plugin;

        this.helper = new CommandHelper(plugin);
        this.registerAdditional(this);
    }

    public T createPunishment(ResultSet result) throws SQLException {
        return null;
    }

    public abstract int getPunishmentType();

    public void onStart() {
    }

    public void onUpdate() {
    }

    public void onShutdown() {
    }

    public void registerAdditional(Object object) {
        this.plugin.getCommons().getCommands().registerCommandObject(object);
        this.plugin.getCommons().getEvents().register(object);

        if (object instanceof Listener) {
            this.plugin.getServer().getPluginManager().registerEvents((Listener) object, this.plugin);
        }
    }
}
