package pl.themolka.punishments;

import org.bukkit.plugin.Plugin;
import pl.themolka.commons.Commons;
import pl.themolka.commons.CommonsFactory;
import pl.themolka.commons.command.BukkitCommands;
import pl.themolka.commons.command.Commands;
import pl.themolka.commons.event.Events;
import pl.themolka.commons.session.Sessions;
import pl.themolka.commons.storage.Storages;
import pl.themolka.punishments.session.OnlineSession;
import pl.themolka.punishments.session.PunishmentsSessions;

public class PunishmentsCommons implements Commons {
    private final PunishmentsPlugin plugin;

    private final Commands commands;
    private final Events events;
    private final Sessions<OnlineSession> sessions;
    private final Storages storages;

    public PunishmentsCommons(PunishmentsPlugin plugin) {
        this.plugin = plugin;

        this.commands = new BukkitCommands(plugin);
        this.events = new Events();
        this.sessions = new PunishmentsSessions(plugin);
        this.storages = new Storages();
    }

    @Override
    public Commands getCommands() {
        return this.commands;
    }

    @Override
    public Events getEvents() {
        return this.events;
    }

    @Override
    public Sessions getSessions() {
        return this.sessions;
    }

    @Override
    public Storages getStorages() {
        return this.storages;
    }

    public PunishmentsPlugin getPlugin() {
        return this.plugin;
    }

    public static class PunishmentsCommonsFactory extends CommonsFactory {
        private PunishmentsCommons commons;
        private PunishmentsPlugin plugin;

        public PunishmentsCommonsFactory(PunishmentsPlugin plugin) {
            this.plugin = plugin;
        }

        public Commons build() {
            if(this.commons == null) {
                this.commons = new PunishmentsCommons(this.plugin);
            }

            this.loadCommands();
            return this.commons;
        }

        public Plugin getPlugin() {
            return this.plugin;
        }

        private void loadCommands() {
            BukkitCommands commands = (BukkitCommands)this.commons.getCommands();
            commands.setSessions(this.commons.getSessions());
        }
    }
}
