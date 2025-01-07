package io.github.storyanvil.minigames.scripts;

import com.mojang.brigadier.context.CommandContext;
import io.github.storyanvil.minigames.Config;
import io.github.storyanvil.minigames.MiniGames;
import io.github.storyanvil.minigames.utils.BigArrays;
import io.github.storyanvil.minigames.utils.StoryUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.SetBlockCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.Optional;

public class MiniGamesScripts {
    public interface SharedScript {
        int execute(CommandContext<CommandSourceStack> context, MinecraftServer server, Level level);
    }
    public static int shared(SharedScript script, CommandContext<CommandSourceStack> context) {
        MinecraftServer server = context.getSource().getServer();
        return script.execute(context, server, context.getSource().getLevel());
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
    private static void setBoth(Level level, int x1, int y1, int z1, int x2, int y2, int z2, String block) {
        BlockState b = ForgeRegistries.BLOCKS.getDelegate(ResourceLocation.tryParse(block)).get().get().defaultBlockState();

        level.setBlock(BlockPos.containing(x1, y1, z1), b, 2);
        level.setBlock(BlockPos.containing(x2, y2, z2), b, 2);
    }
    public static int g000_007_pre(CommandContext<CommandSourceStack> context, MinecraftServer server, Level level) {
        Collections.shuffle(BigArrays.blocks, MiniGames.random);

        setBoth(level, 15024, -53, 272, 15030, -53, 280, BigArrays.blocks.get(0));
        setBoth(level, 15024, -53, 274, 15030, -53, 278, BigArrays.blocks.get(1));
        setBoth(level, 15024, -53, 276, 15030, -53, 276, BigArrays.blocks.get(2));
        setBoth(level, 15024, -53, 278, 15030, -53, 274, BigArrays.blocks.get(3));
        setBoth(level, 15024, -53, 280, 15030, -53, 272, BigArrays.blocks.get(4));
        setBoth(level, 15022, -53, 272, 15032, -53, 280, BigArrays.blocks.get(5));
        setBoth(level, 15022, -53, 274, 15032, -53, 278, BigArrays.blocks.get(6));
        setBoth(level, 15022, -53, 276, 15032, -53, 276, BigArrays.blocks.get(7));
        setBoth(level, 15022, -53, 278, 15032, -53, 274, BigArrays.blocks.get(8));
        setBoth(level, 15022, -53, 280, 15032, -53, 272, BigArrays.blocks.get(9));

        return 0;
    }
}
