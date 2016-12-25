package pl.themolka.punishments.kick;

import pl.themolka.punishments.PunishmentsPlugin;
import pl.themolka.punishments.punishment.PunishmentHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class KickHandler extends PunishmentHandler<Kick> {
    public KickHandler(PunishmentsPlugin plugin) {
        super(plugin);
    }

    @Override
    public Kick createPunishment(ResultSet result) throws SQLException {
        return new Kick(result);
    }

    @Override
    public int getPunishmentType() {
        return Kick.PUNISHMENT_TYPE;
    }
}
