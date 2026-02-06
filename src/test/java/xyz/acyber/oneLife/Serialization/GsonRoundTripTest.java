package xyz.acyber.oneLife.Serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.junit.jupiter.api.Test;
import xyz.acyber.oneLife.DataObjects.PlayerScore;
import xyz.acyber.oneLife.DataObjects.Settings;
import xyz.acyber.oneLife.DataObjects.SubScoreData.MaterialInteractions;
import xyz.acyber.oneLife.DataObjects.SubSettings.Team;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GsonRoundTripTest {

    private Gson createGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(org.bukkit.Material.class, new MaterialTypeAdapter())
                .registerTypeAdapter(org.bukkit.entity.EntityType.class, new EntityTypeAdapter())
                .registerTypeAdapter(org.bukkit.Location.class, new LocationTypeAdapter())
                .create();
    }

    @Test
    void settingsRoundTrip() {
        Settings settings = new Settings();
        settings.setBackupsToKeep(7);
        settings.setLuckPermsEnabled(false);
        settings.setTeams(new HashMap<>());
        Team team = new Team();
        settings.addTeam(team);

        Gson gson = createGson();
        String json = gson.toJson(settings);
        Settings back = gson.fromJson(json, Settings.class);

        assertNotNull(back);
        assertEquals(7, back.getBackupsToKeep());
        assertFalse(back.isLuckPermsEnabled());
        assertTrue(back.getTeams().containsKey(team.getUUID()));
    }

    @Test
    void playerScoreRoundTrip() {
        PlayerScore score = new PlayerScore();
        score.setUUID(UUID.randomUUID());
        score.setPlayerName("TestPlayer");
        score.setLivesBoughtBack(2);

        HashMap<String, Double> deaths = new HashMap<>();
        deaths.put("SURVIVAL", 3.0);
        score.setDeaths(deaths);

        HashMap<Material, MaterialInteractions> placed = new HashMap<>();
        MaterialInteractions interaction = new MaterialInteractions(Material.STONE, new HashMap<>());
        interaction.incrementCount("SURVIVAL");
        placed.put(Material.STONE, interaction);
        score.setTypeBlocksPlaced(placed);

        HashMap<EntityType, xyz.acyber.oneLife.DataObjects.SubScoreData.MobsKilled> mobs = new HashMap<>();
        mobs.put(EntityType.ZOMBIE, new xyz.acyber.oneLife.DataObjects.SubScoreData.MobsKilled(EntityType.ZOMBIE, new HashMap<>()));
        score.setTypeMobsKilled(mobs);

        Gson gson = createGson();
        String json = gson.toJson(score);
        PlayerScore back = gson.fromJson(json, PlayerScore.class);

        assertNotNull(back);
        assertEquals(score.getUUID(), back.getUUID());
        assertEquals("TestPlayer", back.getPlayerName());
        assertEquals(2, back.getLivesBoughtBack());
        assertEquals(3.0, back.getDeaths().get("SURVIVAL"));
        assertTrue(back.getTypeBlocksPlaced().containsKey(Material.STONE));
        assertTrue(back.getTypeMobsKilled().containsKey(EntityType.ZOMBIE));
    }
}

