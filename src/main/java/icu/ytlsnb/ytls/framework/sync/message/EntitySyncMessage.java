package icu.ytlsnb.ytls.framework.sync.message;

import icu.ytlsnb.ytls.framework.network.annotation.NetworkMessage;
import icu.ytlsnb.ytls.framework.network.api.NetworkContext;
import icu.ytlsnb.ytls.framework.network.api.NetworkDirection;
import icu.ytlsnb.ytls.framework.network.api.NetworkPayload;
import icu.ytlsnb.ytls.framework.sync.SyncHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;

@NetworkMessage(id = "entity_sync", direction = NetworkDirection.TO_CLIENT)
public final class EntitySyncMessage extends NetworkPayload {
    public int entityId;
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
            // 业务层可将组件挂在 entity 的 persistent data 或自定义 capability；
            // 此处演示框架通道，实际项目可扩展 SyncTarget 绑定。
            SyncHelper.apply(entity, entity, fieldValues);
        });
    }
}
