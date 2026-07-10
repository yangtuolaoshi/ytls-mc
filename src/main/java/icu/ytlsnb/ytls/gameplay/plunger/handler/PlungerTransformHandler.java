package icu.ytlsnb.ytls.gameplay.plunger.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public final class PlungerTransformHandler {
    private static final Map<Block, Block> SIMPLE_TRANSFORMS = new HashMap<>();
    private static final Map<Block, Block> ORE_TRANSFORMS = new HashMap<>();
    private static final Map<Block, Item> ORE_DROPS = new HashMap<>();
    private static final Map<Block, Block> INFESTED_TRANSFORMS = new HashMap<>();
    private static final Map<Block, Block> DYED_TRANSFORMS = new HashMap<>();
    private static final Map<Block, Block> COPPER_TRANSFORMS = new HashMap<>();

    static {
        SIMPLE_TRANSFORMS.put(Blocks.GRASS_BLOCK, Blocks.DIRT);

        registerLogs();
        registerOres();
        registerInfested();
        registerDyed();
        registerCopper();
    }

    private PlungerTransformHandler() {
    }

    public static boolean tryTransform(Level level, BlockPos pos, BlockState state, Player player, int fortune) {
        Block block = state.getBlock();

        if (block instanceof SpawnerBlock) {
            return handleSpawner(level, pos, fortune);
        }

        Block target = SIMPLE_TRANSFORMS.get(block);
        if (target == null) {
            target = ORE_TRANSFORMS.get(block);
        }
        if (target == null) {
            target = INFESTED_TRANSFORMS.get(block);
        }
        if (target == null) {
            target = DYED_TRANSFORMS.get(block);
        }
        if (target == null) {
            target = COPPER_TRANSFORMS.get(block);
        }
        if (target == null) {
            return false;
        }

        if (level instanceof ServerLevel serverLevel) {
            Item drop = ORE_DROPS.get(block);
            if (drop != null) {
                PlungerBlockHandler.spawnDrops(serverLevel, pos, new ItemStack(drop), fortune);
            }
            if (INFESTED_TRANSFORMS.containsKey(block)) {
                spawnSilverfish(serverLevel, pos, 1 + fortune);
            }
            level.setBlock(pos, target.defaultBlockState(), Block.UPDATE_ALL);
        }
        return true;
    }

    private static boolean handleSpawner(Level level, BlockPos pos, int fortune) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof SpawnerBlockEntity spawnerBe)) {
            return false;
        }
        Entity display = spawnerBe.getSpawner().getOrCreateDisplayEntity(level, pos);
        if (display == null) {
            return false;
        }
        EntityType<?> type = display.getType();
        if (!(level instanceof ServerLevel serverLevel)) {
            return true;
        }
        int count = 5 + serverLevel.random.nextInt(4) + fortune * 2;
        for (int i = 0; i < count; i++) {
            type.spawn(serverLevel, pos, MobSpawnType.SPAWNER);
        }
        return true;
    }

    private static void spawnSilverfish(ServerLevel level, BlockPos pos, int count) {
        for (int i = 0; i < count; i++) {
            Silverfish fish = EntityType.SILVERFISH.create(level);
            if (fish != null) {
                fish.moveTo(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, level.random.nextFloat() * 360.0F, 0.0F);
                level.addFreshEntity(fish);
            }
        }
    }

    private static void registerLogs() {
        for (var entry : ForgeRegistries.BLOCKS.getEntries()) {
            Block block = entry.getValue();
            ResourceLocation id = entry.getKey().location();
            String name = id.getPath();
            if (name.endsWith("_log") && !name.startsWith("stripped_")) {
                Block stripped = ForgeRegistries.BLOCKS.getValue(id.withPrefix("stripped_"));
                if (stripped != null && stripped != Blocks.AIR) {
                    SIMPLE_TRANSFORMS.put(block, stripped);
                }
            }
        }
    }

    private static void registerOres() {
        ore(Blocks.COAL_ORE, Blocks.STONE, Items.COAL);
        ore(Blocks.DEEPSLATE_COAL_ORE, Blocks.DEEPSLATE, Items.COAL);
        ore(Blocks.IRON_ORE, Blocks.STONE, Items.RAW_IRON);
        ore(Blocks.DEEPSLATE_IRON_ORE, Blocks.DEEPSLATE, Items.RAW_IRON);
        ore(Blocks.COPPER_ORE, Blocks.STONE, Items.RAW_COPPER);
        ore(Blocks.DEEPSLATE_COPPER_ORE, Blocks.DEEPSLATE, Items.RAW_COPPER);
        ore(Blocks.GOLD_ORE, Blocks.STONE, Items.RAW_GOLD);
        ore(Blocks.DEEPSLATE_GOLD_ORE, Blocks.DEEPSLATE, Items.RAW_GOLD);
        ore(Blocks.REDSTONE_ORE, Blocks.STONE, Items.REDSTONE);
        ore(Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.DEEPSLATE, Items.REDSTONE);
        ore(Blocks.LAPIS_ORE, Blocks.STONE, Items.LAPIS_LAZULI);
        ore(Blocks.DEEPSLATE_LAPIS_ORE, Blocks.DEEPSLATE, Items.LAPIS_LAZULI);
        ore(Blocks.DIAMOND_ORE, Blocks.STONE, Items.DIAMOND);
        ore(Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.DEEPSLATE, Items.DIAMOND);
        ore(Blocks.EMERALD_ORE, Blocks.STONE, Items.EMERALD);
        ore(Blocks.DEEPSLATE_EMERALD_ORE, Blocks.DEEPSLATE, Items.EMERALD);
        ore(Blocks.NETHER_GOLD_ORE, Blocks.NETHERRACK, Items.GOLD_NUGGET);
        ore(Blocks.NETHER_QUARTZ_ORE, Blocks.NETHERRACK, Items.QUARTZ);
        ore(Blocks.ANCIENT_DEBRIS, Blocks.NETHERRACK, Items.NETHERITE_SCRAP);
    }

    private static void ore(Block ore, Block stone, Item drop) {
        ORE_TRANSFORMS.put(ore, stone);
        ORE_DROPS.put(ore, drop);
    }

    private static void registerInfested() {
        INFESTED_TRANSFORMS.put(Blocks.INFESTED_STONE, Blocks.STONE);
        INFESTED_TRANSFORMS.put(Blocks.INFESTED_COBBLESTONE, Blocks.COBBLESTONE);
        INFESTED_TRANSFORMS.put(Blocks.INFESTED_STONE_BRICKS, Blocks.STONE_BRICKS);
        INFESTED_TRANSFORMS.put(Blocks.INFESTED_MOSSY_STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS);
        INFESTED_TRANSFORMS.put(Blocks.INFESTED_CRACKED_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS);
        INFESTED_TRANSFORMS.put(Blocks.INFESTED_CHISELED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS);
        INFESTED_TRANSFORMS.put(Blocks.INFESTED_DEEPSLATE, Blocks.DEEPSLATE);
    }

    private static void registerDyed() {
        dyedWool();
        dyedTerracotta();
        dyedGlass();
        dyedConcrete();
        dyedConcretePowder();
        dyedCarpet();
        dyedBed();
        dyedShulkerBox();
        dyedCandle();
        dyedBanner();
    }

    private static void dyedWool() {
        for (var entry : ForgeRegistries.BLOCKS.getEntries()) {
            String path = entry.getKey().location().getPath();
            if (path.endsWith("_wool") && !path.equals("white_wool")) {
                DYED_TRANSFORMS.put(entry.getValue(), Blocks.WHITE_WOOL);
            }
        }
    }

    private static void dyedTerracotta() {
        for (var entry : ForgeRegistries.BLOCKS.getEntries()) {
            String path = entry.getKey().location().getPath();
            if (path.endsWith("_terracotta") && !path.equals("terracotta") && !path.equals("white_terracotta")) {
                DYED_TRANSFORMS.put(entry.getValue(), Blocks.WHITE_TERRACOTTA);
            }
        }
    }

    private static void dyedGlass() {
        for (var entry : ForgeRegistries.BLOCKS.getEntries()) {
            String path = entry.getKey().location().getPath();
            if (path.endsWith("_stained_glass") || (path.endsWith("_glass") && path.contains("_") && !path.equals("tinted_glass"))) {
                if (path.contains("stained") || !path.equals("glass")) {
                    DYED_TRANSFORMS.put(entry.getValue(), Blocks.GLASS);
                }
            }
        }
    }

    private static void dyedConcrete() {
        for (var entry : ForgeRegistries.BLOCKS.getEntries()) {
            String path = entry.getKey().location().getPath();
            if (path.endsWith("_concrete") && !path.equals("white_concrete")) {
                DYED_TRANSFORMS.put(entry.getValue(), Blocks.WHITE_CONCRETE);
            }
        }
    }

    private static void dyedConcretePowder() {
        for (var entry : ForgeRegistries.BLOCKS.getEntries()) {
            String path = entry.getKey().location().getPath();
            if (path.endsWith("_concrete_powder") && !path.equals("white_concrete_powder")) {
                DYED_TRANSFORMS.put(entry.getValue(), Blocks.WHITE_CONCRETE_POWDER);
            }
        }
    }

    private static void dyedCarpet() {
        for (var entry : ForgeRegistries.BLOCKS.getEntries()) {
            String path = entry.getKey().location().getPath();
            if (path.endsWith("_carpet") && !path.equals("white_carpet")) {
                DYED_TRANSFORMS.put(entry.getValue(), Blocks.WHITE_CARPET);
            }
        }
    }

    private static void dyedBed() {
        for (var entry : ForgeRegistries.BLOCKS.getEntries()) {
            String path = entry.getKey().location().getPath();
            if (path.endsWith("_bed") && !path.equals("white_bed")) {
                DYED_TRANSFORMS.put(entry.getValue(), Blocks.WHITE_BED);
            }
        }
    }

    private static void dyedShulkerBox() {
        for (var entry : ForgeRegistries.BLOCKS.getEntries()) {
            String path = entry.getKey().location().getPath();
            if (path.endsWith("shulker_box") && !path.equals("shulker_box")) {
                DYED_TRANSFORMS.put(entry.getValue(), Blocks.SHULKER_BOX);
            }
        }
    }

    private static void dyedCandle() {
        for (var entry : ForgeRegistries.BLOCKS.getEntries()) {
            String path = entry.getKey().location().getPath();
            if (path.endsWith("_candle") && !path.equals("candle")) {
                DYED_TRANSFORMS.put(entry.getValue(), Blocks.CANDLE);
            }
        }
    }

    private static void dyedBanner() {
        // banners are items primarily; skip block banners
    }

    private static void registerCopper() {
        copper(Blocks.EXPOSED_COPPER, Blocks.COPPER_BLOCK);
        copper(Blocks.WEATHERED_COPPER, Blocks.COPPER_BLOCK);
        copper(Blocks.OXIDIZED_COPPER, Blocks.COPPER_BLOCK);
        copper(Blocks.EXPOSED_CUT_COPPER, Blocks.CUT_COPPER);
        copper(Blocks.WEATHERED_CUT_COPPER, Blocks.CUT_COPPER);
        copper(Blocks.OXIDIZED_CUT_COPPER, Blocks.CUT_COPPER);
        copper(Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.CUT_COPPER_SLAB);
        copper(Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.CUT_COPPER_SLAB);
        copper(Blocks.OXIDIZED_CUT_COPPER_SLAB, Blocks.CUT_COPPER_SLAB);
        copper(Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.CUT_COPPER_STAIRS);
        copper(Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.CUT_COPPER_STAIRS);
        copper(Blocks.OXIDIZED_CUT_COPPER_STAIRS, Blocks.CUT_COPPER_STAIRS);
        copper(Blocks.WAXED_EXPOSED_COPPER, Blocks.WAXED_COPPER_BLOCK);
        copper(Blocks.WAXED_WEATHERED_COPPER, Blocks.WAXED_COPPER_BLOCK);
        copper(Blocks.WAXED_OXIDIZED_COPPER, Blocks.WAXED_COPPER_BLOCK);
        copper(Blocks.WAXED_EXPOSED_CUT_COPPER, Blocks.WAXED_CUT_COPPER);
        copper(Blocks.WAXED_WEATHERED_CUT_COPPER, Blocks.WAXED_CUT_COPPER);
        copper(Blocks.WAXED_OXIDIZED_CUT_COPPER, Blocks.WAXED_CUT_COPPER);
        copper(Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_CUT_COPPER_SLAB);
        copper(Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_CUT_COPPER_SLAB);
        copper(Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB, Blocks.WAXED_CUT_COPPER_SLAB);
        copper(Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_CUT_COPPER_STAIRS);
        copper(Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_CUT_COPPER_STAIRS);
        copper(Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS, Blocks.WAXED_CUT_COPPER_STAIRS);
    }

    private static void copper(Block from, Block to) {
        COPPER_TRANSFORMS.put(from, to);
    }
}
