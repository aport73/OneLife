package xyz.acyber.oneLife.Managers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import xyz.acyber.oneLife.Events.HasBecomeDayEvent;
import xyz.acyber.oneLife.Events.HasBecomeNightEvent;
import xyz.acyber.oneLife.OneLifePlugin;

public class EventManager implements Listener {

    private final OneLifePlugin OLP;

    public EventManager(OneLifePlugin plugin) {
        OLP = plugin;
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        OLP.initialisePlayer(player);
        player.sendMessage(Component.text("Hello, " + player.getName() + "!"));
        if (OLP.settings.getEnabledFeatures().getEnabledRaceManager() && OLP.rm != null)
            OLP.rm.onPlayerJoin(event);
        if (OLP.settings.getEnabledFeatures().getEnabledLivesManager() && OLP.lm != null)
            OLP.lm.setPlayerGameMode(event.getPlayer());
        if (OLP.settings.getEnabledFeatures().getEnabledAFKChecker()) {
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
        if (OLP.settings.getEnabledFeatures().getEnabledScoreManager() && OLP.sm != null)
            OLP.sm.playerHarvest(ev);
    }

    @EventHandler
    public void playerFish(PlayerFishEvent ev) {
        if (OLP.settings.getEnabledFeatures().getEnabledScoreManager() && OLP.sm != null)
            OLP.sm.playerFish(ev);
    }

    @EventHandler
    public void blockPlaced(BlockPlaceEvent event) {
        //Check if GamemodeScoreMultipliers Enabled and Run Function if So
        if (OLP.settings.getEnabledFeatures().getEnabledScoreManager() && OLP.sm != null)
            OLP.sm.blocksPlaced(event);
    }

    @EventHandler
    public void blockMined(BlockBreakEvent event) {
        //Check if GamemodeScoreMultipliers Enabled and Run Function if So
        if (OLP.settings.getEnabledFeatures().getEnabledScoreManager() && OLP.sm != null)
            OLP.sm.blocksMined(event);
    }

    @EventHandler
    public void playerAdvanced(PlayerAdvancementDoneEvent event) {
        if (OLP.settings.getEnabledFeatures().getEnabledScoreManager() && OLP.sm != null)
            OLP.sm.playerAdvanced(event);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent ev) {
        if (OLP.settings.getEnabledFeatures().getEnabledRaceManager() && OLP.rm != null)
            OLP.rm.onPlayerMove(ev);
        if (OLP.settings.getEnabledFeatures().getEnabledAFKChecker())
            OLP.afkUpdater(ev.getPlayer());
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        if (OLP.settings.getEnabledFeatures().getEnabledAFKChecker())
            OLP.afkUpdater(event.getPlayer());
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (OLP.settings.getEnabledFeatures().getEnabledMobManager() && OLP.mm != null)
            OLP.mm.onEntityDeath(event);
        if (OLP.settings.getEnabledFeatures().getEnabledScoreManager() && OLP.sm != null)
            OLP.sm.entityKilled(event);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (OLP.settings.getEnabledFeatures().getEnabledRaceManager() && OLP.rm != null)
            OLP.rm.onDeath(event);
        if (OLP.settings.getEnabledFeatures().getEnabledScoreManager() && OLP.sm != null)
            OLP.sm.playerDeath(event);
        if (OLP.settings.getEnabledFeatures().getEnabledAFKChecker())
            OLP.afkUpdater(event.getEntity());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (OLP.settings.getEnabledFeatures().getEnabledRaceManager() && OLP.rm != null)
            OLP.rm.applyRace(event.getPlayer(), null);
        if (OLP.settings.getEnabledFeatures().getEnabledLivesManager() && OLP.lm != null)
            OLP.lm.setPlayerGameMode(event.getPlayer());
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (OLP.settings.getEnabledFeatures().getEnabledMobManager() && OLP.mm != null)
            OLP.mm.onEntitySpawn(event);
    }

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        if (OLP.settings.getEnabledFeatures().getEnabledLivesManager() && OLP.lm != null)
            if (!event.getPlayer().isOp() && OLP.lm.getPlayerLivesRemaining(event.getPlayer()) <= 0)
                OLP.lm.setPlayerGameMode(event.getPlayer());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (OLP.settings.getEnabledFeatures().getEnabledRaceManager() && OLP.rm != null)
            OLP.rm.onDamage(event);
    }

    @EventHandler
    public void playerItemConsume(PlayerItemConsumeEvent event) {
        if (OLP.settings.getEnabledFeatures().getEnabledRaceManager() && OLP.rm != null)
            OLP.rm.playerItemConsume(event);
    }

    @EventHandler
    public void playerTamePet(EntityTameEvent event) {
        if (OLP.settings.getEnabledFeatures().getEnabledRaceManager() && OLP.rm != null)
            OLP.rm.playerTamePet(event);
    }

    @EventHandler
    public void playerArmorChange(PlayerArmorChangeEvent event) {
        if (OLP.settings.getEnabledFeatures().getEnabledRaceManager() && OLP.rm != null)
            OLP.rm.playerArmorChange(event);
    }

    @EventHandler
    public void entityPickupEvent(EntityPickupItemEvent event) {
        if (OLP.settings.getEnabledFeatures().getEnabledRaceManager() && OLP.rm != null)
            OLP.rm.entityPickupEvent(event);
    }

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent event) {
        if (OLP.settings.getEnabledFeatures().getEnabledRaceManager() && OLP.rm != null)
            OLP.rm.inventoryClickEvent(event);
    }

    @EventHandler
    public void playerDropItem(PlayerDropItemEvent event) {
        if (OLP.settings.getEnabledFeatures().getEnabledRaceManager() && OLP.rm != null)
            OLP.rm.playerDropItem(event);
    }

    @EventHandler
    public void playerDisconnect(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (OLP.settings.getEnabledFeatures().getEnabledAFKChecker() && OLP.lpAPI != null) {
            User user = OLP.lpAPI.getUserManager().getUser(player.getUniqueId());
            assert user != null;
            Group afk = OLP.lpAPI.getGroupManager().getGroup("afk");
            assert afk != null;
            user.data().remove(InheritanceNode.builder(afk).build());
            OLP.lpAPI.getUserManager().saveUser(user);
            OLP.afkLastInput.remove(player.getUniqueId());
        }
    }
}
