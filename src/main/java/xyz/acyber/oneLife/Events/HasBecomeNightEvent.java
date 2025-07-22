package xyz.acyber.oneLife.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HasBecomeNightEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean night;

    public HasBecomeNightEvent(boolean isNight) {
        this.night = isNight;
    }

    public boolean getIsNight() {
        return this.night;
    }

    public void setIsNight(boolean isNight) {
        this.night = isNight;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

}
