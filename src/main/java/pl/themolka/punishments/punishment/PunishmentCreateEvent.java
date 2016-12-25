package pl.themolka.punishments.punishment;

import pl.themolka.commons.event.Cancelable;

public class PunishmentCreateEvent extends PunishmentEvent implements Cancelable {
    private boolean cancel;

    public PunishmentCreateEvent(Punishment punishment) {
        super(punishment);
    }

    @Override
    public boolean isCanceled() {
        return this.cancel;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }
}
