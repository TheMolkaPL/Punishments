package pl.themolka.punishments.punishment;

import pl.themolka.commons.event.Event;

public class PunishmentEvent extends Event {
    private final Punishment punishment;

    public PunishmentEvent(Punishment punishment) {
        this.punishment = punishment;
    }

    public Punishment getPunishment() {
        return this.punishment;
    }
}
