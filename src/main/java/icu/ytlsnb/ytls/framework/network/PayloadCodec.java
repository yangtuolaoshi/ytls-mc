package icu.ytlsnb.ytls.framework.network;

import icu.ytlsnb.ytls.framework.network.api.NetworkPayload;
import icu.ytlsnb.ytls.framework.util.FrameworkLog;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于反射的字段级编解码器，业务层只需在消息类上声明 public 字段。
 */
public final class PayloadCodec {
    private static final Logger LOG = FrameworkLog.of("network-codec");
    private static final Map<Class<?>, List<Field>> FIELD_CACHE = new ConcurrentHashMap<>();

    private PayloadCodec() {
    }

    public static void writePayload(NetworkPayload payload, FriendlyByteBuf buf) {
        for (Field field : fieldsOf(payload.getClass())) {
            writeField(buf, field, payload);
        }
    }

    public static void readPayload(NetworkPayload payload, FriendlyByteBuf buf) {
        for (Field field : fieldsOf(payload.getClass())) {
            readField(buf, field, payload);
        }
    }

    private static List<Field> fieldsOf(Class<?> clazz) {
        return FIELD_CACHE.computeIfAbsent(clazz, key -> {
            List<Field> result = new ArrayList<>();
            for (Field field : key.getDeclaredFields()) {
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                if (java.lang.reflect.Modifier.isTransient(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                result.add(field);
            }
            return List.copyOf(result);
        });
    }

    private static void writeField(FriendlyByteBuf buf, Field field, Object target) {
        try {
            Object value = field.get(target);
            Class<?> type = field.getType();
            if (type == String.class) {
                buf.writeUtf(value == null ? "" : (String) value);
            } else if (type == int.class || type == Integer.class) {
                buf.writeInt(value == null ? 0 : (Integer) value);
            } else if (type == long.class || type == Long.class) {
                buf.writeLong(value == null ? 0L : (Long) value);
            } else if (type == boolean.class || type == Boolean.class) {
                buf.writeBoolean(value != null && (Boolean) value);
            } else if (type == float.class || type == Float.class) {
                buf.writeFloat(value == null ? 0F : (Float) value);
            } else if (type == double.class || type == Double.class) {
                buf.writeDouble(value == null ? 0D : (Double) value);
            } else if (type == ResourceLocation.class) {
                buf.writeResourceLocation(value == null ? new ResourceLocation("minecraft", "air") : (ResourceLocation) value);
            } else if (BlockPos.class.isAssignableFrom(type)) {
                buf.writeBlockPos(value == null ? BlockPos.ZERO : (BlockPos) value);
            } else if (Map.class.isAssignableFrom(type)) {
                @SuppressWarnings("unchecked")
                Map<String, String> map = value == null ? Map.of() : (Map<String, String>) value;
                buf.writeUtf(cn.hutool.json.JSONUtil.toJsonStr(map));
            } else {
                throw new IllegalStateException("Unsupported network field type: " + type.getName() + " in " + field.getName());
            }
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException("Failed to encode field: " + field.getName(), ex);
        }
    }

    private static void readField(FriendlyByteBuf buf, Field field, Object target) {
        try {
            Class<?> type = field.getType();
            if (type == String.class) {
                field.set(target, buf.readUtf());
            } else if (type == int.class || type == Integer.class) {
                field.setInt(target, buf.readInt());
            } else if (type == long.class || type == Long.class) {
                field.setLong(target, buf.readLong());
            } else if (type == boolean.class || type == Boolean.class) {
                field.setBoolean(target, buf.readBoolean());
            } else if (type == float.class || type == Float.class) {
                field.setFloat(target, buf.readFloat());
            } else if (type == double.class || type == Double.class) {
                field.setDouble(target, buf.readDouble());
            } else if (type == ResourceLocation.class) {
                field.set(target, buf.readResourceLocation());
            } else if (BlockPos.class.isAssignableFrom(type)) {
                field.set(target, buf.readBlockPos());
            } else if (Map.class.isAssignableFrom(type)) {
                @SuppressWarnings("unchecked")
                Map<String, String> map = cn.hutool.json.JSONUtil.toBean(buf.readUtf(), HashMap.class);
                field.set(target, map);
            } else {
                throw new IllegalStateException("Unsupported network field type: " + type.getName() + " in " + field.getName());
            }
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException("Failed to decode field: " + field.getName(), ex);
        }
    }
}
