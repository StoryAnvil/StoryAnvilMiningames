package io.github.storyanvil.minigames.events;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.storyanvil.minigames.MiniGames;
import io.github.storyanvil.minigames.ShopItem;
import io.github.storyanvil.minigames.scripts.MiniGamesScripts;
import io.github.storyanvil.minigames.utils.BigArrays;
import io.github.storyanvil.minigames.utils.StoryUtils;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

import static io.github.storyanvil.minigames.MiniGames.LOGGER;
import static io.github.storyanvil.minigames.scripts.MiniGamesScripts.quiet;
import static io.github.storyanvil.minigames.scripts.MiniGamesScripts.shared;

@Mod.EventBusSubscriber(modid = MiniGames.MODID)
public class RawEventBus {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        //noinspection SpellCheckingInspection
        event.getDispatcher().register(Commands.literal("storyclient").requires(css -> css.hasPermission(1))
                .then(Commands.literal("shop")
                        .then(Commands.argument("id", IntegerArgumentType.integer(0))
                                .executes(context -> {
                                    int id = IntegerArgumentType.getInteger(context, "id");
                                    if (id < MiniGames.shop.size()) {
                                        Scoreboard scoreboard = context.getSource().getServer().getScoreboard();
                                        Score score = scoreboard.getOrCreatePlayerScore(context.getSource().getPlayer().getScoreboardName(), scoreboard.getObjective("coins"));
                                        if (score.getScore() >= MiniGames.shop.get(id).price) {
                                            score.setScore(score.getScore() - MiniGames.shop.get(id).price);
                                            context.getSource().getPlayer().getInventory().add(MiniGames.shop.get(id).item.createItemStack(1, false));
                                        } else {
                                            context.getSource().getPlayer().sendSystemMessage(Component.translatable("story.storyanvil.not_enough_coins"));
                                        }
                                        return 0;
                                    }
                                    return 1;
                                })
                        )
                )
        );
        event.getDispatcher().register(Commands.literal("storyanvil").requires(css -> css.hasPermission(2))
                .then(Commands.literal("log")
                        .then(Commands.argument("msg", StringArgumentType.string()).executes(context -> {
                            LOGGER.info(StringArgumentType.getString(context, "msg"));
                            return 0;
                        }))
                )
                .then(Commands.literal("log_error")
                        .then(Commands.argument("msg", StringArgumentType.string()).executes(context -> {
                            LOGGER.error(StringArgumentType.getString(context, "msg"));
                            return 0;
                        }))
                )
                .then(Commands.literal("spreadPlayersToTeams")
                        .then(Commands.argument("minPlayers", IntegerArgumentType.integer(1))
                                .then(Commands.argument("maxPlayersPerTeam", IntegerArgumentType.integer(1))
                                        .then(Commands.argument("players", EntityArgument.players())
                                                .then(Commands.argument("teams", IntegerArgumentType.integer(1, 4))
                                                        .then(Commands.argument("success", FunctionArgument.functions())
                                                                .then(Commands.argument("failure", FunctionArgument.functions())
                                                                        .executes(context -> {
                                                                            MinecraftServer server = context.getSource().getServer();
                                                                            ArrayList<ServerPlayer> players = new ArrayList<>(EntityArgument.getPlayers(context, "players"));
                                                                            int minPlayers = IntegerArgumentType.getInteger(context, "minPlayers");
                                                                            int maxPlayersPerTeam = IntegerArgumentType.getInteger(context, "maxPlayersPerTeam");
                                                                            int teamsNumber = IntegerArgumentType.getInteger(context, "teams");
                                                                            Collection<CommandFunction> onSuccess = FunctionArgument.getFunctions(context, "success");
                                                                            Collection<CommandFunction> onFailure = FunctionArgument.getFunctions(context, "failure");
                                                                            String[] teams = Arrays.copyOfRange(StoryUtils.TEAMS, 0, teamsNumber);

                                                                            LOGGER.info(Arrays.toString(teams));

                                                                            if (players.size() < minPlayers) {
                                                                                LOGGER.info("Failed!");
                                                                                StoryUtils.runAsFunctions(server, context.getSource(), onFailure);
                                                                                return 1;
                                                                            }

                                                                            int seats = maxPlayersPerTeam * teamsNumber;
                                                                            if (seats > players.size()) seats = players.size();
                                                                            Collections.shuffle(players, MiniGames.random);
                                                                            int team = 0;
                                                                            for (int i = 0; i < seats; i++) {
                                                                                StoryUtils.joinTeam(server, players.get(i).getScoreboardName(), teams[team]);
                                                                                LOGGER.info(players.get(i).getScoreboardName() + " in " + teams[team]);
                                                                                team++;
                                                                                if (team > teamsNumber - 1) {
                                                                                    team = 0;
                                                                                }
                                                                            }
                                                                            for (int i = seats; i < players.size(); i++) {
                                                                                StoryUtils.joinTeam(server, players.get(i).getScoreboardName(), StoryUtils.TEAM_SPECTATORS);
                                                                            }
                                                                            StoryUtils.runAsFunctions(server, context.getSource(), onSuccess);
                                                                            return 0;
                                                                        })
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.literal("unpack")
                        .then(Commands.argument("cmd", StringArgumentType.string())
                                .executes(context -> {
                                    MinecraftServer server = context.getSource().getServer();
                                    String[] commands = StringArgumentType.getString(context, "cmd").split("\\\\;");
                                    for (String command : commands) {
                                        StoryUtils.runAs(context.getSource(), command);
                                    }
                                    return 0;
                                })
                        )
                )
                .then(Commands.literal("script")
                        .then(Commands.literal("000_001_randomize_word").executes(c -> shared(MiniGamesScripts::g000_001_randomize_word, c)))
                        .then(Commands.literal("000_007_pre").executes(c ->            shared(MiniGamesScripts::g000_007_pre, c)))
                        .then(Commands.literal("000_007_state_1").executes(c ->         quiet(MiniGamesScripts::g000_007_state_1, c)))
                )
                .then(Commands.literal("whisper")
                        .then(Commands.argument("from", StringArgumentType.string())
                                .then(Commands.argument("players", EntityArgument.players())
                                        .then(Commands.argument("text", StringArgumentType.string())
                                                .executes(context -> {
                                                    Component from = Component.translatable(StringArgumentType.getString(context, "from"));
                                                    Component text = Component.translatable(StringArgumentType.getString(context, "text"));
                                                    for (ServerPlayer player : EntityArgument.getPlayers(context, "players")) {
                                                        StoryUtils.mimicWhisper(from, text, player);
                                                    }
                                                    return 0;
                                                })
                                        )
                                )
                        )
                )
                .then(Commands.literal("registerShopItem")
                        .then(Commands.argument("price", IntegerArgumentType.integer(0))
                                .then(Commands.argument("item", ItemArgument.item(event.getBuildContext()))
                                        .executes(context -> {
                                            ItemInput itemInput = ItemArgument.getItem(context, "item");
                                            MiniGames.shop.add(new ShopItem(IntegerArgumentType.getInteger(context, "price"), itemInput, MiniGames.shop.size() + 1, itemInput.createItemStack(1, false).getDisplayName()));
                                            return 0;
                                        })
                                )
                        )
                )
                .then(Commands.literal("clearShopRegistries")
                        .executes(context -> {
                            MiniGames.shop.clear();
                            return 0;
                        })
                )
                .then(Commands.literal("sendShopItem")
                        .then(Commands.argument("id", IntegerArgumentType.integer(0))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> {
                                            int id = IntegerArgumentType.getInteger(context, "id");
                                            ShopItem item = MiniGames.shop.get(id);
                                            ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                            player.sendSystemMessage(Component.translatable("story.storyanvil.shopItem", String.valueOf(item.price), item.name).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/storyclient shop " + id))));
                                            return 0;
                                        })
                                )
                        )
                )
                .then(Commands.literal("G")
                        .then(Commands.literal("registerStartup") // On game start
                                .then(Commands.argument("id", IntegerArgumentType.integer(1))
                                        .then(Commands.argument("command", StringArgumentType.string())
                                                .executes(context -> {
                                                    int id = IntegerArgumentType.getInteger(context, "id");
                                                    String ifStarted = StringArgumentType.getString(context, "command");
                                                    MinecraftServer server = context.getSource().getServer();
                                                    // check so no game running same time
                                                    if (StoryUtils.getDataScore(server, "_") != 0) {
                                                        context.getSource().sendSystemMessage(Component.translatable("story.storyanvil.game_already_running"));
                                                        return 1;
                                                    }
                                                    StoryUtils.setDataScore(server, "_", id);
                                                    StoryUtils.runAs(context.getSource(), ifStarted);
                                                    LOGGER.info("Game " + id + " started!");
                                                    return 0;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("registerEnd") // On game end
                                .executes(context -> {
                                    MiniGamesScripts.registerEnd(context.getSource().getServer(), context.getSource());
                                    return 0;
                                })
                        )
                )
                .then(Commands.literal("P")
                        .then(Commands.argument("players", EntityArgument.players())
                                .then(Commands.literal("p")
                                        .then(Commands.argument("may", BoolArgumentType.bool())
                                                .then(Commands.literal("fly")
                                                        .executes(context -> {
                                                            EntityArgument.getPlayers(context, "players").forEach(serverPlayer -> {
                                                                StoryUtils.mayFly(serverPlayer, BoolArgumentType.getBool(context, "may"));
                                                            });
                                                            return 0;
                                                        })
                                                )
                                                .then(Commands.literal("god")
                                                        .executes(context -> {
                                                            EntityArgument.getPlayers(context, "players").forEach(serverPlayer -> {
                                                                StoryUtils.mayTakeNoDamage(serverPlayer, BoolArgumentType.getBool(context, "may"));
                                                            });
                                                            return 0;
                                                        })
                                                )
                                                .then(Commands.literal("instabuild")
                                                        .executes(context -> {
                                                            EntityArgument.getPlayers(context, "players").forEach(serverPlayer -> {
                                                                StoryUtils.mayInstabuild(serverPlayer, BoolArgumentType.getBool(context, "may"));
                                                            });
                                                            return 0;
                                                        })
                                                )
                                        )
                                )
                        )
                )
        );
    }

    @SubscribeEvent
    public static void onPlayerInteractWithBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getSide() == LogicalSide.CLIENT) return;

        MinecraftServer server = event.getLevel().getServer();
        if (StoryUtils.ifGame(server, 7)) {
            BlockState block = event.getLevel().getBlockState(event.getPos());
            if (Objects.equals(ForgeRegistries.BLOCKS.getKey(block.getBlock()), new ResourceLocation("minecraft", "spruce_trapdoor"))) {
                boolean isSecondPlayer = event.getPos().getX() == 15030 || event.getPos().getX() == 15032 || event.getPos().getX() == 15034;
                assert server != null;
                boolean isOpen = block.getValue(BlockStateProperties.OPEN);
                StoryUtils.setScoreboard(server, "g000_007",
                        (isSecondPlayer ?
                                "P2_" + StoryUtils.backFindPos(BigArrays.player2pos, event.getPos().below()) :
                                "P1_" + StoryUtils.backFindPos(BigArrays.player1pos, event.getPos().below())),
                        isOpen ? 1 : 0);
                int state =  StoryUtils.getScoreboard(server, "g000_007", "state");
                if (state == 0) {
                    event.setCanceled(true);
                    event.getLevel().setBlock(isSecondPlayer ? new BlockPos(15013, -53, 292) : new BlockPos(15011, -53, 292), event.getLevel().getBlockState(event.getPos().below()), 2);
                    server.getPlayerList().broadcastSystemMessage(isSecondPlayer ? Component.translatable("story.storyanvil.g000_007_p2_selected") : Component.translatable("story.storyanvil.g000_007_p1_selected"), true);
                    if (!event.getLevel().getBlockState(!isSecondPlayer ? new BlockPos(15013, -53, 292) : new BlockPos(15011, -53, 292)).isAir()) {
                        // If both players selected block
                        BlockState bsP1 = event.getLevel().getBlockState(new BlockPos(15011, -53, 294));
                        BlockState bsP2 = event.getLevel().getBlockState(new BlockPos(15013, -53, 294));
                        for (int i = 0; i < 15; i++) {
                            event.getLevel().setBlock(BigArrays.player1pos.get(i).above(), bsP1, 2);
                            event.getLevel().setBlock(BigArrays.player2pos.get(i).above(), bsP2, 2);
                        }
                        MiniGamesScripts.g000_007_state_1(null, server, event.getLevel());
                    }
                } else if (state == 1) {
                    boolean won = false;
                    boolean cause = false;
                    if (!isSecondPlayer) {
                        if (StoryUtils.addScoreboard(server, "g000_007", "p1Left", isOpen ? -1 : 1) == 1) {
                            cause = true;
                            for (Integer i : BigArrays.player1pos.keySet()) {
                                BlockPos bp = BigArrays.player1pos.get(i).above();
                                BlockState bs = event.getLevel().getBlockState(bp);
                                if (bs.getValue(BlockStateProperties.OPEN)) {
                                    BlockState bs2 = event.getLevel().getBlockState(new BlockPos(15013, -53, 292));
                                    BlockState bs3 = event.getLevel().getBlockState(bp.below());
                                    won = bs3.equals(bs2);
                                    break;
                                }
                            }
                        }
                    } else {
                        if (StoryUtils.addScoreboard(server, "g000_007", "p2Left", isOpen ? -1 : 1) == 1) {
                            cause = true;
                            for (Integer i : BigArrays.player2pos.keySet()) {
                                BlockPos bp = BigArrays.player2pos.get(i).above();
                                BlockState bs = event.getLevel().getBlockState(bp);
                                if (bs.getValue(BlockStateProperties.OPEN)) {
                                    BlockState bs2 = event.getLevel().getBlockState(new BlockPos(15011, -53, 292));
                                    BlockState bs3 = event.getLevel().getBlockState(bp.below());
                                    won = !bs3.equals(bs2);
                                    break;
                                }
                            }
                        }
                    }
                    if (cause) {
                        event.setCanceled(true);
                        if (won) { // Player 1 won
                            server.getPlayerList().broadcastSystemMessage(Component.translatable("g000_007_p1_won"), true);
                        } else {
                            server.getPlayerList().broadcastSystemMessage(Component.translatable("g000_007_p2_won"), true);
                        }
                        for (Integer i : BigArrays.player1pos.keySet()) {
                            BlockPos bp = BigArrays.player1pos.get(i);
                            BlockState bs = event.getLevel().getBlockState(bp);
                            BlockState bs2 = event.getLevel().getBlockState(new BlockPos(15011, -53, 292));
                            if (bs.equals(bs2)) {
                                event.getLevel().setBlock(bp.above(), won ? Blocks.DIAMOND_BLOCK.defaultBlockState() : Blocks.GOLD_BLOCK.defaultBlockState(), 2);
                            } else {
                                event.getLevel().setBlock(bp.above(), Blocks.AIR.defaultBlockState(), 2);
                                event.getLevel().setBlock(bp, Blocks.AIR.defaultBlockState(), 2);
                            }
                        }
                        for (Integer i : BigArrays.player2pos.keySet()) {
                            BlockPos bp = BigArrays.player2pos.get(i);
                            BlockState bs = event.getLevel().getBlockState(bp);
                            BlockState bs2 = event.getLevel().getBlockState(new BlockPos(15013, -53, 292));
                            if (bs.equals(bs2)) {
                                event.getLevel().setBlock(bp.above(), !won ? Blocks.DIAMOND_BLOCK.defaultBlockState() : Blocks.GOLD_BLOCK.defaultBlockState(), 2);
                            } else {
                                event.getLevel().setBlock(bp.above(), Blocks.AIR.defaultBlockState(), 2);
                                event.getLevel().setBlock(bp, Blocks.AIR.defaultBlockState(), 2);
                            }
                        }
                        StoryUtils.setScoreboard(server, "g000_007", "state", -1);
                        MiniGames.queueServerWork(20 * 10, () -> {
                            MiniGamesScripts.registerEnd(server, StoryUtils.s(server, (ServerLevel) event.getLevel()));
                        });
                    }
                }
            }
        }
    }
}
