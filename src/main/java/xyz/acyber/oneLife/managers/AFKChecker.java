package xyz.acyber.oneLife.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import xyz.acyber.oneLife.Main;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class AFKChecker extends BukkitRunnable {

    private final Main plugin;
    private final HashMap<UUID,Long> lastInput;
    private final double afkTime;
    MiniMessage mm = MiniMessage.miniMessage();

    public AFKChecker(@NotNull Main plugin, HashMap<UUID, Long> AKFLastInput) {
        this.plugin = plugin;
        this.lastInput = AKFLastInput;

        afkTime = plugin.getConfig().getDouble("AFK.minutesAFK") * 60000;
    }

    @Override
    public void run() {
        plugin.sendMsgOps("AFKChecker: running checker");
        for (Player p: Bukkit.getOnlinePlayers()) {
            long time = System.currentTimeMillis();

            if(!lastInput.containsKey(p.getUniqueId())){
                if(p.hasPermission("OneLife.AFK.Bypass")){
                    return;
                }
                lastInput.put(p.getUniqueId(),time);
            }

            long lastIn = lastInput.get(p.getUniqueId());
            long interval = time - lastIn;
            double test = afkTime - interval;

            if(plugin.getConfig().getLong("AFK.secondsInterval") == 1) {
                if (test > 4000 && test < 5001) {
                    p.sendMessage(kickIn("5"));
                    return;
                }

                if (test > 3000 && test < 4001) {
                    p.sendMessage(kickIn("4"));
                    return;
                }

                if (test > 2000 && test < 3001) {
                    p.sendMessage(kickIn("3"));
                    return;
                }

                if (test > 1000 && test < 2001) {
                    p.sendMessage(kickIn("2"));
                    return;
                }

                if (test > 0 && test < 1001) {
                    p.sendMessage(kickIn("1"));
                    return;
                }
            }

            if(interval > afkTime){
                if(plugin.getConfig().getLong("AFK.secondsInterval") > 1) {
                    lastInput.remove(p.getUniqueId());

                    AtomicInteger count = new AtomicInteger(5);
                    Bukkit.getScheduler().runTaskTimer(plugin, task -> {
                        p.sendMessage(kickIn(String.valueOf(count.getAndDecrement())));
                        if(count.get() == 0) task.cancel();
                    }, 0, 20);

                    Bukkit.getScheduler().runTaskLater(plugin,() -> p.kick(mm.deserialize(Objects.requireNonNull(plugin.getConfig().getString("AFK.kicked")))) ,5*20);
                    return;
                }
                lastInput.remove(p.getUniqueId());

                p.kick(mm.deserialize(Objects.requireNonNull(plugin.getConfig().getString("AFK.kicked"))));
            }
        }
    }

    private @NotNull Component kickIn(String seconds){
        String msg = plugin.getConfig().getString("AFK.kickIn");
        assert msg != null;
        msg = msg.replace("%seconds%",seconds);
        return mm.deserialize(msg);

    }

}
