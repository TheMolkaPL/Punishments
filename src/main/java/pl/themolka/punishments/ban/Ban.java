package pl.themolka.punishments.ban;

import pl.themolka.punishments.punishment.Punishment;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Ban extends Punishment {
    public static final int PUNISHMENT_TYPE = 0;

    public Ban() {
    }

    public Ban(ResultSet result) throws SQLException {
        super(result);
    }

    @Override
    public String getPunishmentName() {
        return this.expires() ? "Temporary Ban" : "Permanent Ban";
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
//        player.kickPlayer("You were " + this.getPunishmentName() + "ned: " + this.getReason());
    }
}
