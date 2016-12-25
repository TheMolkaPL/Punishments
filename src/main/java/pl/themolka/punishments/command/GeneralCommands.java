package pl.themolka.punishments.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import pl.themolka.commons.command.CommandContext;
import pl.themolka.commons.command.CommandInfo;
import pl.themolka.commons.session.Session;
import pl.themolka.commons.storage.QueryCallback;
import pl.themolka.punishments.PunishmentsPlugin;
import pl.themolka.punishments.database.DBQuery;
import pl.themolka.punishments.punishment.Punishment;
import pl.themolka.punishments.session.OfflineSession;
import pl.themolka.punishments.session.OnlineSession;

import java.sql.ResultSet;
import java.util.List;

public class GeneralCommands {
    private final PunishmentsPlugin plugin;

    private final CommandHelper helper;

    public GeneralCommands(PunishmentsPlugin plugin) {
        this.plugin = plugin;

        this.helper = new CommandHelper(plugin);
    }

    @CommandInfo(name = {"exculpate", "pexceulpate", "pardon", "ppardon", "unban", "punban"},
            description = "Exculpate player from punishment", min = 1, flags = {"all", "by", "last"},
            usage = "[-all|-by|-last] <player|punishment-id>",
            permission = "punishments.command.exculpate", completer = "exculpateCompleter")
    public void exculpateCommand(OnlineSession sender, CommandContext context) {
        boolean allFlag = context.hasFlag("all") && sender.hasPermission("punishments.command.exculpate.all");
        boolean byFlag = context.hasFlag("by") && sender.hasPermission("punishments.command.exculpate.all");
        boolean lastFlag = context.hasFlag("last");
        OfflineSession playerParam = this.helper.parseParamSession(context, 0);
        int punishmentParam = this.helper.parseParamPunishment(context, 0);

        if (playerParam == null && punishmentParam == 0) {
            throw new CommandException("Nie udalo sie odczytac kary");
        }
    }

    public List<String> exculpateCompleter(OnlineSession sender, CommandContext context) {
        return this.helper.playersCompleter();
    }

    @CommandInfo(name = {"lookup", "plookup"}, description = "Lookup player records", min = 1, flags = {"all", "by"},
            usage = "[-all|-by] <player>", permission = "punishments.command.lookup", completer = "lookupCompleter")
    public void lookupCommand(OnlineSession sender, CommandContext context) {
        boolean allFlag = context.hasFlag("all");
        boolean byFlag = context.hasFlag("by");
        OfflineSession playerParam = this.helper.parseParamSession(context, 0);

        if (playerParam == null) {
            throw new CommandException("Nie udalo sie odczytac gracza");
        }
    }

    public List<String> lookupCompleter(Session<OnlineSession> sender, CommandContext context) {
        return this.helper.playersCompleter();
    }

    @CommandInfo(name = {"punishment", "ppunishment"}, description = "Show punishment record details", min = 1,
            usage = "<punishment-id>", permission = "punishments.command.punishment")
    public void punishmentCommand(final OnlineSession sender, CommandContext context) {
        final int punishmentParam = this.helper.parseParamPunishment(context, 0);

        sender.sendInfo("Pobieranie informacji z bazy danych...");

        this.plugin.getStorage().query(DBQuery.SELECT_PUNISHMENT_BY_ID, punishmentParam).forEach(new QueryCallback() {
            @Override
            public void onResult(ResultSet result, int index, int count) {
                Punishment punishment = GeneralCommands.this.plugin.createPunishment(result);

                sender.sendError(" ===== Rekord dla kary #" + punishment.getId() + " ===== ");
                sender.send(this.keyValue("Kara", punishment.getPunishmentName() +
                        " (" + punishment.getType() + ")"));
                sender.send(this.keyValue("Stworzona", punishment.getCreation()));
                sender.send(this.keyValue("Wygasa", punishment.getExpires()));
                sender.send(this.keyValue("Gracz", punishment.getPlayerId()));
                sender.send(this.keyValue("Karajacy", punishment.getPunisherId()));
                sender.send(this.keyValue("Powod", punishment.getReason()));
                sender.send(this.keyValue("Serwer", punishment.getServerName()));
            }

            private String keyValue(String key, Object value) {
                return ChatColor.YELLOW + key + ": " + ChatColor.YELLOW + value.toString();
            }
        }).result(new QueryCallback() {
            @Override
            public void onResult(ResultSet resultSet, int index, int count) {
                if (count == 0) {
                    sender.sendError("Kara o podanym ID (#" + punishmentParam + ") nie istnieje.");
                }
            }
        }).submit();
    }
}
