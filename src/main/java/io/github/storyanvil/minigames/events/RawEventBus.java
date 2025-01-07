package io.github.storyanvil.minigames.events;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.storyanvil.minigames.MiniGames;
import io.github.storyanvil.minigames.ShopItem;
import io.github.storyanvil.minigames.scripts.MiniGamesScripts;
import io.github.storyanvil.minigames.utils.StoryUtils;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.ExecuteCommand;
import net.minecraft.server.commands.GiveCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static io.github.storyanvil.minigames.MiniGames.LOGGER;
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
                                    MinecraftServer server = context.getSource().getServer();
                                    StoryUtils.setDataScore(server, "_", 0);
                                    server.getPlayerList().getPlayers().forEach(player -> {
                                        StoryUtils.mayFly(player, false);
                                    });
                                    StoryUtils.runAsFunction(server, context.getSource(), ResourceLocation.tryParse("storyanvil:game/spawn"));
                                    LOGGER.info("Game stopped!");
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

}
