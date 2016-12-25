package pl.themolka.punishments.command;

import pl.themolka.commons.command.CommandContext;
import pl.themolka.commons.command.CommandInfo;
import pl.themolka.punishments.PunishmentsPlugin;
import pl.themolka.punishments.session.OnlineSession;

public class PunishmentsCommand {
    private final PunishmentsPlugin plugin;

    public PunishmentsCommand(PunishmentsPlugin plugin) {
        this.plugin = plugin;
    }

    @CommandInfo(name = "punishments", description = "Punishments plugin")
    public void punishmentsCommand(OnlineSession sender, CommandContext context) {
        sender.sendSuccess("Plugin Punishments wersja " + this.plugin.getDescription().getVersion());
        sender.sendInfo("Stworzone przez TheMolkaPL");
        sender.sendInfo("Strona projektu: https://github.com/TheMolkaPL/Punishments");
        sender.sendInfo("Bugi? Pytania? https://github.com/TheMolkaPL/Punishments/issues");
        sender.sendInfo("(prosze uzyc funkcji wyszukiwarki przed stworzeniem nowych issue!)");
    }
}
