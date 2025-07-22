package xyz.acyber.oneLife.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HasBecomeDayEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean day;
    private boolean cancelled;

    public HasBecomeDayEvent(boolean isDay) {
        this.day = isDay;
    }

    public boolean isDay() {
        return this.day;
    }

    public void setNight(boolean isDay) {
        this.day = isDay;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

}
