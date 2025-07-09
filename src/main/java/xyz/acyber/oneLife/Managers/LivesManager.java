package xyz.acyber.oneLife.Managers;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.lucko.spark.paper.lib.protobuf.SourceContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import xyz.acyber.oneLife.Main;
import org.bukkit.scoreboard.*;

import java.util.Objects;

public class LivesManager {

    static Main main;
    ScoreboardManager scoreboardManager;


    public LivesManager(Main plugin) {
        main = plugin;
    }

    public void enableDeathsScoreboard() {
        if (!main.livesMEnabled) {
            main.livesMEnabled = true;
            main.getConfig().set("Modes.LivesManager", main.livesMEnabled);
            main.saveConfig();
        }

        scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
        Objective deaths = getObjective("deaths", scoreboard);
        if (deaths == null)
            deaths = scoreboard.registerNewObjective("deaths", Criteria.DEATH_COUNT, Component.text("Deaths"), RenderType.INTEGER);
        deaths.setDisplaySlot(DisplaySlot.BELOW_NAME);
        deaths.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        deaths.setAutoUpdateDisplay(true);
    }

    public void disableDeathsScoreboard() {
        if (main.livesMEnabled) {
            main.livesMEnabled = false;
            main.getConfig().set("Modes.LivesManager", main.livesMEnabled);
            main.saveConfig();
        }
        Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
        Objective deaths = getObjective("deaths", scoreboard);
        deaths.unregister();
    }

    public void setLivesCap(int cap) {
        main.getConfig().set("Lives.cap", cap);
        main.saveConfig();
    }

    public int getLivesCap() {
        return main.getConfig().getInt("Lives.cap");
    }

    public String getFinalGameMode() {
        return main.getConfig().getString("Lives.gameModeAfterLastDeath");
    }

    public void setFinalGameMode(GameMode mode) {
        main.getConfig().set("Lives.gameModeAfterLastDeath", mode.toString());
        main.saveConfig();
    }

    public void setPlayerGameMode(Player player) {
        Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
        main.sendMsgOps(String.valueOf(getPlayerScore(getObjective("deaths",scoreboard), player)));
        if (getPlayerScore(getObjective("deaths",scoreboard), player) >= main.getConfig().getInt("Lives.cap")) {
            player.setGameMode(GameMode.valueOf(Objects.requireNonNull(main.getConfig().getString("Lives.gameModeAfterLastDeath")).toUpperCase()));
            player.sendMessage(Component.text("Sorry, You're out of lives! You can continue to play on the server in Adventure Mode"));
        }
        else
            player.setGameMode(GameMode.SURVIVAL);
    }

    public Scoreboard getScoreboard() {
        return scoreboardManager.getMainScoreboard();
    }

    public Objective getObjective(String name, Scoreboard scoreboard) {
        return scoreboard.getObjective(name);
    }

    private int getPlayerScore(Objective objective, Player player) {
        return objective.getScore(player).getScore();
    }

    public void setPlayerScore(Objective objective, Player player, int score) {
        objective.getScore(player).setScore(score);
        setPlayerGameMode(player);
    }

    public void resetPlayerScore(Objective objective, Player player) {
        objective.getScore(player.getName()).resetScore();
        setPlayerGameMode(player);
    }

    public void resetObjective(Objective objective) {
        Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
        String name = objective.getName();
        Criteria criteria = objective.getTrackedCriteria();
        Component displayName = objective.displayName();
        RenderType renderType = objective.getRenderType();

        objective.unregister();
        enableDeathsScoreboard();

        for (Player player : Bukkit.getOnlinePlayers()) {
            setPlayerGameMode(player);
        }
    }
}
