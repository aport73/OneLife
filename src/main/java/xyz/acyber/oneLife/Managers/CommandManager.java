package xyz.acyber.oneLife.Managers;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
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
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;

import xyz.acyber.oneLife.Main;

public class CommandManager {

    static MobManager mm;
    static ScoreManager sm;
    static RaceManager rm;
    static Main main;

    public CommandManager(Main plugin) {
        main = plugin;
        sm = main.sm;
        rm = main.rm;
        mm = main.mm;
    }

    public LiteralCommandNode<CommandSourceStack> loadCmds() {

        LiteralArgumentBuilder<CommandSourceStack> cmdScores = Commands.literal("Scores")
                .requires(sender -> sender.getSender().isOp() && main.scoreMEnabled)
                .then(Commands.literal("All")
                        .executes(CommandManager::runAllScoresLogic))
                .then(Commands.literal("Player")
                        .then(Commands.argument("Player", ArgumentTypes.player())
                                .executes(CommandManager::runPlayerScoreLogic)));

        LiteralArgumentBuilder<CommandSourceStack> cmdFeatures = Commands.literal("Features")
                .requires(sender -> sender.getSender().isOp())
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
                                })));

        LiteralArgumentBuilder<CommandSourceStack> cmdRaces = Commands.literal("Races")
                .requires(sender -> main.raceMEnabled)
                .then(Commands.argument("Player", ArgumentTypes.player())
                        .then(Commands.literal("ResetStartItems")
                                .requires(sender -> sender.getSender().isOp())
                                .executes(CommandManager::runResetStartItemsLogic))
                        .then(Commands.literal("SetRace")
                                .requires(sender -> sender.getSender().isOp() || rm.getPlayerRace((Player) sender.getSender()).equalsIgnoreCase("Human"))
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
                                .requires(sender -> sender.getSender().isOp())
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
                        .requires(sender -> sender.getSender().isOp())
                        .executes(ctx -> {
                            main.reloadConfig();
                            ctx.getSource().getSender().sendRichMessage("One Life Plugin Reloaded!");
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(cmdFeatures)
                .then(cmdHelp);

         return cmdRoot.build();
    }

    private static int runClimbLogic(CommandContext<CommandSourceStack> ctx) {
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

    private static int runSetPlayerClimbLogic(CommandContext<CommandSourceStack> ctx) {
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


    private static int runGiveLifeLogic(CommandContext<CommandSourceStack> ctx) {
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

    private static int runAllScoresLogic(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack stack = ctx.getSource();
        stack.getSender().sendMessage("Generating Scores");
        for (OfflinePlayer offlinePlayer : Bukkit.getWhitelistedPlayers()) {
            if (offlinePlayer.getStatistic(Statistic.PLAY_ONE_MINUTE) != 0)
                sm.playerScore(stack, offlinePlayer, false);
        }
        stack.getSender().sendMessage("Scores have been logged to a File");
        return Command.SINGLE_SUCCESS;
    }

    private static int runPlayerScoreLogic(CommandContext<CommandSourceStack> ctx) {
        final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("Player", PlayerSelectorArgumentResolver.class);
        final Player player;
        try {
            player = targetResolver.resolve(ctx.getSource()).getFirst();
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
        sm.playerScore(ctx.getSource(), Objects.requireNonNull(player), true);
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
                    player.getInventory().removeItemAnySlot(i);
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


    private static Player getPlayerArgument(CommandContext<CommandSourceStack> ctx) {
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
