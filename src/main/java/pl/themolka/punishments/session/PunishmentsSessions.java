package pl.themolka.punishments.session;

import org.bukkit.command.CommandException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.themolka.commons.command.CommandContext;
import pl.themolka.commons.command.CommandInfo;
import pl.themolka.commons.session.Sessions;
import pl.themolka.commons.storage.Query;
import pl.themolka.commons.storage.QueryCallback;
import pl.themolka.punishments.PunishmentsPlugin;
import pl.themolka.punishments.command.CommandHelper;

import java.sql.ResultSet;
import java.util.List;

public class PunishmentsSessions extends Sessions<OnlineSession> implements Listener {
    private final PunishmentsPlugin plugin;

    private final CommandHelper helper;

    public PunishmentsSessions(PunishmentsPlugin plugin) {
        this.plugin = plugin;

        this.helper = new CommandHelper(plugin);

        plugin.getCommons().getCommands().registerCommandObject(this);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final OnlineSession session = new OnlineSession(this.plugin, event.getPlayer());
        this.insertSession(session);

        session.getClient().push(this.plugin.getStorage()).result(new QueryCallback() {
            @Override
            public void onResult(ResultSet result, int index, int count) {
                session.push(PunishmentsSessions.this.plugin.getStorage()).handle();
                session.fetch(PunishmentsSessions.this.plugin.getStorage()).handle();
            }
        }).submit();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.removeSession(this.getSession(event.getPlayer().getUniqueId()));
    }

    @CommandInfo(name = {"usernamehistory", "username-history"}, description = "Show username history",
            min = 1, usage = "<player>", permission = "punishments.command.history", completer = "usernameHistoryCompleter")
    public void usernameHistoryCommand(final OnlineSession sender, final CommandContext context) {
        final OfflineSession playerParam = this.helper.parseParamSession(context, 0);
        if (playerParam == null) {
            throw new CommandException("Nie udalo sie odczytac gracza");
        }

        sender.sendInfo("Pobieranie informacji z bazy danych...");

        QueryCallback callback = new QueryCallback() {
            @Override
            public void onResult(ResultSet result, int index, int count) {
                if (!playerParam.isFetched()) {
                    playerParam.fetch(PunishmentsSessions.this.plugin.getStorage()).handle();
                }

                sender.sendError(" ===== Rekord historii nazw dla " + playerParam.getUsername() + " ===== ");

                String lastUsername = null;
                for (OfflineSession.SessionPart part : playerParam.getHistory()) {
                    if (lastUsername == null) {
                        lastUsername = part.getUsername();
                    }

                    if (lastUsername.equals(part.getUsername())) {
                        continue;
                    }

                    sender.sendInfo(this.info(part));
                    lastUsername = part.getUsername();
                }
            }

            private String info(OfflineSession.SessionPart part) {
                return "- " + part.getCreation().toString() + " jako " + part.getUsername() + " na " + part.getServerName();
            }
        };

        Query clientQuery = playerParam.getClient().fetch(this.plugin.getStorage());
        if (clientQuery != null) {
            clientQuery.result(callback).submit();
        } else {
            callback.onResult(null, 0, 0);
        }
    }

    public List<String> usernameHistoryCompleter(OnlineSession sender, CommandContext context) {
        return this.helper.playersCompleter();
    }
}
