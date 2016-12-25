package pl.themolka.punishments.warn;

import pl.themolka.punishments.PunishmentsPlugin;
import pl.themolka.punishments.punishment.PunishmentHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WarningHandler extends PunishmentHandler<Warning> {
    public WarningHandler(PunishmentsPlugin plugin) {
        super(plugin);
    }

    @Override
    public Warning createPunishment(ResultSet result) throws SQLException {
        return new Warning(result);
    }

    @Override
    public int getPunishmentType() {
        return Warning.PUNISHMENT_TYPE;
    }
}
