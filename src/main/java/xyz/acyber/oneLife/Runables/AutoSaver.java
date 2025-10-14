package xyz.acyber.oneLife.Runables;

import org.bukkit.scheduler.BukkitRunnable;
import xyz.acyber.oneLife.OneLifePlugin;

public class AutoSaver extends BukkitRunnable {

    private final OneLifePlugin plugin;
    long autoSaveInterval;

    public AutoSaver(OneLifePlugin plugin) {
        this.plugin = plugin;
        autoSaveInterval = plugin.settings.getAutoSaveIntervalSeconds() * 20L;
    }

    @Override
    public void run() {
        plugin.saveSettings();
        plugin.savePlayerScores();
    }
}
