package xyz.acyber.oneLife.Managers;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.acyber.oneLife.DataObjects.Settings;
import xyz.acyber.oneLife.DataObjects.SubSettings.PlayerConfig;
import xyz.acyber.oneLife.DataObjects.SubSettings.Race;
import xyz.acyber.oneLife.OneLifePlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("SameReturnValue")
public class CommandManager {

    static ScoreManager sm;
    static RaceManager rm;
    static LivesManager lm;
    static OneLifePlugin oneLifePlugin;

    public CommandManager(OneLifePlugin plugin) {
        oneLifePlugin = plugin;
        sm = oneLifePlugin.sm;
        rm = oneLifePlugin.rm;
        lm = oneLifePlugin.lm;
    }

    public LiteralCommandNode<CommandSourceStack> loadCmds() {

        Settings settings = oneLifePlugin.settings;

        LiteralArgumentBuilder<CommandSourceStack> cmdAFK = Commands.literal("AFK")
                .requires(sender -> sender.getSender().hasPermission("OneLife.AFK"))
                .then(Commands.literal("Set")
                        .then(Commands.literal("KickTime")
                                .then(Commands.argument("time", IntegerArgumentType.integer())
                                        .executes(CommandManager::runSetKickTimeLogic))))
                .then(Commands.literal("Get")
                        .then(Commands.literal("KickTime")
                                .executes(CommandManager::runGetKickTimeLogic)));

        LiteralArgumentBuilder<CommandSourceStack> cmdLives = Commands.literal("Lives")
                .requires(sender -> sender.getSender().hasPermission("OneLife.lives"))
                .then(Commands.literal("Set")
                        .then(Commands.literal("LivesCap")
                            .then(Commands.argument("Cap", IntegerArgumentType.integer())
                                .executes(CommandManager::runSetLivesCapLogic)))
                        .then(Commands.literal("FinalGameMode")
                                .then(Commands.argument("GameMode", ArgumentTypes.gameMode())
                                        .executes(CommandManager::runSetFinalGameModeLogic)))
                        .then(Commands.literal("PlayerDeaths")
                                .then(Commands.argument("Player", ArgumentTypes.player())
                                    .then(Commands.argument("Deaths", IntegerArgumentType.integer())
                                        .executes(CommandManager::runSetPlayerDeathsLogic)))))
                .then(Commands.literal("Get")
                        .then(Commands.literal("LivesCap")
                                .executes(CommandManager::runGetLivesCapLogic))
                        .then(Commands.literal("FinalGameMode")
                                .executes(CommandManager::runGetFinalGameModeLogic)))
                .then(Commands.literal("Reset")
                        .then(Commands.literal("Deaths")
                                .executes(CommandManager::runResetDeathsLogic))
                        .then(Commands.literal("PlayerDeaths")
                                .then(Commands.argument("Player", ArgumentTypes.player())
                                        .executes(CommandManager::runResetPlayerDeathsLogic))));

        LiteralArgumentBuilder<CommandSourceStack> cmdScores = Commands.literal("Scores")
                .requires(sender -> sender.getSender().hasPermission("OneLife.Scores") && settings.getEnabledFeatures().getEnabledScoreManager())
                .then(Commands.literal("All")
                        .executes(CommandManager::runAllScoresLogic))
                .then(Commands.literal("Player")
                        .then(Commands.argument("Player", ArgumentTypes.player())
                                .executes(CommandManager::runPlayerScoreLogic)));

        LiteralArgumentBuilder<CommandSourceStack> cmdFeatures = Commands.literal("Features")
                .requires(sender -> sender.getSender().hasPermission("OneLife.Features"))
                .then(Commands.literal("MobsTweaks")
                        .then(Commands.argument("mMEnabled", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    settings.getEnabledFeatures().setEnabledMobManager(ctx.getArgument("mMEnabled", Boolean.class));
                                    Player player = (Player) ctx.getSource().getSender();
                                    player.updateCommands();
                                    if (settings.getEnabledFeatures().getEnabledMobManager())
                                        ctx.getSource().getSender().sendRichMessage("MobConfig Tweaks Enabled");
                                    else
                                        ctx.getSource().getSender().sendRichMessage("MobConfig Tweaks Disabled");
                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.literal("Races")
                        .then(Commands.argument("rMEnabled", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    settings.getEnabledFeatures().setEnabledRaceManager(ctx.getArgument("rMEnabled", Boolean.class));
                                    Player player = (Player) ctx.getSource().getSender();
                                    player.updateCommands();
                                    if (settings.getEnabledFeatures().getEnabledRaceManager())
                                        ctx.getSource().getSender().sendRichMessage("Races Enabled");
                                    else
                                        ctx.getSource().getSender().sendRichMessage("Races Disabled");
                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.literal("ScoreManager")
                        .then(Commands.argument("scoreMEnabled", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    settings.getEnabledFeatures().setEnabledScoreManager(ctx.getArgument("scoreMEnabled", Boolean.class));
                                    Player player = (Player) ctx.getSource().getSender();
                                    player.updateCommands();
                                    if (settings.getEnabledFeatures().getEnabledScoreManager())
                                        ctx.getSource().getSender().sendRichMessage("Score Manager Enabled");
                                    else
                                        ctx.getSource().getSender().sendRichMessage("Score Manager Disabled");
                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.literal("LifeGifting")
                        .then(Commands.argument("lifeGEnabled", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    settings.getEnabledFeatures().setEnabledLifeGifting(ctx.getArgument("lifeGEnabled", Boolean.class));
                                    Player player = (Player) ctx.getSource().getSender();
                                    player.updateCommands();
                                    if (settings.getEnabledFeatures().getEnabledLifeGifting())
                                        ctx.getSource().getSender().sendRichMessage("Life Gifting Enabled");
                                    else
                                        ctx.getSource().getSender().sendRichMessage("Life Gifting Disabled");
                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.literal("Lives")
                    .then(Commands.argument("livesMEnabled", BoolArgumentType.bool())
                            .executes(ctx -> {
                                if (ctx.getArgument("livesMEnabled", Boolean.class))
                                    lm.enableDeathsScoreboard();
                                else
                                    lm.disableDeathsScoreboard();

                                Player player = (Player) ctx.getSource().getSender();
                                player.updateCommands();

                                if (settings.getEnabledFeatures().getEnabledLivesManager())
                                    ctx.getSource().getSender().sendRichMessage("Lives Manager Enabled");
                                else
                                    ctx.getSource().getSender().sendRichMessage("Lives Manager Disabled");
                                return Command.SINGLE_SUCCESS;

                            })))
                .then(Commands.literal("AFKCheckerConfig")
                        .then(Commands.argument("AFKCheckerEnabled", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    settings.getEnabledFeatures().setEnabledAFKChecker(ctx.getArgument("AFKCheckerEnabled", Boolean.class));
                                    Player player = (Player) ctx.getSource().getSender();
                                    player.updateCommands();
                                    if (settings.getEnabledFeatures().getEnabledAFKChecker())
                                        ctx.getSource().getSender().sendRichMessage("AFK Checking Enabled");
                                    else
                                        ctx.getSource().getSender().sendRichMessage("AFK Checking Disabled");
                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.literal("NightHostiles")
                        .then(Commands.argument("NightHostiles", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    settings.getEnabledFeatures().setEnabledNightHostiles(ctx.getArgument("NightHostiles", Boolean.class));
                                    Player player = (Player) ctx.getSource().getSender();
                                    player.updateCommands();
                                    if (settings.getEnabledFeatures().getEnabledNightHostiles())
                                        ctx.getSource().getSender().sendRichMessage("Night Hostiles Enabled");
                                    else
                                        ctx.getSource().getSender().sendRichMessage("Night Hostiles Disabled");
                                    return Command.SINGLE_SUCCESS;
                                })));

        LiteralArgumentBuilder<CommandSourceStack> cmdRaces = Commands.literal("Races")
                .requires(sender -> settings.getEnabledFeatures().getEnabledRaceManager())
                .then(Commands.argument("Player", ArgumentTypes.player())
                        .then(Commands.literal("ResetStartItems")
                                .requires(sender -> sender.getSender().hasPermission("OneLife.Races.ResetStartItems"))
                                .executes(CommandManager::runResetStartItemsLogic))
                        .then(Commands.literal("SetRace")
                                .requires(sender -> sender.getSender().hasPermission("OneLife.Races.SetRace") || settings.getPlayerRace((Player) sender.getSender()).getRaceName().equalsIgnoreCase("Human"))
                                .then(Commands.argument("Race", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            List<String> races = new ArrayList<>();
                                                for (Race race : Objects.requireNonNull(settings.getRacesList()))
                                                    if (race.isEnabled())
                                                        races.add(race.getRaceName());
                                            races.stream()
                                                    .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                                    .forEach(builder::suggest);
                                            return builder.buildFuture();
                                        })
                                        .executes(CommandManager::runSetRaceLogic)))
                        .then(Commands.literal("Abilities")
                                .requires(sender -> sender.getSender().hasPermission("OneLife.Races.Abilities"))
                                .then(Commands.literal("Climb")
                                        .then(Commands.argument("state", BoolArgumentType.bool())
                                                .executes(CommandManager::runSetPlayerClimbLogic)))));

        LiteralArgumentBuilder<CommandSourceStack> cmdHelp = Commands.literal("Help")
                .executes(ctx -> {
                    ctx.getSource().getSender().sendRichMessage("One Life Help! - To Be Implemented");
                    return Command.SINGLE_SUCCESS;
                });

        LiteralArgumentBuilder<CommandSourceStack> cmdRoot = Commands.literal("OneLife")
                .then(cmdScores)
                .then(cmdRaces)
                .then(Commands.literal("Climb")
                        .requires(sender -> sender.getExecutor() instanceof Player player && settings.getPlayerRace(player).getCanClimbWalls() && oneLifePlugin.settings.getEnabledFeatures().getEnabledRaceManager())
                        .executes(CommandManager::runClimbLogic))
                .then(Commands.literal("GiveLife")
                        .requires(sender -> settings.getEnabledFeatures().getEnabledLifeGifting())
                        .then(Commands.argument("Player", ArgumentTypes.player())
                                .executes(CommandManager::runGiveLifeLogic)))
                .then(Commands.literal("Reload")
                        .requires(sender -> sender.getSender().hasPermission("OneLife.Reload"))
                        .executes(ctx -> {
                            oneLifePlugin.reload();
                            ctx.getSource().getSender().sendRichMessage("One Life Plugin Reloaded!");
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(cmdLives
                        .requires(sender -> settings.getEnabledFeatures().getEnabledLivesManager()))
                .then(cmdAFK
                        .requires(sender -> settings.getEnabledFeatures().getEnabledAFKChecker()))
                .then(cmdFeatures)
                .then(cmdHelp);

         return cmdRoot.build();
    }

    private static int runGetKickTimeLogic(@NotNull CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().getSender().sendRichMessage("Time before viewed as AFK is currently: " + oneLifePlugin.settings.getAfkCheckerConfig().getMinutesAFK() + "m");
        return Command.SINGLE_SUCCESS;
    }

    private static int runSetKickTimeLogic(@NotNull CommandContext<CommandSourceStack> ctx) {
        double kickTime = (double) ctx.getArgument("time", Integer.class);
        oneLifePlugin.settings.getAfkCheckerConfig().setMinutesAFK((int) kickTime);
        oneLifePlugin.saveConfig();
        ctx.getSource().getSender().sendMessage("Time before viewed as AFK  is now set to: " + kickTime + "m");
        return Command.SINGLE_SUCCESS;
    }

    private static int runResetPlayerDeathsLogic(CommandContext<CommandSourceStack> ctx) {
        Player player = getPlayerArgument(ctx);
        lm.resetPlayerScore(lm.getObjective("deaths", lm.getScoreboard()), player);
        ctx.getSource().getSender().sendRichMessage(player.getName() + "'s Deaths have been reset!");
        return Command.SINGLE_SUCCESS;
    }

    private static int runResetDeathsLogic(@NotNull CommandContext<CommandSourceStack> ctx) {
        lm.resetObjective(lm.getObjective("deaths", lm.getScoreboard()));
        ctx.getSource().getSender().sendRichMessage("Deaths have been reset!");
        return Command.SINGLE_SUCCESS;
    }

    private static int runGetFinalGameModeLogic(@NotNull CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().getSender().sendRichMessage("Final Game Mode is: " + lm.getFinalGameMode());
        return Command.SINGLE_SUCCESS;
    }

    private static int runGetLivesCapLogic(@NotNull CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().getSender().sendRichMessage("Lives cap is: " + lm.getLivesCap());
        return Command.SINGLE_SUCCESS;
    }

    private static int runSetPlayerDeathsLogic(@NotNull CommandContext<CommandSourceStack> ctx) {
        int deaths = ctx.getArgument("Deaths", Integer.class);
        Player player = getPlayerArgument(ctx);
        lm.setPlayerScore(lm.getObjective("deaths",lm.getScoreboard()), player, deaths);
        ctx.getSource().getSender().sendRichMessage(player.getName() + "'s deaths has been set to " + deaths);
        return Command.SINGLE_SUCCESS;
    }

    private static int runSetFinalGameModeLogic(@NotNull CommandContext<CommandSourceStack> ctx) {
        GameMode mode = ctx.getArgument("GameMode", GameMode.class);
        lm.setFinalGameMode(mode);
        ctx.getSource().getSender().sendRichMessage("Final game mode has been set to " + mode.name());
        return Command.SINGLE_SUCCESS;
    }

    private static int runSetLivesCapLogic(@NotNull CommandContext<CommandSourceStack> ctx) {
        int cap = ctx.getArgument("Cap", Integer.class);
        lm.setLivesCap(cap);
        ctx.getSource().getSender().sendRichMessage("Lives cap has been set to " + cap);
        return Command.SINGLE_SUCCESS;
    }

    private static int runClimbLogic(@NotNull CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack stack = ctx.getSource();
        Player player = (Player) stack.getSender();
        PlayerConfig pc = oneLifePlugin.settings.getPlayerConfig(player);
        if (pc.isClimbingEnabled()) {
            pc.setClimbingEnabled(false);
            stack.getSender().sendRichMessage("Climbing Turned Off");
        } else {
            pc.setClimbingEnabled(true);
            stack.getSender().sendRichMessage("Climbing Turned On");
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int runSetPlayerClimbLogic(@NotNull CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack stack = ctx.getSource();
        Player player = getPlayerArgument(ctx);
        PlayerConfig pc = oneLifePlugin.settings.getPlayerConfig(player);
        boolean state = ctx.getArgument("state", boolean.class);
        pc.setClimbingEnabled(state);
        if (state)
            stack.getSender().sendRichMessage("Climbing Turned On");
        else
            stack.getSender().sendRichMessage("Climbing Turned Off");
        return Command.SINGLE_SUCCESS;
    }

    private static int runGiveLifeLogic(@NotNull CommandContext<CommandSourceStack> ctx) {
        final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("Player", PlayerSelectorArgumentResolver.class);
        final Player receiver;
        final Player giver;

        try {
            receiver = targetResolver.resolve(ctx.getSource()).getFirst();
            giver = Bukkit.getPlayer(ctx.getSource().getSender().getName());
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }

        oneLifePlugin.giveLife(giver, receiver);
        return Command.SINGLE_SUCCESS;
    }

    private static int runAllScoresLogic(@NotNull CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack stack = ctx.getSource();
        stack.getSender().sendMessage("Generating Scores");
        sm.allPlayerScores(stack, false);
        stack.getSender().sendMessage("Scores have been logged to a File");
        return Command.SINGLE_SUCCESS;
    }

    private static int runPlayerScoreLogic(@NotNull CommandContext<CommandSourceStack> ctx) {
        final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("Player", PlayerSelectorArgumentResolver.class);
        final Player player;
        try {
            player = targetResolver.resolve(ctx.getSource()).getFirst();
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
        sm.singlePlayerScores(ctx.getSource(), Objects.requireNonNull(player));
        return Command.SINGLE_SUCCESS;
    }

    private static int runResetStartItemsLogic(CommandContext<CommandSourceStack> ctx) {
        final Player player = getPlayerArgument(ctx);
        PlayerConfig pc = oneLifePlugin.settings.getPlayerConfig(player);
        pc.setGivenStartItems(false);
        ctx.getSource().getSender().sendRichMessage(player.getName() + "'s start items have been reset");
        return Command.SINGLE_SUCCESS;
    }

    private static int runSetRaceLogic(CommandContext<CommandSourceStack> ctx) {
        final Player player = getPlayerArgument(ctx);
        CommandSourceStack stack = ctx.getSource();
        final String race = ctx.getArgument("Race", String.class);
        if (stack.getSender().isOp()) {
            assert player != null;
            for (ItemStack i : player.getInventory().getContents()) {
                rm.clearRaceItemEnchants(i);
                if (rm.isRaceItem(i)) {
                    player.getInventory().removeItemAnySlot(Objects.requireNonNull(i));
                }
            }
            for(int id: oneLifePlugin.settings.getPlayerConfig(player).getRunningTasks().keySet()) {
                Bukkit.getScheduler().cancelTask(id);
                oneLifePlugin.settings.getPlayerConfig(player).getRunningTasks().remove(id);
            }
            oneLifePlugin.settings.setPlayerRace(player,oneLifePlugin.settings.getRaceByName(race));
            rm.applyRace(player, null);
            Player sender = (Player) ctx.getSource().getSender();
            sender.updateCommands();
            stack.getSender().sendMessage(player.getName() + " has been set to " + race);
        } else if (oneLifePlugin.settings.getPlayerRace(player).getRaceName().equalsIgnoreCase("Human")) {
            for(int id: oneLifePlugin.settings.getPlayerConfig(player).getRunningTasks().keySet()) {
                Bukkit.getScheduler().cancelTask(id);
                oneLifePlugin.settings.getPlayerConfig(player).getRunningTasks().remove(id);
            }
            oneLifePlugin.settings.setPlayerRace(player,oneLifePlugin.settings.getRaceByName(race));
            rm.applyRace(player, null);
            stack.getSender().sendMessage("Your race has been set to " + race);
        } else {
            stack.getSender().sendMessage("You are unable to change your race more than once, please ask an op for more assistance.");
            oneLifePlugin.sendMsgOps(stack.getSender().getName() + " tried to change their race.");
        }
        return Command.SINGLE_SUCCESS;
    }

    private static Player getPlayerArgument(@NotNull CommandContext<CommandSourceStack> ctx) {
        final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("Player", PlayerSelectorArgumentResolver.class);
        final Player player;
        try {
            player = targetResolver.resolve(ctx.getSource()).getFirst();
        } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
        }
        return player;
    }
}
