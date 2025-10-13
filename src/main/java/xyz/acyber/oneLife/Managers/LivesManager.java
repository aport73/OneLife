package xyz.acyber.oneLife.Managers;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import xyz.acyber.oneLife.OneLifePlugin;

import java.util.Objects;

import static java.lang.Math.max;

public class LivesManager {

    static OneLifePlugin oneLifePlugin;
    ScoreboardManager scoreboardManager;


    public LivesManager(OneLifePlugin plugin) {
        oneLifePlugin = plugin;
    }

    public void enableDeathsScoreboard() {
        if (!oneLifePlugin.settings.getEnabledFeatures().getEnabledLivesManager()) {
            oneLifePlugin.settings.getEnabledFeatures().setEnabledLivesManager(true);
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
        if (oneLifePlugin.settings.getEnabledFeatures().getEnabledLivesManager()) {
            oneLifePlugin.settings.getEnabledFeatures().setEnabledLivesManager(false);
        }
        Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
        Objective deaths = getObjective("deaths", scoreboard);
        deaths.unregister();
    }

    public void setLivesCap(int cap) {
        oneLifePlugin.getConfig().set("Lives.cap", cap);
        oneLifePlugin.saveConfig();
    }

    public int getLivesCap() {
        return oneLifePlugin.getConfig().getInt("Lives.cap");
    }

    public String getFinalGameMode() {
        return oneLifePlugin.getConfig().getString("Lives.gameModeAfterLastDeath");
    }

    public void setFinalGameMode(GameMode mode) {
        oneLifePlugin.getConfig().set("Lives.gameModeAfterLastDeath", mode.toString());
        oneLifePlugin.saveConfig();
    }

    public int getPlayerLivesRemaining(Player player) {
        Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
        int lives = oneLifePlugin.getConfig().getInt("Lives.cap") - getPlayerScore(getObjective("deaths",scoreboard), player);
        return max(lives, 0);
    }

    public void setPlayerGameMode(Player player) {
        Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
        if (getPlayerLivesRemaining(player) <= 0) {
            GameMode gmAfterDeath = GameMode.valueOf(Objects.requireNonNull(oneLifePlugin.getConfig().getString("Lives.gameModeAfterLastDeath")).toUpperCase());
            if (gmAfterDeath != player.getGameMode() && !player.isOp()) {
                player.setGameMode(gmAfterDeath);
                player.sendMessage(Component.text("Sorry, You're out of lives! You can continue to play on the server in " + gmAfterDeath.name() + " Mode"));
            }
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
        scoreboardManager.getMainScoreboard();
        objective.getName();
        objective.getTrackedCriteria();
        objective.displayName();
        objective.getRenderType();

        objective.unregister();
        enableDeathsScoreboard();

        for (Player player : Bukkit.getOnlinePlayers()) {
            setPlayerGameMode(player);
        }
    }
}
