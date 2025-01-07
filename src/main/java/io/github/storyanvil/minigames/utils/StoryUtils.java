package io.github.storyanvil.minigames.utils;

import io.github.storyanvil.minigames.MiniGames;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;

import java.awt.*;
import java.util.Collection;
import java.util.Optional;

import static io.github.storyanvil.minigames.MiniGames.LOGGER;

public class StoryUtils {
    public static final String TEAM_RED = "RED";
    public static final String TEAM_GREEN = "GREEN";
    public static final String TEAM_BLUE = "BLUE";
    public static final String TEAM_YELLOW = "YELLOW";
    public static final String TEAM_SPECTATORS = "SPECTATORS";
    public static final String[] TEAMS = new String[]{TEAM_RED, TEAM_BLUE, TEAM_YELLOW, TEAM_GREEN};
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
    public static void runAs(CommandSourceStack source, String command) {
        source.getServer().getCommands().performPrefixedCommand(source, command);
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
    public static void mimicWhisper(Component from, Component text, Player player) {
        player.sendSystemMessage(Component.translatable("commands.message.display.incoming", from, text).withStyle(style -> style.withColor(TextColor.fromLegacyFormat(ChatFormatting.GRAY)).withItalic(true)));
    }
    public static void mayFly(ServerPlayer player, boolean may) {
        player.getAbilities().mayfly = may;
    }
    public static void mayTakeNoDamage(ServerPlayer player, boolean may) {
        player.getAbilities().invulnerable = may;
    }
    public static void mayInstabuild(ServerPlayer player, boolean may) {
        player.getAbilities().instabuild = may;
    }
}
