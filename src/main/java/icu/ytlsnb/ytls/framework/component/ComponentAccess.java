package icu.ytlsnb.ytls.framework.component;

import icu.ytlsnb.ytls.ModConstants;
import icu.ytlsnb.ytls.framework.component.api.ComponentHolder;
import icu.ytlsnb.ytls.framework.component.api.ComponentType;
import icu.ytlsnb.ytls.framework.component.api.GameComponent;
import icu.ytlsnb.ytls.framework.util.ModResources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Optional;

/**
 * 业务层访问组件的统一入口。
 */
public final class ComponentAccess {
    private ComponentAccess() {
    }

    public static Optional<ComponentHolder> of(Entity entity) {
        return entity.getCapability(ComponentCapabilities.HOLDER).resolve();
    }

    public static Optional<ComponentHolder> of(Player player) {
        return player.getCapability(ComponentCapabilities.HOLDER).resolve();
    }

    public static Optional<ComponentHolder> of(BlockEntity blockEntity) {
        return blockEntity.getCapability(ComponentCapabilities.HOLDER).resolve();
    }

    public static Optional<ComponentHolder> of(Level level) {
        return level.getCapability(ComponentCapabilities.HOLDER).resolve();
    }

    public static <T extends GameComponent> T getOrCreate(Entity entity, ComponentType<T> type) {
        return of(entity).orElseThrow(() -> new IllegalStateException("Entity has no component holder: " + entity))
                .getOrCreate(type);
    }

    public static <T extends GameComponent> T getOrCreate(Player player, ComponentType<T> type) {
        return of(player).orElseThrow(() -> new IllegalStateException("Player has no component holder"))
                .getOrCreate(type);
    }

    public static <T extends GameComponent> T getOrCreate(BlockEntity blockEntity, ComponentType<T> type) {
        return of(blockEntity).orElseThrow(() -> new IllegalStateException("BlockEntity has no component holder"))
                .getOrCreate(type);
    }

    public static <T extends GameComponent> T getOrCreate(Level level, ComponentType<T> type) {
        return of(level).orElseThrow(() -> new IllegalStateException("Level has no component holder"))
                .getOrCreate(type);
    }

    public static <T extends GameComponent> Optional<T> get(Entity entity, ComponentType<T> type) {
        return of(entity).flatMap(holder -> holder.get(type));
    }

    public static <T extends GameComponent> Optional<T> get(Player player, ComponentType<T> type) {
        return of(player).flatMap(holder -> holder.get(type));
    }

    public static ResourceLocation capabilityId() {
        return ModResources.loc("components");
    }
}
