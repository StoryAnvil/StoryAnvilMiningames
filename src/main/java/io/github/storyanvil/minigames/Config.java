package io.github.storyanvil.minigames;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = MiniGames.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.ConfigValue<Integer> ___G000_001_WORD_COUNT = BUILDER
            .comment("Defines word count for G000_001")
            .define("G000_001_WORD_COUNT", 50);
    private static int G000_001_WORD_COUNT = 50;

    private static final ForgeConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
        G000_001_WORD_COUNT = ___G000_001_WORD_COUNT.get();
    }

    public static int G000_001_WordCount() {
        return G000_001_WORD_COUNT;
    }
}
