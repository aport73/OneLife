package xyz.acyber.oneLife.Runables;

import org.bukkit.scheduler.BukkitRunnable;

import xyz.acyber.oneLife.OneLifePlugin;

public class AutoSaver extends BukkitRunnable {

    private final OneLifePlugin plugin;

    public AutoSaver(OneLifePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.saveSettings();
        plugin.savePlayerScores();
    }
}
