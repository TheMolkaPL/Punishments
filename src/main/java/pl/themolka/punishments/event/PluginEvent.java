package pl.themolka.punishments.event;

import pl.themolka.punishments.PunishmentsPlugin;

public class PluginEvent {
    private final PunishmentsPlugin plugin;

    public PluginEvent(PunishmentsPlugin plugin) {
        this.plugin = plugin;
    }

    public PunishmentsPlugin getPlugin() {
        return this.plugin;
    }
}
