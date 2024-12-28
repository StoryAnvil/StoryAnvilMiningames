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

@Mod.EventBusSubscriber(modid = MiniGames.MODID)
public class RawEventBus {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("storyanvil").requires(css -> css.hasPermission(2))
                .then(Commands.literal("log")
                        .then(Commands.argument("msg", StringArgumentType.string()).executes(context -> {
                            MiniGames.LOGGER.info(StringArgumentType.getString(context, "msg"));
                            return 0;
                        }))
                )
                .then(Commands.literal("log_error")
                        .then(Commands.argument("msg", StringArgumentType.string()).executes(context -> {
                            MiniGames.LOGGER.error(StringArgumentType.getString(context, "msg"));
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
                                                                            int maxPlayers = maxPlayersPerTeam * teamsNumber;
                                                                            List<String> teams = new ArrayList<>(List.of(Arrays.copyOfRange(StoryUtils.TEAMS, 0, teamsNumber - 1)));
                                                                            ArrayList<Integer> _players = new ArrayList<>(Collections.nCopies(teamsNumber, 0));

                                                                            if (players.size() < minPlayers) {
                                                                                StoryUtils.runAsFunctions(server, context.getSource(), onFailure);
                                                                                return 1;
                                                                            }

                                                                            Collections.shuffle(players);
                                                                            for (ServerPlayer player : players) {
                                                                                if (maxPlayers == 0) {
                                                                                    StoryUtils.joinTeam(server, player.getScoreboardName(), StoryUtils.TEAM_SPECTATORS);
                                                                                    continue;
                                                                                }
                                                                                int team = MiniGames.random.nextInt(teams.size());
                                                                                StoryUtils.joinTeam(server, player.getScoreboardName(), teams.get(team));
                                                                                maxPlayers--;
                                                                                _players.set(team, _players.get(team) + 1);
                                                                                if (_players.get(team) == maxPlayersPerTeam) {
                                                                                    _players.remove(team);
                                                                                    teams.remove(team);
                                                                                }
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
        );
    }
}
