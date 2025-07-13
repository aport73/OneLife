package xyz.acyber.oneLife.managers;

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
import xyz.acyber.oneLife.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("SameReturnValue")
public class CommandManager {

    static ScoreManager sm;
    static RaceManager rm;
    static LivesManager lm;
    static Main main;

    public CommandManager(Main plugin) {
        main = plugin;
        sm = main.sm;
        rm = main.rm;
        lm = main.lm;
    }

    public LiteralCommandNode<CommandSourceStack> loadCmds() {

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
                .requires(sender -> sender.getSender().hasPermission("OneLife.Scores") && main.scoreMEnabled)
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
                                    main.mobMEnabled = ctx.getArgument("mMEnabled", Boolean.class);
                                    main.setFeatures();
                                    Player player = (Player) ctx.getSource().getSender();
                                    player.updateCommands();
                                    if (main.mobMEnabled)
                                        ctx.getSource().getSender().sendRichMessage("Mob Tweaks Enabled");
                                    else
                                        ctx.getSource().getSender().sendRichMessage("Mob Tweaks Disabled");
                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.literal("Races")
                        .then(Commands.argument("rMEnabled", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    main.raceMEnabled = ctx.getArgument("rMEnabled", Boolean.class);
                                    main.setFeatures();
                                    Player player = (Player) ctx.getSource().getSender();
                                    player.updateCommands();
                                    if (main.raceMEnabled)
                                        ctx.getSource().getSender().sendRichMessage("Races Enabled");
                                    else
                                        ctx.getSource().getSender().sendRichMessage("Races Disabled");
                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.literal("ScoreManager")
                        .then(Commands.argument("scoreMEnabled", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    main.scoreMEnabled = ctx.getArgument("scoreMEnabled", Boolean.class);
                                    main.setFeatures();
                                    Player player = (Player) ctx.getSource().getSender();
                                    player.updateCommands();
                                    if (main.scoreMEnabled)
                                        ctx.getSource().getSender().sendRichMessage("Score Manager Enabled");
                                    else
                                        ctx.getSource().getSender().sendRichMessage("Score Manager Disabled");
                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.literal("LifeGifting")
                        .then(Commands.argument("lifeGEnabled", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    main.lifeGEnabled = ctx.getArgument("lifeGEnabled", Boolean.class);
                                    main.setFeatures();
                                    Player player = (Player) ctx.getSource().getSender();
                                    player.updateCommands();
                                    if (main.lifeGEnabled)
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

                                if (main.livesMEnabled)
                                    ctx.getSource().getSender().sendRichMessage("Lives Manager Enabled");
                                else
                                    ctx.getSource().getSender().sendRichMessage("Lives Manager Disabled");
                                return Command.SINGLE_SUCCESS;

                            })))
                .then(Commands.literal("AFKKicker")
                        .then(Commands.argument("AFKKickerEnabled", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    main.afkKickEnabled = ctx.getArgument("AFKKickerEnabled", Boolean.class);
                                    main.setFeatures();
                                    Player player = (Player) ctx.getSource().getSender();
                                    player.updateCommands();
                                    if (main.afkKickEnabled)
                                        ctx.getSource().getSender().sendRichMessage("AFK Kicking Enabled");
                                    else
                                        ctx.getSource().getSender().sendRichMessage("AFK Kicking Disabled");
                                    return Command.SINGLE_SUCCESS;
                                })));

        LiteralArgumentBuilder<CommandSourceStack> cmdRaces = Commands.literal("Races")
                .requires(sender -> main.raceMEnabled)
                .then(Commands.argument("Player", ArgumentTypes.player())
                        .then(Commands.literal("ResetStartItems")
                                .requires(sender -> sender.getSender().hasPermission("OneLife.Races.ResetStartItems"))
                                .executes(CommandManager::runResetStartItemsLogic))
                        .then(Commands.literal("SetRace")
                                .requires(sender -> sender.getSender().hasPermission("OneLife.Races.SetRace") || rm.getPlayerRace((Player) sender.getSender()).equalsIgnoreCase("Human"))
                                .then(Commands.argument("Race", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            List<String> races = new ArrayList<>();
                                                for (String key : Objects.requireNonNull(main.getConfig().getConfigurationSection("races")).getKeys(false))
                                                    if (Objects.requireNonNull(main.getConfig().getConfigurationSection("races." + key)).getBoolean("enabled"))
                                                        races.add(key);
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

        //TODO Build out help cmd argument stack
        LiteralArgumentBuilder<CommandSourceStack> cmdHelp = Commands.literal("Help")
                .executes(ctx -> {
                    ctx.getSource().getSender().sendRichMessage("One Life Help! - To Be Implemented");
                    return Command.SINGLE_SUCCESS;
                });

        LiteralArgumentBuilder<CommandSourceStack> cmdRoot = Commands.literal("OneLife")
                .then(cmdScores)
                .then(cmdRaces)
                .then(Commands.literal("Climb")
                        .requires(sender -> sender.getExecutor() instanceof Player player && rm.getPlayerRace(player).equalsIgnoreCase("Arathim") && main.raceMEnabled)
                        .executes(CommandManager::runClimbLogic))
                .then(Commands.literal("GiveLife")
                        .requires(sender -> main.lifeGEnabled)
                        .then(Commands.argument("Player", ArgumentTypes.player())
                                .executes(CommandManager::runGiveLifeLogic)))
                .then(Commands.literal("Reload")
                        .requires(sender -> sender.getSender().hasPermission("OneLife.Reload"))
                        .executes(ctx -> {
                            main.reloadConfig();
                            ctx.getSource().getSender().sendRichMessage("One Life Plugin Reloaded!");
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(cmdLives
                        .requires(sender -> main.livesMEnabled))
                .then(cmdAFK
                        .requires(sender -> main.afkKickEnabled))
                .then(cmdFeatures)
                .then(cmdHelp);

         return cmdRoot.build();
    }

    private static int runGetKickTimeLogic(@NotNull CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().getSender().sendRichMessage("Time AFK before Kick is currently: " + main.getConfig().getDouble("AFK.minutesAFK") + "m");
        return Command.SINGLE_SUCCESS;
    }

    private static int runSetKickTimeLogic(@NotNull CommandContext<CommandSourceStack> ctx) {
        double kickTime = (double) ctx.getArgument("time", Integer.class);
        main.getConfig().set("AFK.minutesAFK", kickTime);
        main.saveConfig();
        ctx.getSource().getSender().sendMessage("Time AFK before Kick is now set to: " + kickTime + "m");
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
        if (rm.getPlayerClimbs(player)) {
            rm.setPlayerClimbs(player, false);
            stack.getSender().sendRichMessage("Climbing Turned Off");
        } else {
            rm.setPlayerClimbs(player, true);
            stack.getSender().sendRichMessage("Climbing Turned On");
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int runSetPlayerClimbLogic(@NotNull CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack stack = ctx.getSource();
        Player player = getPlayerArgument(ctx);
        boolean state = ctx.getArgument("state", boolean.class);
        rm.setPlayerClimbs(player, state);
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

        main.giveLife(giver, receiver);
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
        rm.setPlayerStartItem(player, false);
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
            main.setPlayerTasks(player, 0);
            rm.setPlayerRace(player, race);
            rm.applyRace(player, null);
            Player sender = (Player) ctx.getSource().getSender();
            sender.updateCommands();
            stack.getSender().sendMessage(player.getName() + " has been set to " + race);
        } else if (rm.getPlayerRace(player).equalsIgnoreCase("Human")) {
            main.setPlayerTasks(player, 0);
            rm.setPlayerRace(player, race);
            rm.applyRace(player, null);
            stack.getSender().sendMessage("Your race has been set to " + race);
        } else {
            stack.getSender().sendMessage("You are unable to change your race more than once, please ask an op for more assistance.");
            main.sendMsgOps(stack.getSender().getName() + " tried to change their race.");
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
