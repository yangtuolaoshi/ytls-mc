package icu.ytlsnb.ytls.framework.data.provider;

import icu.ytlsnb.ytls.framework.registry.ForgeRegistryProvider;
import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import icu.ytlsnb.ytls.framework.registry.api.RegistryKind;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.function.BiConsumer;

/**
 * 为 catalog 中注册的方块生成默认战利品表（掉落自身）。
 * 实现 {@link net.minecraft.world.level.storage.loot.LootTableSubProvider}，通过 {@link net.minecraft.data.loot.LootTableProvider} 注册。
 */
public final class AutoBlockLootProvider implements LootTableSubProvider {
    private final ForgeRegistryProvider registry;

    public AutoBlockLootProvider(ForgeRegistryProvider registry) {
        this.registry = registry;
    }

    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> writer) {
        for (var entry : registry.catalog()) {
            if (entry.kind() != RegistryKind.BLOCK) {
                continue;
            }
            Block block = RegistryAccess.block(entry.name());
            writer.accept(block.getLootTable(), LootTable.lootTable()
                    .withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(block))));
        }
    }
}
