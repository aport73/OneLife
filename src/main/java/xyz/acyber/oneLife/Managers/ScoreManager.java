package xyz.acyber.oneLife.Managers;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import xyz.acyber.oneLife.Main;

import java.util.ArrayList;
import java.util.List;

public class ScoreManager {

    static MobManager mm;
    static RaceManager rm;
    static CommandManager cm;
    static Main main;

    public ScoreManager(Main plugin) {
        main = plugin;
        mm = main.mm;
        rm = main.rm;
        cm = main.cm;
    }

    public void blocksPlaced(BlockPlaceEvent event) {
        //Update Players Blocks Placed
        FileConfiguration config = main.getPlayerConfig();
        Player player = event.getPlayer();
        //Update Total Blocks Placed Count
        config.set(player.getUniqueId() + ".playerBlocksPlaced", config.getInt(player.getUniqueId() + ".playerBlocksPlaced") + 1);
        //Update Specific Blocks Placed Count
        config.set(player.getUniqueId() + ".player" + event.getBlock().getType().name() + "Placed", config.getInt(player.getUniqueId() + ".player" + event.getBlock().getType().name() + "Placed") + 1);
        main.savePlayerConfig(config);
    }

    public void blocksMined(BlockBreakEvent event) {
        //Update Players Blocks Placed
        FileConfiguration config = main.getPlayerConfig();
        Player player = event.getPlayer();
        //Update Total Blocks Placed Count
        config.set(player.getUniqueId() + ".playerBlocksMined", config.getInt(player.getUniqueId() + ".playerBlocksMined") + 1);
        //Update Specific Blocks Placed Count
        config.set(player.getUniqueId() + ".player" + event.getBlock().getType().name() + "Mined", config.getInt(player.getUniqueId() + ".player" + event.getBlock().getType().name() + "Mined") + 1);
        main.savePlayerConfig(config);
    }

    public void playerAdvanced(PlayerAdvancementDoneEvent event) {
        FileConfiguration config = main.getPlayerConfig();
        Player player = event.getPlayer();
        config.set(player.getUniqueId() + ".playerAdvancements", config.getInt(player.getUniqueId() + ".playerAdvancements") + 1);
        main.savePlayerConfig(config);
    }

    public void playerScore(CommandSourceStack stack, OfflinePlayer player, Boolean showInChat) {
        ConfigurationSection scoring = main.getConfig().getConfigurationSection("Scoring");
        ConfigurationSection playerScore = main.getPlayerConfig().getConfigurationSection(player.getUniqueId().toString());

        assert scoring != null;

        assert playerScore != null;
        double Death = player.getStatistic(Statistic.DEATHS) * scoring.getDouble("Death");
        double Xp = playerScore.getInt("playerTotalXp") * scoring.getDouble("Xp");
        double OnlineHr = (double) ((player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) / 60) / 60 * scoring.getDouble("OnlineHr");
        double BlocksPlaced = playerScore.getInt("playerBlocksPlaced") * scoring.getDouble("BlocksPlaced");
        double BlocksMined = playerScore.getInt("playerBlocksMined") * scoring.getDouble("BlocksMined");
        double Achevements = playerScore.getInt("playerAdvancements") * scoring.getDouble("Achevements");
        double TotalPoints = Death + Xp + OnlineHr + BlocksPlaced + BlocksMined + Achevements;
        List<String> MobMessages = new ArrayList<>();
        for (String key : scoring.getConfigurationSection("MobKills").getKeys(false)) {
            double MobPoints = player.getStatistic(Statistic.KILL_ENTITY, EntityType.valueOf(key)) * scoring.getConfigurationSection("MobKills").getDouble(key);
            TotalPoints = TotalPoints + MobPoints;
            MobMessages.add(StringUtils.capitalize(key.toLowerCase()) + " Killed: " + player.getStatistic(Statistic.KILL_ENTITY, EntityType.valueOf(key)) + ", Points: " + MobPoints);
        }
        if (showInChat) {
            stack.getSender().sendMessage(player.getName() + "'s Scorecard:");
            stack.getSender().sendMessage("Total Points: " + TotalPoints);
            stack.getSender().sendMessage("Deaths: " + player.getStatistic(Statistic.DEATHS) + ", Points: " + Death);
            stack.getSender().sendMessage("Xp: " + playerScore.getInt("playerTotalXp") + ", Points: " + Xp);
            stack.getSender().sendMessage("OnlineHr: " + (double) ((player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) / 60) / 60 + ", Points: " + OnlineHr);
            if (!MobMessages.isEmpty()) {
                for (String msg : MobMessages) {
                    stack.getSender().sendMessage(msg);
                }
            }
            stack.getSender().sendMessage("Blocks Placed: " + playerScore.getInt("playerBlocksPlaced") + ", Points: " + BlocksPlaced);
            stack.getSender().sendMessage("Blocks Mined: " + playerScore.getInt("playerBlocksMined") + ", Points: " + BlocksMined);
            stack.getSender().sendMessage("Achevements: " + playerScore.getInt("playerAdvancements") + ", Points: " + Achevements);
            stack.getSender().sendMessage("------- END -----");
        } else {
            String path = "Score";
            main.logToFile(player.getName() + "'s Scorecard:", path);
            main.logToFile("Total Points: " + TotalPoints, path);
            main.logToFile("Deaths: " + player.getStatistic(Statistic.DEATHS) + ", Points: " + Death, path);
            main.logToFile("Xp: " + playerScore.getInt("playerTotalXp") + ", Points: " + Xp, path);
            main.logToFile("OnlineHr: " + (double) ((player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) / 60) / 60 + ", Points: " + OnlineHr, path);
            if (!MobMessages.isEmpty()) {
                for (String msg : MobMessages) {
                    main.logToFile(msg, path);
                }
            }
            main.logToFile("Blocks Placed: " + playerScore.getInt("playerBlocksPlaced") + ", Points: " + BlocksPlaced, path);
            main.logToFile("Blocks Mined: " + playerScore.getInt("playerBlocksMined") + ", Points: " + BlocksMined, path);
            main.logToFile("Achevements: " + playerScore.getInt("playerAdvancements") + ", Points: " + Achevements, path);
            main.logToFile("------- END -----", path);
        }
    }

}
