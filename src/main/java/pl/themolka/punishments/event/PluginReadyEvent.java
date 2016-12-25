package pl.themolka.punishments.event;

import pl.themolka.punishments.PunishmentsPlugin;

public class PluginReadyEvent extends PluginEvent {
    public PluginReadyEvent(PunishmentsPlugin plugin) {
        super(plugin);
    }
}
