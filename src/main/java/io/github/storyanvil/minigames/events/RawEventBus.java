package io.github.storyanvil.minigames.events;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.storyanvil.minigames.MiniGames;
import io.github.storyanvil.minigames.utils.StoryUtils;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

import static io.github.storyanvil.minigames.MiniGames.LOGGER;

@Mod.EventBusSubscriber(modid = MiniGames.MODID)
public class RawEventBus {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
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
                .then(Commands.literal("add")
                        .then(Commands.argument("e", EntityArgument.entity())
                                .executes(context -> {

                                    return 0;
                                })
                        ))
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
                                                                                if (team > teamsNumber) {
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
                                        StoryUtils.runAs(server, context.getSource(), command);
                                    }
                                    return 0;
                                })
                        )
                )
        );
    }
}
