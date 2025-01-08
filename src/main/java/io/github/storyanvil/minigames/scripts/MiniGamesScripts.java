package io.github.storyanvil.minigames.scripts;

import com.mojang.brigadier.context.CommandContext;
import io.github.storyanvil.minigames.Config;
import io.github.storyanvil.minigames.MiniGames;
import io.github.storyanvil.minigames.utils.BigArrays;
import io.github.storyanvil.minigames.utils.StoryUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.TitleCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;

import static io.github.storyanvil.minigames.MiniGames.LOGGER;

public class MiniGamesScripts {
    public interface SharedScript {
        int execute(CommandContext<CommandSourceStack> context, MinecraftServer server, Level level);
    }
    public static int shared(SharedScript script, CommandContext<CommandSourceStack> context) {
        MinecraftServer server = context.getSource().getServer();
        return script.execute(context, server, context.getSource().getLevel());
    }
    public static int quiet(SharedScript script, CommandContext<CommandSourceStack> context) {
        MinecraftServer server = context.getSource().getServer();
        return script.execute(null, server, context.getSource().getLevel());
    }
    public static int g000_001_randomize_word(CommandContext<CommandSourceStack> context, MinecraftServer server, Level level) {
        // Generate id of random word
        Component word = Component.translatable("story.storyanvil.g000_001_" + MiniGames.random.nextInt(1, Config.G000_001_WordCount() + 1)); // from 1 to 50

        // Broadcast word to players
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.getTags().contains("g000_001_word")) {
                StoryUtils.mimicWhisper(Component.translatable("name.storyanvil.dasha"), Component.translatable("story.storyanvil.g000_001_word", word), player);
            } else if (player.getTags().contains("g000_001_no_word")) {
                StoryUtils.mimicWhisper(Component.translatable("name.storyanvil.dasha"), Component.translatable("story.storyanvil.g000_001_no_word"), player);
            }
        }
        return 0;
    }
    private static void setBoth(Level level, BlockPos p1, BlockPos p2, String block) {
        BlockState b = ForgeRegistries.BLOCKS.getDelegate(ResourceLocation.tryParse(block)).get().get().defaultBlockState();

        level.setBlock(p1, b, 2);
        level.setBlock(p2, b, 2);
    }
    private static void setBoth(Level level, BlockPos p1, BlockPos p2, BlockState block) {
        level.setBlock(p1, block, 2);
        level.setBlock(p2, block, 2);
    }
    public static int g000_007_pre(CommandContext<CommandSourceStack> context, MinecraftServer server, Level level) {
        // Prepare the scoreboard
        StoryUtils.removeObjective(server, "g000_007");
        Objective objective = StoryUtils.createObjective(server, "g000_007", ObjectiveCriteria.DUMMY);
        StoryUtils.setScoreboard(objective, "state", 0);


        // Prepare the blocks
        setBoth(level, new BlockPos(15011, -53, 292), new BlockPos(15013, -53, 292), Blocks.AIR.defaultBlockState());

        Collections.shuffle(BigArrays.guessableBlocks, MiniGames.random);

        BlockState bsP1 = level.getBlockState(new BlockPos(15011, -53, 294));
        BlockState bsP2 = level.getBlockState(new BlockPos(15013, -53, 294));
        for (int i = 0; i < 15; i++) {
            setBoth(level, BigArrays.player1pos.get(i), BigArrays.player2pos.get(i), BigArrays.guessableBlocks.get(i));
            level.setBlock(BigArrays.player1pos.get(i).above(), bsP1, 2);
            level.setBlock(BigArrays.player2pos.get(i).above(), bsP2, 2);
        }

        return 0;
    }

    public static int g000_007_state_1(CommandContext<CommandSourceStack> __NULL__, MinecraftServer server, Level level) {
        Objective objective = server.getScoreboard().getObjective("g000_007");
        assert objective != null;
        StoryUtils.setScoreboard(objective, "state", 1);
        StoryUtils.setScoreboard(objective, "p1Left", BigArrays.player1pos.size());
        StoryUtils.setScoreboard(objective, "p2Left", BigArrays.player2pos.size());
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            StoryUtils.sendTitle(player, Component.translatable("story.storyanvil.g000_007_p1_question"));
        }
        return 0;
    }



    public static void registerEnd(MinecraftServer server, CommandSourceStack context) {
        StoryUtils.setDataScore(server, "_", 0);
        server.getPlayerList().getPlayers().forEach(player -> {
            StoryUtils.mayFly(player, false);
        });
        StoryUtils.runAsFunction(server, context, ResourceLocation.tryParse("storyanvil:game/spawn"));
        LOGGER.info("Game stopped!");
    }
}
