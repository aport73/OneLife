package xyz.acyber.oneLife;

import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Dictionary;
import java.util.List;

public class ScoreData {

    public OfflinePlayer player;
    public String team;
    public double deaths;
    public double deathPoints;
    public double adventureDeaths;
    public double adventureDeathPoints;
    public double xp;
    public double xpPoints;
    public double survivalHr;
    public double adventureHr;
    public double afkHr;
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
    public double afkPointsOffset;


    public Dictionary<String, List<Double>> typeMobs;
    public Dictionary<String, List<Double>> typeBlocksMined;
    public Dictionary<String, List<Double>> typeBlocksPlaced;
    public Dictionary<String, List<Double>> typeItemsHarvested;
    public Dictionary<String, List<Double>> typeItemsCaught;

    public double totalPoints() {
        return round(deathPoints + adventureDeathPoints + afkPointsOffset + xpPoints + onlineHrPoints + blocksPlacedPoints + blocksMinedPoints + harvestedPoints +
                caughtPoints + achievementPoints + typeMobTotalPoints + typeBlocksMinedTotalPoints +
                typeBlocksPlacedTotalPoints + typeItemsHarvestedTotalPoints + typeItemsCaughtTotalPoints, 5);
    }

    public double onlineHr() {
        return round(survivalHr + adventureHr + afkHr, 2);
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}

