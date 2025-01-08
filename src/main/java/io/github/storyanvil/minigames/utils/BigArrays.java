package io.github.storyanvil.minigames.utils;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BigArrays {
    public static ArrayList<String> guessableBlocks = new ArrayList<>(List.of("minecraft:oak_log",
            "minecraft:oak_planks", "minecraft:oak_fence",
            "minecraft:oak_fence_gate", "minecraft:spruce_log", "minecraft:spruce_planks", "minecraft:spruce_fence",
            "minecraft:spruce_fence_gate", "minecraft:birch_log", "minecraft:birch_planks", "minecraft:birch_fence",
            "minecraft:birch_fence_gate", "minecraft:jungle_log", "minecraft:jungle_planks", "minecraft:jungle_fence",
            "minecraft:jungle_fence_gate", "minecraft:acacia_log", "minecraft:acacia_planks", "minecraft:acacia_fence",
            "minecraft:acacia_fence_gate", "minecraft:dark_oak_log", "minecraft:dark_oak_planks", "minecraft:dark_oak_fence",
            "minecraft:dark_oak_fence_gate", "minecraft:mangrove_log", "minecraft:mangrove_planks", "minecraft:mangrove_fence",
            "minecraft:mangrove_fence_gate", "minecraft:cherry_log", "minecraft:cherry_planks", "minecraft:cherry_fence",
            "minecraft:cherry_fence_gate", "minecraft:bamboo_block", "minecraft:bamboo_planks", "minecraft:bamboo_mosaic",
            "minecraft:bamboo_fence", "minecraft:bamboo_fence_gate", "minecraft:crimson_stem", "minecraft:crimson_planks",
            "minecraft:crimson_fence", "minecraft:crimson_fence_gate", "minecraft:warped_stem", "minecraft:warped_planks",
            "minecraft:warped_fence", "minecraft:warped_fence_gate", "minecraft:cobblestone", "minecraft:stone",
            "minecraft:chiseled_stone_bricks", "minecraft:stone_bricks", "minecraft:diorite", "minecraft:andesite",
            "minecraft:polished_diorite", "minecraft:polished_andesite", "minecraft:deepslate",
            "minecraft:polished_deepslate", "minecraft:deepslate_tiles", "minecraft:deepslate_bricks",
            "minecraft:reinforced_deepslate", "minecraft:bricks", "minecraft:packed_mud", "minecraft:mud_bricks",
            "minecraft:sandstone", "minecraft:red_sandstone", "minecraft:sea_lantern", "minecraft:prismarine",
            "minecraft:dark_prismarine", "minecraft:netherrack", "minecraft:nether_bricks", "minecraft:basalt",
            "minecraft:chiseled_polished_blackstone", "minecraft:blackstone", "minecraft:end_stone", "minecraft:purpur_block",
            "minecraft:coal_block", "minecraft:iron_block", "minecraft:iron_bars", "minecraft:gold_block",
            "minecraft:redstone_block", "minecraft:emerald_block", "minecraft:lapis_block", "minecraft:diamond_block",
            "minecraft:netherite_block", "minecraft:quartz_block", "minecraft:quartz_bricks", "minecraft:quartz_pillar",
            "minecraft:amethyst_block", "minecraft:waxed_copper_block", "minecraft:waxed_exposed_copper",
            "minecraft:waxed_weathered_copper", "minecraft:waxed_oxidized_copper", "minecraft:waxed_oxidized_cut_copper",
            "minecraft:waxed_weathered_cut_copper", "minecraft:waxed_exposed_cut_copper", "minecraft:waxed_cut_copper",
            "minecraft:white_wool", "minecraft:light_gray_wool", "minecraft:gray_wool",
            "minecraft:black_wool", "minecraft:brown_wool", "minecraft:red_wool",
            "minecraft:orange_wool", "minecraft:yellow_wool", "minecraft:lime_wool",
            "minecraft:green_wool", "minecraft:cyan_wool", "minecraft:light_blue_wool",
            "minecraft:blue_wool", "minecraft:purple_wool", "minecraft:magenta_wool",
            "minecraft:pink_wool", "minecraft:terracotta", "minecraft:white_concrete",
            "minecraft:light_gray_concrete", "minecraft:gray_concrete", "minecraft:black_concrete",
            "minecraft:brown_concrete", "minecraft:red_concrete", "minecraft:orange_concrete",
            "minecraft:yellow_concrete", "minecraft:lime_concrete", "minecraft:green_concrete",
            "minecraft:cyan_concrete", "minecraft:light_blue_concrete", "minecraft:blue_concrete",
            "minecraft:purple_concrete", "minecraft:magenta_concrete", "minecraft:pink_concrete",
            "minecraft:white_concrete_powder", "minecraft:light_gray_concrete_powder", "minecraft:gray_concrete_powder",
            "minecraft:black_concrete_powder", "minecraft:brown_concrete_powder", "minecraft:red_concrete_powder",
            "minecraft:orange_concrete_powder", "minecraft:yellow_concrete_powder", "minecraft:lime_concrete_powder",
            "minecraft:green_concrete_powder", "minecraft:cyan_concrete_powder", "minecraft:light_blue_concrete_powder",
            "minecraft:blue_concrete_powder", "minecraft:purple_concrete_powder", "minecraft:magenta_concrete_powder",
            "minecraft:pink_concrete_powder", "minecraft:white_glazed_terracotta", "minecraft:light_gray_glazed_terracotta",
            "minecraft:gray_glazed_terracotta", "minecraft:black_glazed_terracotta", "minecraft:brown_glazed_terracotta",
            "minecraft:red_glazed_terracotta", "minecraft:orange_glazed_terracotta", "minecraft:yellow_glazed_terracotta",
            "minecraft:lime_glazed_terracotta", "minecraft:green_glazed_terracotta", "minecraft:cyan_glazed_terracotta",
            "minecraft:light_blue_glazed_terracotta", "minecraft:blue_glazed_terracotta", "minecraft:purple_glazed_terracotta",
            "minecraft:magenta_glazed_terracotta", "minecraft:pink_glazed_terracotta", "minecraft:white_stained_glass",
            "minecraft:light_gray_stained_glass", "minecraft:gray_stained_glass", "minecraft:black_stained_glass",
            "minecraft:brown_stained_glass", "minecraft:red_stained_glass", "minecraft:orange_stained_glass",
            "minecraft:yellow_stained_glass", "minecraft:lime_stained_glass", "minecraft:green_stained_glass",
            "minecraft:cyan_stained_glass", "minecraft:light_blue_stained_glass", "minecraft:blue_stained_glass",
            "minecraft:purple_stained_glass", "minecraft:magenta_stained_glass", "minecraft:pink_stained_glass", "minecraft:glass",
            "minecraft:tinted_glass", "minecraft:white_candle", "minecraft:light_gray_candle", "minecraft:gray_candle",
            "minecraft:black_candle", "minecraft:brown_candle", "minecraft:red_candle", "minecraft:orange_candle",
            "minecraft:yellow_candle", "minecraft:lime_candle", "minecraft:green_candle", "minecraft:cyan_candle",
            "minecraft:light_blue_candle", "minecraft:blue_candle", "minecraft:purple_candle", "minecraft:magenta_candle",
            "minecraft:pink_candle", "minecraft:grass_block", "minecraft:podzol", "minecraft:mycelium", "minecraft:dirt_path",
            "minecraft:coarse_dirt", "minecraft:rooted_dirt", "minecraft:mud", "minecraft:clay", "minecraft:gravel",
            "minecraft:sand", "minecraft:red_sand", "minecraft:packed_ice", "minecraft:snow_block", "minecraft:moss_block",
            "minecraft:calcite", "minecraft:tuff", "minecraft:dripstone_block", "minecraft:magma_block", "minecraft:obsidian",
            "minecraft:crying_obsidian", "minecraft:crimson_nylium", "minecraft:warped_nylium", "minecraft:soul_sand",
            "minecraft:soul_soil", "minecraft:bone_block", "minecraft:coal_ore", "minecraft:deepslate_coal_ore", "minecraft:iron_ore",
            "minecraft:deepslate_iron_ore", "minecraft:copper_ore", "minecraft:deepslate_copper_ore", "minecraft:gold_ore",
            "minecraft:deepslate_gold_ore", "minecraft:redstone_ore", "minecraft:deepslate_redstone_ore", "minecraft:emerald_ore",
            "minecraft:deepslate_emerald_ore", "minecraft:lapis_ore", "minecraft:deepslate_lapis_ore", "minecraft:diamond_ore",
            "minecraft:deepslate_diamond_ore", "minecraft:ancient_debris", "minecraft:raw_iron_block", "minecraft:raw_copper_block",
            "minecraft:raw_gold_block", "minecraft:glowstone", "minecraft:mangrove_roots", "minecraft:mushroom_stem",
            "minecraft:red_mushroom_block", "minecraft:nether_wart_block", "minecraft:warped_wart_block", "minecraft:shroomlight", "minecraft:dried_kelp_block",
            "minecraft:dead_tube_coral_block", "minecraft:dead_brain_coral_block", "minecraft:dead_bubble_coral_block", "minecraft:sponge",
            "minecraft:melon", "minecraft:pumpkin", "minecraft:carved_pumpkin", "minecraft:sculk", "minecraft:crafting_table",
            "minecraft:redstone_lamp", "minecraft:furnace", "minecraft:smoker", "minecraft:blast_furnace", "minecraft:stonecutter",
            "minecraft:bedrock", "minecraft:cartography_table", "minecraft:fletching_table", "minecraft:smithing_table",
            "minecraft:grindstone", "minecraft:loom", "minecraft:note_block", "minecraft:jukebox", "minecraft:enchanting_table",
            "minecraft:campfire", "minecraft:soul_campfire", "minecraft:anvil", "minecraft:composter",
            "minecraft:lodestone", "minecraft:cauldron", "minecraft:bell", "minecraft:lightning_rod", "minecraft:beacon",
            "minecraft:beehive", "minecraft:scaffolding", "minecraft:bookshelf", "minecraft:chiseled_bookshelf",
            "minecraft:lectern", "minecraft:chest", "minecraft:barrel", "minecraft:piston", "minecraft:sticky_piston",
            "minecraft:dispenser", "minecraft:dropper", "minecraft:hopper", "minecraft:observer",
            "minecraft:rail", "minecraft:powered_rail", "minecraft:detector_rail", "minecraft:activator_rail"));

    public static final HashMap<Integer, BlockPos> player1pos = new HashMap<>();
    public static final HashMap<Integer, BlockPos> player2pos = new HashMap<>();

    static {
        player1pos.put(0, new BlockPos(15024, -53, 272));
        player1pos.put(1, new BlockPos(15024, -53, 274));
        player1pos.put(2, new BlockPos(15024, -53, 276));
        player1pos.put(3, new BlockPos(15024, -53, 278));
        player1pos.put(4, new BlockPos(15024, -53, 280));
        player1pos.put(5, new BlockPos(15022, -53, 272));
        player1pos.put(6, new BlockPos(15022, -53, 274));
        player1pos.put(7, new BlockPos(15022, -53, 276));
        player1pos.put(8, new BlockPos(15022, -53, 278));
        player1pos.put(9, new BlockPos(15022, -53, 280));
        player1pos.put(10, new BlockPos(15020, -53, 272));
        player1pos.put(11, new BlockPos(15020, -53, 274));
        player1pos.put(12, new BlockPos(15020, -53, 276));
        player1pos.put(13, new BlockPos(15020, -53, 278));
        player1pos.put(14, new BlockPos(15020, -53, 280));

        player2pos.put(0, new BlockPos(15030, -53, 280));
        player2pos.put(1, new BlockPos(15030, -53, 278));
        player2pos.put(2, new BlockPos(15030, -53, 276));
        player2pos.put(3, new BlockPos(15030, -53, 274));
        player2pos.put(4, new BlockPos(15030, -53, 272));
        player2pos.put(5, new BlockPos(15032, -53, 280));
        player2pos.put(6, new BlockPos(15032, -53, 278));
        player2pos.put(7, new BlockPos(15032, -53, 276));
        player2pos.put(8, new BlockPos(15032, -53, 274));
        player2pos.put(9, new BlockPos(15032, -53, 272));
        player2pos.put(10, new BlockPos(15034, -53, 280));
        player2pos.put(11, new BlockPos(15034, -53, 278));
        player2pos.put(12, new BlockPos(15034, -53, 276));
        player2pos.put(13, new BlockPos(15034, -53, 274));
        player2pos.put(14, new BlockPos(15034, -53, 272));
    }
}
