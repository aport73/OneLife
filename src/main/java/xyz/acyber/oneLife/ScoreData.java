package xyz.acyber.oneLife;

import net.luckperms.api.LuckPermsProvider;
import org.bukkit.OfflinePlayer;

import java.util.Comparator;
import java.util.Dictionary;
import java.util.List;

public class ScoreData {

    public OfflinePlayer player;
    public String team;
    public double deaths;
    public double deathPoints;
    public double xp;
    public double xpPoints;
    public double onlineHr;
    public double onlineHrPoints;
    public double blocksPlaced;
    public double blocksPlacedPoints;
    public double blocksMined;
    public double blocksMinedPoints;
    public double harvested;
    public double harvestedPoints;
    public double caught;
    public double caughtPoints;
    public double achievements;
    public double achievementPoints;
    public double typeMobTotalPoints;
    public double typeBlocksMinedTotalPoints;
    public double typeBlocksPlacedTotalPoints;
    public double typeItemsHarvestedTotalPoints;
    public double typeItemsCaughtTotalPoints;

    public Dictionary<String, List<Double>> typeMobs;
    public Dictionary<String, List<Double>> typeBlocksMined;
    public Dictionary<String, List<Double>> typeBlocksPlaced;
    public Dictionary<String, List<Double>> typeItemsHarvested;
    public Dictionary<String, List<Double>> typeItemsCaught;

    public double totalPoints() {
        return deathPoints + xpPoints + onlineHrPoints + blocksPlacedPoints + blocksMinedPoints + harvestedPoints +
                caughtPoints + achievementPoints + typeMobTotalPoints + typeBlocksMinedTotalPoints +
                typeBlocksPlacedTotalPoints + typeItemsHarvestedTotalPoints + typeItemsCaughtTotalPoints;
    }
}

