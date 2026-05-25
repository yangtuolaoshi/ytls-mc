package icu.ytlsnb.ytls.framework.sync.message;

import icu.ytlsnb.ytls.framework.network.annotation.NetworkMessage;
import icu.ytlsnb.ytls.framework.network.api.NetworkContext;
import icu.ytlsnb.ytls.framework.network.api.NetworkDirection;
import icu.ytlsnb.ytls.framework.network.api.NetworkPayload;
import icu.ytlsnb.ytls.framework.component.ComponentAccess;
import icu.ytlsnb.ytls.framework.skill.SkillCaster;
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
            ComponentAccess.get(entity, SkillCaster.stateType())
                    .ifPresent(state -> SyncHelper.apply(entity, state, fieldValues));
        });
    }
}
