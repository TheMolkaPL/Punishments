package pl.themolka.punishments.mute;

import pl.themolka.punishments.PunishmentsPlugin;
import pl.themolka.punishments.punishment.PunishmentHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MuteHandler extends PunishmentHandler<Mute> {
    public MuteHandler(PunishmentsPlugin plugin) {
        super(plugin);
    }

    @Override
    public Mute createPunishment(ResultSet result) throws SQLException {
        return new Mute(result);
    }

    @Override
    public int getPunishmentType() {
        return Mute.PUNISHMENT_TYPE;
    }
}
