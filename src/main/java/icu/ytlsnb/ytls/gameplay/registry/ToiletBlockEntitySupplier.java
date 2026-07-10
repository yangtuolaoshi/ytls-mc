package icu.ytlsnb.ytls.gameplay.registry;

import icu.ytlsnb.ytls.framework.registry.RegistryAccess;
import icu.ytlsnb.ytls.framework.registry.annotation.RegisterBlockEntity;
import icu.ytlsnb.ytls.framework.registry.api.RegistrySupplier;
import icu.ytlsnb.ytls.gameplay.toilet.blockentity.ToiletBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.concurrent.atomic.AtomicReference;

@RegisterBlockEntity("toilet_be")
public final class ToiletBlockEntitySupplier implements RegistrySupplier<BlockEntityType<?>> {
    @Override
    public BlockEntityType<?> get() {
        AtomicReference<BlockEntityType<ToiletBlockEntity>> typeRef = new AtomicReference<>();
        BlockEntityType<ToiletBlockEntity> type = BlockEntityType.Builder.of(
                (pos, state) -> new ToiletBlockEntity(typeRef.get(), pos, state),
                RegistryAccess.block("toilet")
        ).build(null);
        typeRef.set(type);
        return type;
    }
}
