package icu.ytlsnb.ytls.framework.sync;

import icu.ytlsnb.ytls.framework.network.NetworkChannel;
import icu.ytlsnb.ytls.framework.sync.annotation.SyncField;
import icu.ytlsnb.ytls.framework.sync.message.EntitySyncMessage;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 实体字段级同步辅助。服务端修改带 {@link SyncField} 的字段后调用 {@link #markDirty} 推送。
 */
public final class SyncHelper {
    private static final Logger LOG = FrameworkLog.of("sync");

    private static NetworkChannel networkChannel;

    private SyncHelper() {
    }

    public static void bind(NetworkChannel channel) {
        networkChannel = channel;
    }

    public static void markDirty(Entity entity, Object dataHolder) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        EntitySyncMessage message = new EntitySyncMessage();
        message.entityId = entity.getId();
        message.fieldValues = collectFieldValues(dataHolder);

        for (ServerPlayer player : serverLevel.players()) {
            if (player.connection != null) {
                networkChannel.sendToPlayer(message, player);
            }
        }
        LOG.debug("Synced entity {} fields to {} players", entity.getId(), serverLevel.players().size());
    }

    public static void apply(Entity entity, Object dataHolder, Map<String, String> fieldValues) {
        for (Field field : dataHolder.getClass().getDeclaredFields()) {
            if (field.getAnnotation(SyncField.class) == null) {
                continue;
            }
            String encoded = fieldValues.get(field.getName());
            if (encoded == null) {
                continue;
            }
            field.setAccessible(true);
            try {
                setFieldFromString(field, dataHolder, encoded);
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException("Failed to apply sync field: " + field.getName(), ex);
            }
        }
    }

    static Map<String, String> collectFieldValues(Object dataHolder) {
        Map<String, String> values = new HashMap<>();
        for (Field field : dataHolder.getClass().getDeclaredFields()) {
            SyncField syncField = field.getAnnotation(SyncField.class);
            if (syncField == null) {
                continue;
            }
            field.setAccessible(true);
            try {
                values.put(field.getName(), stringify(field.get(dataHolder)));
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException("Failed to read sync field: " + field.getName(), ex);
            }
        }
        return values;
    }

    private static String stringify(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private static void setFieldFromString(Field field, Object target, String encoded) throws IllegalAccessException {
        Class<?> type = field.getType();
        if (type == String.class) {
            field.set(target, encoded);
        } else if (type == int.class || type == Integer.class) {
            field.setInt(target, Integer.parseInt(encoded));
        } else if (type == long.class || type == Long.class) {
            field.setLong(target, Long.parseLong(encoded));
        } else if (type == boolean.class || type == Boolean.class) {
            field.setBoolean(target, Boolean.parseBoolean(encoded));
        } else if (type == float.class || type == Float.class) {
            field.setFloat(target, Float.parseFloat(encoded));
        } else if (type == double.class || type == Double.class) {
            field.setDouble(target, Double.parseDouble(encoded));
        } else {
            throw new IllegalStateException("Unsupported sync field type: " + type.getName());
        }
    }
}
