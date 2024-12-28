package io.github.storyanvil.minigames.utils;

import io.github.storyanvil.minigames.MiniGames;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;

import java.util.Collection;
import java.util.Optional;

import static io.github.storyanvil.minigames.MiniGames.LOGGER;

public class StoryUtils {
    public static final String TEAM_RED = "RED";
    public static final String TEAM_GREEN = "GREEN";
    public static final String TEAM_BLUE = "BLUE";
    public static final String TEAM_YELLOW = "YELLOW";
    public static final String TEAM_SPECTATORS = "SPECTATORS";
    public static final String[] TEAMS = new String[]{TEAM_RED, TEAM_BLUE, TEAM_YELLOW, TEAM_BLUE};
    public static void setDataScore(MinecraftServer server, String key, int value) {
        Scoreboard scoreboard = server.getScoreboard();
        Score score = scoreboard.getOrCreatePlayerScore(key, scoreboard.getObjective("story"));
        score.setScore(value);
    }
    public static int getDataScore(MinecraftServer server, String key) {
        Scoreboard scoreboard = server.getScoreboard();
        Score score = scoreboard.getOrCreatePlayerScore(key, scoreboard.getObjective("story"));
        return score.getScore();
    }
    public static void joinTeam(MinecraftServer server, String player, String team) {
        Scoreboard scoreboard = server.getScoreboard();
        scoreboard.addPlayerToTeam(player, scoreboard.getPlayerTeam(team));
    }
    public static void runAs(MinecraftServer server, CommandSourceStack source, String command) {
        server.getCommands().performPrefixedCommand(source, command);
    }
    public static void runAsFunction(MinecraftServer server, CommandSourceStack source, ResourceLocation functionLocation) {
        Optional<CommandFunction> function = server.getFunctions().get(functionLocation);
        if (function.isEmpty()) {
            LOGGER.warn("Function " + functionLocation + " missing!");
            return;
        }
        server.getFunctions().execute(function.get(), source);
    }
    public static void runAsFunctions(MinecraftServer server, CommandSourceStack stack, Collection<CommandFunction> functions) {
        for (CommandFunction function : functions) {
            server.getFunctions().execute(function, stack);
        }
    }
}
