package icu.ytlsnb.ytls.gameplay.registry;

import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import icu.ytlsnb.ytls.framework.registry.api.RegistryContributor;
import icu.ytlsnb.ytls.framework.registry.api.RegistryFacade;
import icu.ytlsnb.ytls.framework.registry.api.RegistryKind;
import icu.ytlsnb.ytls.gameplay.blockentity.ExampleBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 演示 {@link RegistryContributor}：方块实体等依赖已注册方块的类型建议在此注册，
 * 或在 {@link icu.ytlsnb.ytls.framework.registry.annotation.RegisterBlockEntity} 的 Supplier 内延迟引用 {@link RegistryAccess}。
 */
public final class ExampleGameplayRegistryContributor implements RegistryContributor {
    @Override
    public void contribute(RegistryFacade registry) {
        registry.register(RegistryKind.BLOCK_ENTITY, "example_be", () -> {
            AtomicReference<BlockEntityType<ExampleBlockEntity>> typeRef = new AtomicReference<>();
            BlockEntityType<ExampleBlockEntity> type = BlockEntityType.Builder.of(
                    (pos, state) -> new ExampleBlockEntity(typeRef.get(), pos, state),
                    RegistryAccess.block("example_block")
            ).build(null);
            typeRef.set(type);
            return type;
        });
    }
}
