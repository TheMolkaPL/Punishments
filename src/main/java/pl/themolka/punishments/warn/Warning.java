package pl.themolka.punishments.warn;

import pl.themolka.punishments.punishment.Punishment;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Warning extends Punishment {
    public static final int PUNISHMENT_TYPE = 3;

    public Warning() {
    }

    public Warning(ResultSet result) throws SQLException {
        super(result);
    }

    @Override
    public String getPunishmentName() {
        return "Warning";
    }

    @Override
    public int getPunishmentType() {
        return PUNISHMENT_TYPE;
    }
}
