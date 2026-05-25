package icu.ytlsnb.ytls.framework.component.api;

import net.minecraft.nbt.CompoundTag;

/**
 * 可挂载游戏组件的统一接口，支持持久化与复制。
 */
public interface GameComponent {
    void save(CompoundTag tag);

    void load(CompoundTag tag);

    void copyFrom(GameComponent other);

    default void onAttach() {
    }

    default void onDetach() {
    }
}
