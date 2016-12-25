package pl.themolka.punishments.ban;

import org.bukkit.command.CommandException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import pl.themolka.commons.command.CommandContext;
import pl.themolka.commons.command.CommandInfo;
import pl.themolka.commons.storage.QueryCallback;
import pl.themolka.punishments.PunishmentsPlugin;
import pl.themolka.punishments.database.DBQuery;
import pl.themolka.punishments.punishment.Punishment;
import pl.themolka.punishments.punishment.PunishmentHandler;
import pl.themolka.punishments.session.OfflineSession;
import pl.themolka.punishments.session.OnlineSession;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BanHandler extends PunishmentHandler<Ban> {
    public BanHandler(PunishmentsPlugin plugin) {
        super(plugin);
    }

    @Override
    public Ban createPunishment(ResultSet result) throws SQLException {
        return new Ban(result);
    }

    @Override
    public int getPunishmentType() {
        return Ban.PUNISHMENT_TYPE;
    }

    @Override
    public void onStart() {
        if (this.plugin.getConfig().getBoolean("bans.login-listener", true)) {
            this.registerAdditional(new LoginListener());
        }
    }

    @CommandInfo(name = {"ban", "pban"}, description = "Ban player permanent", min = 1,
            usage = "<player> [reason]", permission = "punishments.command.ban", completer = "banCompleter")
    public void banCommand(OnlineSession sender, CommandContext context) {
        OfflineSession playerParam = this.helper.parseParamSession(context, 0);
        String reasonParam = context.getParams(1);

        if (playerParam == null) {
            throw new CommandException("Nie udalo sie odczytac gracza");
        }
    }

    public List<String> banCompleter(OnlineSession sender, CommandContext context) {
        return this.helper.playersCompleter();
    }

    @CommandInfo(name = {"temp", "ptemp"}, description = "Ban player temporary", min = 2,
            usage = "<player> <duration> [reason]", permission = "punishments.command.temp", completer = "tempCompleter")
    public void tempCommand(OnlineSession sender, CommandContext context) {
        OfflineSession playerParam = this.helper.parseParamSession(context, 0);
        String durationParam = context.getParam(1);
        String reasonParam = context.getParams(2);

        if (playerParam == null) {
            throw new CommandException("Nie udalo sie odczytac gracza");
        }
    }

    public List<String> tempCompleter(OnlineSession sender, CommandContext context) {
        List<String> results = new ArrayList<>();
        switch (context.getArgs().length) {
            case 1: return this.helper.playersCompleter();
            case 2: return Arrays.asList("m", "h", "d", "w");
            case 3: return this.helper.playersCompleter();
        }

        return results;
    }

    private class LoginListener implements Listener {
        @EventHandler
        public void onAsyncPlayerPreLogin(final AsyncPlayerPreLoginEvent event) {
            BanHandler.this.plugin.getStorage().query(DBQuery.SELECT_ACTIVE_PUNISHMENTS_FOR,
                    event.getUniqueId().toString()
            ).forEach(new QueryCallback() {
                @Override
                public void onResult(ResultSet result, int index, int count) {
                    Punishment punishment = BanHandler.this.plugin.createPunishment(result);
                    if (punishment.getType() != BanHandler.this.getPunishmentType()) {
                        return;
                    }

                    if (punishment.isActive()) {
                        String reason = String.format("You are banned from the server.\n%s\nReason: %s",
                                punishment.getPunishmentName(),
                                punishment.getReason());

                        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, reason);
                    }
                }
            }).handle(); // sync
        }
    }
}
