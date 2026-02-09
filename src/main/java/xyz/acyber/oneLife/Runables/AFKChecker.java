package xyz.acyber.oneLife.Runables;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import xyz.acyber.oneLife.OneLifePlugin;
import xyz.acyber.oneLife.Managers.ScoreManager;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class AFKChecker extends BukkitRunnable {

    private final OneLifePlugin plugin;
    private final HashMap<UUID,Long> lastInput;
    private final double afkTime;
    private final Group afk;
    private final LuckPerms lpAPI;
    ScoreManager sm;
    MiniMessage mm = MiniMessage.miniMessage();

    public AFKChecker(@NotNull OneLifePlugin plugin, HashMap<UUID, Long> AKFLastInput, Group afk, LuckPerms lpAPI, ScoreManager sm) {
        this.plugin = plugin;
        this.lastInput = AKFLastInput;
        this.afk = afk;
        this.lpAPI = lpAPI;
        this.sm = sm;

        afkTime = plugin.getConfig().getDouble("AFK.minutesAFK") * 60000;
    }

    @Override
    public void run() {
        for (Player p: Bukkit.getOnlinePlayers()) {
            sm.timeOnline(p);
            long time = System.currentTimeMillis();
            User user = lpAPI.getUserManager().getUser(p.getUniqueId());
            if (user == null) continue;
            InheritanceNode node = InheritanceNode.builder(afk).build();

            Long lastInValue = lastInput.get(p.getUniqueId());
            if (lastInValue == null) {
                lastInput.put(p.getUniqueId(), time);
                continue;
            }
            long lastIn = lastInValue;
            long interval = time - lastIn;
            double test = afkTime - interval;

            if(plugin.getConfig().getLong("AFK.secondsInterval") == 1) {
                if (test > 4000 && test < 5001) {
                    p.sendMessage(afkWarning("5"));
                    return;
                }

                if (test > 3000 && test < 4001) {
                    p.sendMessage(afkWarning("4"));
                    return;
                }

                if (test > 2000 && test < 3001) {
                    p.sendMessage(afkWarning("3"));
                    return;
                }

                if (test > 1000 && test < 2001) {
                    p.sendMessage(afkWarning("2"));
                    return;
                }

                if (test > 0 && test < 1001) {
                    p.sendMessage(afkWarning("1"));
                    return;
                }
            }

            if(interval > afkTime) {
                if (!user.getPrimaryGroup().equals(afk.getName())) {
                    p.sendMessage(MiniMessage.miniMessage().deserialize("<bold><red>You are now marked afk"));
                    user.data().add(node);
                    lpAPI.getUserManager().saveUser(user);
                }
            } else {
                if (user.getPrimaryGroup().equals(afk.getName())) {
                    p.sendMessage(MiniMessage.miniMessage().deserialize("<bold><red>You are no longer marked afk"));
                    user.data().remove(node);
                    lpAPI.getUserManager().saveUser(user);
                }
            }
        }
    }

    private @NotNull Component afkWarning(String seconds){
        String msg = plugin.getConfig().getString("AFK.afkWarning");
        assert msg != null;
        msg = msg.replace("%seconds%",seconds);
        return mm.deserialize(msg);

    }

}
