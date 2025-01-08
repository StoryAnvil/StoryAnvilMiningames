package io.github.storyanvil.minigames.utils;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.ScoreboardCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static io.github.storyanvil.minigames.MiniGames.LOGGER;

public class StoryUtils {
    public static final String TEAM_RED = "RED";
    public static final String TEAM_GREEN = "GREEN";
    public static final String TEAM_BLUE = "BLUE";
    public static final String TEAM_YELLOW = "YELLOW";
    public static final String TEAM_SPECTATORS = "SPECTATORS";
    public static final String[] TEAMS = new String[]{TEAM_RED, TEAM_BLUE, TEAM_YELLOW, TEAM_GREEN};

    ///////////////////////////////////////////////////////////////////////////
    // SCOREBOARDS
    ///////////////////////////////////////////////////////////////////////////
    public static boolean ifGame(MinecraftServer server, int gameID) {
        return getDataScore(server, "_") == gameID;
    }
    public static void setDataScore(MinecraftServer server, String key, int value) {
        setScoreboard(server, "story", key, value);
    }
    public static int getDataScore(MinecraftServer server, String key) {
        return getScoreboard(server, "story", key);
    }
    public static void setScoreboard(MinecraftServer server, String _scoreboard, String key, int value) {
        Scoreboard scoreboard = server.getScoreboard();
        Score score = scoreboard.getOrCreatePlayerScore(key, scoreboard.getObjective(_scoreboard));
        score.setScore(value);
    }
    public static int addScoreboard(MinecraftServer server, String _scoreboard, String key, int value) {
        Scoreboard scoreboard = server.getScoreboard();
        Score score = scoreboard.getOrCreatePlayerScore(key, scoreboard.getObjective(_scoreboard));
        score.setScore(score.getScore() + value);
        return score.getScore();
    }
    public static void setScoreboard(Objective objective, String key, int value) {
        Score score = objective.getScoreboard().getOrCreatePlayerScore(key, objective);
        score.setScore(value);
    }
    public static int getScoreboard(MinecraftServer server, String _scoreboard, String key) {
        Scoreboard scoreboard = server.getScoreboard();
        Score score = scoreboard.getOrCreatePlayerScore(key, scoreboard.getObjective(_scoreboard));
        return score.getScore();
    }
    public static int getScoreboard(Objective objective, String key) {
        Score score = objective.getScoreboard().getOrCreatePlayerScore(key, objective);
        return score.getScore();
    }
    public static Objective createObjective(MinecraftServer server, String name, ObjectiveCriteria criteria) {
        Scoreboard scoreboard = server.getScoreboard();
        return scoreboard.addObjective(name, criteria, Component.literal(name), criteria.getDefaultRenderType());
    }
    public static void removeObjective(MinecraftServer server, String name) {
        Scoreboard scoreboard = server.getScoreboard();
        Objective objective = scoreboard.getObjective(name);
        if (objective != null) {
            scoreboard.removeObjective(objective);
        }
    }
    public static void joinTeam(MinecraftServer server, String player, String team) {
        Scoreboard scoreboard = server.getScoreboard();
        scoreboard.addPlayerToTeam(player, scoreboard.getPlayerTeam(team));
    }

    ///////////////////////////////////////////////////////////////////////////
    // RUNAS
    ///////////////////////////////////////////////////////////////////////////
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

    ///////////////////////////////////////////////////////////////////////////
    // PLAYERS
    ///////////////////////////////////////////////////////////////////////////
    public static void mimicWhisper(Component from, Component text, Player player) {
        player.sendSystemMessage(Component.translatable("commands.message.display.incoming", from, text).withStyle(style -> style.withColor(TextColor.fromLegacyFormat(ChatFormatting.GRAY)).withItalic(true)));
    }
    public static void sendTitle(ServerPlayer target, Component title) {
        Function<Component, Packet<?>> pPacketGetter = ClientboundSetTitleTextPacket::new;
        try {
            target.connection.send(pPacketGetter.apply(ComponentUtils.updateForEntity(s(target.getServer(), target.serverLevel()), title, target, 0)));
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // MISC
    ///////////////////////////////////////////////////////////////////////////
    public static CommandSourceStack s(MinecraftServer server, ServerLevel level) {
        return new CommandSourceStack(CommandSource.NULL, new Vec3(0, 0, 0), new Vec2(0, 0), level, 4, "SERVER", Component.literal("SERVER"), server, null);
    }
    public static void mayFly(ServerPlayer player, boolean may) {
        player.getAbilities().mayfly = may; //FIXME
    }
    public static void mayTakeNoDamage(ServerPlayer player, boolean may) {
        player.getAbilities().invulnerable = may;
    }
    public static void mayInstabuild(ServerPlayer player, boolean may) {
        player.getAbilities().instabuild = may; //FIXME
    }

    public static int backFindPos(HashMap<Integer, BlockPos> repo, BlockPos toFind) {
        int r = -1;
        for (Integer i : repo.keySet()) {
            if (repo.get(i).equals(toFind)) {
                r = i;
                break;
            }
        }

        return r;
    }
}
