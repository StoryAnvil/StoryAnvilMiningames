package io.github.storyanvil.minigames.scripts;

import com.mojang.brigadier.context.CommandContext;
import io.github.storyanvil.minigames.MiniGames;
import io.github.storyanvil.minigames.utils.StoryUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class MiniGamesScripts {
    public static int g000_001_randomize_word(CommandContext<CommandSourceStack> context) {
        MinecraftServer server = context.getSource().getServer();

        // Generate id of random word
        Component word = Component.translatable("story.storyanvil.g000_001_" + MiniGames.random.nextInt(1, 51)); // from 1 to 50

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
}
