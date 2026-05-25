package icu.ytlsnb.ytls.framework.component;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.framework.component.api.ComponentHolder;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ComponentCapabilities {
    public static final Capability<ComponentHolder> HOLDER = CapabilityManager.get(new CapabilityToken<>() {
    });

    private ComponentCapabilities() {
    }

    public static final class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final ForgeComponentHolder holder = new ForgeComponentHolder();
        private final LazyOptional<ComponentHolder> optional = LazyOptional.of(() -> holder);

        public ForgeComponentHolder holderImpl() {
            return holder;
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return HOLDER.orEmpty(cap, optional);
        }

        @Override
        public CompoundTag serializeNBT() {
            return holder.save();
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            holder.load(tag);
        }
    }
}
