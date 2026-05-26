package icu.ytlsnb.ytls.framework.sync.message;

import icu.ytlsnb.ytls.framework.component.ComponentAccess;
import icu.ytlsnb.ytls.framework.component.ComponentRegistry;
import icu.ytlsnb.ytls.framework.component.api.ComponentType;
import icu.ytlsnb.ytls.framework.network.annotation.NetworkMessage;
import icu.ytlsnb.ytls.framework.network.api.NetworkContext;
import icu.ytlsnb.ytls.framework.network.api.NetworkDirection;
import icu.ytlsnb.ytls.framework.network.api.NetworkPayload;
import icu.ytlsnb.ytls.framework.sync.SyncHelper;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;

@NetworkMessage(id = "entity_sync", direction = NetworkDirection.TO_CLIENT)
public final class EntitySyncMessage extends NetworkPayload {
    public int entityId;
    /** 组件类型 ID，如 {@code player_stats} 或 {@code ytls:skill_state} */
    public String componentTypeId = "";
    public Map<String, String> fieldValues = new HashMap<>();

    @Override
    public void handleClient(NetworkContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) {
                return;
            }
            Entity entity = mc.level.getEntity(entityId);
            if (entity == null) {
                return;
            }
            ComponentType<?> type = ComponentRegistry.findById(componentTypeId);
            if (type == null) {
                FrameworkLog.of("sync").warn("Unknown component type in sync message: {}", componentTypeId);
                return;
            }
            ComponentAccess.get(entity, type).ifPresent(component ->
                    SyncHelper.apply(entity, component, fieldValues));
        });
    }
}
