package xyz.acyber.oneLife.Runables;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.acyber.oneLife.OneLifePlugin;
import xyz.acyber.oneLife.Events.HasBecomeDayEvent;
import xyz.acyber.oneLife.Events.HasBecomeNightEvent;

public class DayNightChecker extends BukkitRunnable {

    private final OneLifePlugin plugin;
    public boolean isNight = false;

    public DayNightChecker(OneLifePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        World world = plugin.getServer().getWorlds().getFirst();
        long time = world.getTime();
        if (time > 13000 && !isNight) {
            isNight = true;
            callHasBecomeNightEvent();
        }
        if (time < 13000 && isNight) {
            isNight = false;
            callHasBecomeDayEvent();
        }
    }

    public void callHasBecomeNightEvent() {
        HasBecomeNightEvent nightEvent = new HasBecomeNightEvent(true);
        Bukkit.getScheduler().runTask(plugin, nightEvent::callEvent);
    }

    public void callHasBecomeDayEvent() {
        HasBecomeDayEvent dayEvent = new HasBecomeDayEvent(true);
        Bukkit.getScheduler().runTask(plugin, dayEvent::callEvent);
    }

}
