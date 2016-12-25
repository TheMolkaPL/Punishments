package pl.themolka.punishments.kick;

import pl.themolka.punishments.punishment.Punishment;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Kick extends Punishment {
    public static final int PUNISHMENT_TYPE = 1;

    public Kick() {
    }

    public Kick(ResultSet result) throws SQLException {
        super(result);
    }

    @Override
    public String getPunishmentName() {
        return "Kick";
    }

    @Override
    public int getPunishmentType() {
        return PUNISHMENT_TYPE;
    }

    @Override
    public void onHandle() {
//        Player player = Bukkit.getPlayer(this.getPlayer());
//        if (player == null) {
//            return;
//        }
//
//        player.kickPlayer("You were " + this.getPunishmentName() + "ed: " + this.getReason());
    }
}
