package pl.themolka.punishments.command;

import org.bukkit.entity.Player;
import pl.themolka.commons.command.CommandContext;
import pl.themolka.commons.command.CommandException;
import pl.themolka.punishments.PunishmentsPlugin;
import pl.themolka.punishments.session.OfflineSession;
import pl.themolka.punishments.session.OnlineSession;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandHelper {
    private final PunishmentsPlugin plugin;

    public CommandHelper(PunishmentsPlugin plugin) {
        this.plugin = plugin;
    }

    public int parseFlagInt(CommandContext context, String flag, int format) {
        return this.parseFlagInt(context, flag, format, format);
    }

    public int parseFlagInt(CommandContext context, String flag, int def, int format) {
        try {
            return context.getFlagInt(flag, def);
        } catch (NumberFormatException ex) {
            return format;
        }
    }

    public int parseFlagPunishment(CommandContext context, String flag) {
        return this.parsePunishment(this.parseFlagInt(context, flag, Integer.MAX_VALUE));
    }

    public OfflineSession parseFlagSession(CommandContext context, String flag) {
        return this.parseSession(context.getFlag(flag));
    }

    public int parseParamInt(CommandContext context, int index, int format) {
        return this.parseParamInt(context, index, format, format);
    }

    public int parseParamInt(CommandContext context, int index, int def, int format) {
        try {
            return context.getParamInt(index, def);
        } catch (NumberFormatException ex) {
            return format;
        }
    }

    public int parseParamPunishment(CommandContext context, int index) {
        return this.parsePunishment(this.parseParamInt(context, index, Integer.MAX_VALUE));
    }

    public OfflineSession parseParamSession(CommandContext context, int index) {
        return this.parseSession(context.getParam(index));
    }

    public int parsePunishment(int id) {
        if (id != Integer.MAX_VALUE) {
            return id;
        }

        throw new CommandException("ID kary musi byc liczba dodatnia");
    }

    public OfflineSession parseSession(String session) {
        if (session.length() <= 16) {
            return this.parseSessionUsername(session);
        } else if (session.length() == 36) {
            return this.parseSessionUuid(session);
        }

        return null;
    }

    public OfflineSession parseSessionUsername(String username) {
        if (username.length() > 16) {
            return null;
        }

        OnlineSession online = (OnlineSession) this.plugin.getCommons().getSessions().getSession(username.toLowerCase());
        if (online != null) {
            return online;
        }

        OfflineSession session = new OfflineSession(this.plugin);
        session.setUsernameLower(username.toLowerCase());
        return session;
    }

    public OfflineSession parseSessionUuid(String uuid) {
        try {
            return this.parseSessionUuid(UUID.fromString(uuid));
        } catch (IllegalArgumentException ex) {
            throw new CommandException("Nie udalo sie przetworzyc podanego identyfikatora UUID");
        }
    }

    public OfflineSession parseSessionUuid(UUID uuid) {
        OnlineSession online = (OnlineSession) this.plugin.getCommons().getSessions().getSession(uuid);
        if (online != null) {
            return online;
        }

        return new OfflineSession(this.plugin, uuid);
    }

    public List<String> playersCompleter() {
        List<String> results = new ArrayList<>();
        for (Player online : this.plugin.getServer().getOnlinePlayers()) {
            results.add(online.getName());
            results.add(online.getUniqueId().toString());
        }

        return results;
    }
}
