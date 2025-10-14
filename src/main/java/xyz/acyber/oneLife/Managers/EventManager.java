package xyz.acyber.oneLife.Managers;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;
import xyz.acyber.oneLife.DataObjects.Settings;
import xyz.acyber.oneLife.Events.HasBecomeDayEvent;
import xyz.acyber.oneLife.Events.HasBecomeNightEvent;
import xyz.acyber.oneLife.OneLifePlugin;

public class EventManager implements Listener {

    private final OneLifePlugin OLP;
    private final MobManager mm;
    private final RaceManager rm;
    private final ScoreManager sm;
    private final LivesManager lm;
    private final LuckPerms lpAPI;
    private final Settings settings;

    public EventManager(OneLifePlugin plugin) {
        OLP = plugin;
        lpAPI = OLP.lpAPI;
        mm = OLP.mm;
        rm = OLP.rm;
        sm = OLP.sm;
        lm = OLP.lm;
        settings = OLP.settings;
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        OLP.initialisePlayer(player);
        player.sendMessage(Component.text("Hello, " + player.getName() + "!"));
        if (settings.getEnabledFeatures().getEnabledRaceManager())
            rm.onPlayerJoin(event);
        if (settings.getEnabledFeatures().getEnabledLivesManager())
            lm.setPlayerGameMode(event.getPlayer());
        if (settings.getEnabledFeatures().getEnabledAFKChecker()) {
            if(!player.hasPermission("OneLife.AFK.Bypass"))
                OLP.afkLastInput.put(player.getUniqueId(),System.currentTimeMillis());
        }
    }

    @EventHandler
    public void nightFall(HasBecomeNightEvent event) {
        OLP.setNight(true);
    }

    @EventHandler
    public void dayBreak(HasBecomeDayEvent event) {
        OLP.setNight(false);
    }

    @EventHandler
    public void playerHarvest(PlayerHarvestBlockEvent ev) {
        if (settings.getEnabledFeatures().getEnabledScoreManager())
            sm.playerHarvest(ev);
    }

    @EventHandler
    public void playerFish(PlayerFishEvent ev) {
        if (settings.getEnabledFeatures().getEnabledScoreManager())
            sm.playerFish(ev);
    }

    @EventHandler
    public void blockPlaced(BlockPlaceEvent event) {
        //Check if GamemodeScoreMultipliers Enabled and Run Function if So
        if (settings.getEnabledFeatures().getEnabledScoreManager())
            sm.blocksPlaced(event);
    }

    @EventHandler
    public void blockMined(BlockBreakEvent event) {
        //Check if GamemodeScoreMultipliers Enabled and Run Function if So
        if (settings.getEnabledFeatures().getEnabledScoreManager())
            sm.blocksMined(event);
    }

    @EventHandler
    public void playerAdvanced(PlayerAdvancementDoneEvent event) {
        if (settings.getEnabledFeatures().getEnabledScoreManager())
            sm.playerAdvanced(event);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent ev) {
        if (settings.getEnabledFeatures().getEnabledRaceManager())
            rm.onPlayerMove(ev);
        if (settings.getEnabledFeatures().getEnabledAFKChecker())
            OLP.afkUpdater(ev.getPlayer());
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        if (settings.getEnabledFeatures().getEnabledAFKChecker())
            OLP.afkUpdater(event.getPlayer());
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (settings.getEnabledFeatures().getEnabledMobManager())
            mm.onEntityDeath(event);
        if (settings.getEnabledFeatures().getEnabledScoreManager())
            sm.entityKilled(event);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (settings.getEnabledFeatures().getEnabledRaceManager())
            rm.onDeath(event);
        if (settings.getEnabledFeatures().getEnabledScoreManager())
            sm.playerDeath(event);
        if (settings.getEnabledFeatures().getEnabledAFKChecker())
            OLP.afkUpdater(event.getEntity());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (settings.getEnabledFeatures().getEnabledRaceManager())
            rm.applyRace(event.getPlayer(), null);
        if (settings.getEnabledFeatures().getEnabledLivesManager())
            lm.setPlayerGameMode(event.getPlayer());
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (settings.getEnabledFeatures().getEnabledMobManager())
            mm.onEntitySpawn(event);
    }

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        if (settings.getEnabledFeatures().getEnabledLivesManager())
            if (!event.getPlayer().isOp() && lm.getPlayerLivesRemaining(event.getPlayer()) <= 0)
                lm.setPlayerGameMode(event.getPlayer());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (settings.getEnabledFeatures().getEnabledRaceManager())
            rm.onDamage(event);
    }

    @EventHandler
    public void playerItemConsume(PlayerItemConsumeEvent event) {
        if (settings.getEnabledFeatures().getEnabledRaceManager())
            rm.playerItemConsume(event);
    }

    @EventHandler
    public void playerTamePet(EntityTameEvent event) {
        if (settings.getEnabledFeatures().getEnabledRaceManager())
            rm.playerTamePet(event);
    }

    @EventHandler
    public void playerArmorChange(PlayerArmorChangeEvent event) {
        if (settings.getEnabledFeatures().getEnabledRaceManager())
            rm.playerArmorChange(event);
    }

    @EventHandler
    public void entityPickupEvent(EntityPickupItemEvent event) {
        if (settings.getEnabledFeatures().getEnabledRaceManager())
            rm.entityPickupEvent(event);
    }

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent event) {
        if (settings.getEnabledFeatures().getEnabledRaceManager())
            rm.inventoryClickEvent(event);
    }

    @EventHandler
    public void playerDropItem(PlayerDropItemEvent event) {
        if (settings.getEnabledFeatures().getEnabledRaceManager())
            rm.playerDropItem(event);
    }

    @EventHandler
    public void playerDisconnect(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (settings.getEnabledFeatures().getEnabledAFKChecker()) {
            User user = lpAPI.getUserManager().getUser(player.getUniqueId());
            assert user != null;
            Group afk = lpAPI.getGroupManager().getGroup("afk");
            assert afk != null;
            user.data().remove(InheritanceNode.builder(afk).build());
            lpAPI.getUserManager().saveUser(user);
            OLP.afkLastInput.remove(player.getUniqueId());
        }
    }
}
