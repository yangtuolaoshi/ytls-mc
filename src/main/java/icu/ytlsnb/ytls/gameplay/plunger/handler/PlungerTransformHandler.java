package icu.ytlsnb.ytls.gameplay.plunger.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.Vec3;
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
        registerLogsAndStems();
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
        if (block instanceof BedBlock) {
            return handleBed(level, pos, state);
        }
        if (block instanceof BannerBlock || block instanceof WallBannerBlock) {
            return handleBanner(level, pos, state);
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
                PlungerBlockHandler.spawnFortuneCopies(serverLevel, pos, new ItemStack(drop), fortune);
            }
            if (INFESTED_TRANSFORMS.containsKey(block)) {
                spawnSilverfish(serverLevel, pos, 1 + fortune);
            }
            // 尽量保留朝向等属性
            BlockState newState = copyCompatibleProperties(state, target.defaultBlockState());
            level.setBlock(pos, newState, Block.UPDATE_ALL);
        }
        return true;
    }

    private static boolean handleBed(Level level, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof BedBlock) || state.getBlock() == Blocks.WHITE_BED) {
            return false;
        }
        if (!(level instanceof ServerLevel)) {
            return true;
        }
        Direction facing = state.getValue(BedBlock.FACING);
        BedPart part = state.getValue(BedBlock.PART);
        boolean occupied = state.getValue(BedBlock.OCCUPIED);
        BlockPos otherPos = pos.relative(part == BedPart.FOOT ? facing : facing.getOpposite());
        BlockState otherState = level.getBlockState(otherPos);

        BlockState newHere = Blocks.WHITE_BED.defaultBlockState()
                .setValue(BedBlock.FACING, facing)
                .setValue(BedBlock.PART, part)
                .setValue(BedBlock.OCCUPIED, occupied);

        if (otherState.getBlock() instanceof BedBlock) {
            BedPart otherPart = part == BedPart.FOOT ? BedPart.HEAD : BedPart.FOOT;
            boolean otherOccupied = otherState.hasProperty(BedBlock.OCCUPIED)
                    ? otherState.getValue(BedBlock.OCCUPIED)
                    : occupied;
            BlockState newOther = Blocks.WHITE_BED.defaultBlockState()
                    .setValue(BedBlock.FACING, facing)
                    .setValue(BedBlock.PART, otherPart)
                    .setValue(BedBlock.OCCUPIED, otherOccupied);
            // 先改另一半，避免原版床方块校验拆掉半截
            level.setBlock(otherPos, newOther, 2);
        }
        level.setBlock(pos, newHere, 3);
        return true;
    }

    private static boolean handleBanner(Level level, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        if (block == Blocks.WHITE_BANNER || block == Blocks.WHITE_WALL_BANNER) {
            return false;
        }
        if (!(level instanceof ServerLevel)) {
            return true;
        }
        BlockState replacement;
        if (block instanceof WallBannerBlock) {
            replacement = Blocks.WHITE_WALL_BANNER.defaultBlockState()
                    .setValue(WallBannerBlock.FACING, state.getValue(WallBannerBlock.FACING));
        } else if (block instanceof BannerBlock) {
            replacement = Blocks.WHITE_BANNER.defaultBlockState()
                    .setValue(BannerBlock.ROTATION, state.getValue(BannerBlock.ROTATION));
        } else {
            return false;
        }
        level.setBlock(pos, replacement, Block.UPDATE_ALL);
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
            BlockPos spawnPos = PlungerSpawnHelper.findNearbySpace(serverLevel, pos);
            type.spawn(serverLevel, spawnPos, MobSpawnType.SPAWNER);
        }
        return true;
    }

    private static void spawnSilverfish(ServerLevel level, BlockPos pos, int count) {
        for (int i = 0; i < count; i++) {
            Silverfish fish = EntityType.SILVERFISH.create(level);
            if (fish == null) {
                continue;
            }
            BlockPos spawnPos = PlungerSpawnHelper.findNearbySpace(level, pos);
            Vec3 center = PlungerSpawnHelper.centerOf(spawnPos);
            fish.moveTo(center.x, center.y, center.z, level.random.nextFloat() * 360.0F, 0.0F);
            level.addFreshEntity(fish);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static BlockState copyCompatibleProperties(BlockState from, BlockState to) {
        BlockState result = to;
        for (net.minecraft.world.level.block.state.properties.Property property : from.getProperties()) {
            if (result.hasProperty(property)) {
                result = result.setValue(property, from.getValue(property));
            }
        }
        return result;
    }

    private static void registerLogsAndStems() {
        for (var entry : ForgeRegistries.BLOCKS.getEntries()) {
            Block block = entry.getValue();
            ResourceLocation id = entry.getKey().location();
            String name = id.getPath();
            if (name.startsWith("stripped_")) {
                continue;
            }
            if (name.endsWith("_log") || name.endsWith("_stem") || name.endsWith("_hyphae")) {
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
        for (var entry : ForgeRegistries.BLOCKS.getEntries()) {
            Block block = entry.getValue();
            String path = entry.getKey().location().getPath();
            if (path.endsWith("_wool") && !path.equals("white_wool")) {
                DYED_TRANSFORMS.put(block, Blocks.WHITE_WOOL);
            } else if (path.endsWith("_terracotta") && !path.equals("terracotta") && !path.equals("white_terracotta")) {
                DYED_TRANSFORMS.put(block, Blocks.WHITE_TERRACOTTA);
            } else if (path.endsWith("_stained_glass")) {
                DYED_TRANSFORMS.put(block, Blocks.GLASS);
            } else if (path.endsWith("_stained_glass_pane")) {
                DYED_TRANSFORMS.put(block, Blocks.GLASS_PANE);
            } else if (path.endsWith("_concrete") && !path.endsWith("_powder") && !path.equals("white_concrete")) {
                DYED_TRANSFORMS.put(block, Blocks.WHITE_CONCRETE);
            } else if (path.endsWith("_concrete_powder") && !path.equals("white_concrete_powder")) {
                DYED_TRANSFORMS.put(block, Blocks.WHITE_CONCRETE_POWDER);
            } else if (path.endsWith("_carpet") && !path.equals("white_carpet")) {
                DYED_TRANSFORMS.put(block, Blocks.WHITE_CARPET);
            } else if (path.endsWith("shulker_box") && !path.equals("shulker_box")) {
                DYED_TRANSFORMS.put(block, Blocks.SHULKER_BOX);
            } else if (path.endsWith("_candle") && !path.equals("candle")) {
                DYED_TRANSFORMS.put(block, Blocks.CANDLE);
            }
            // 床与旗帜单独处理，避免半截床 / 丢失朝向
        }
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
