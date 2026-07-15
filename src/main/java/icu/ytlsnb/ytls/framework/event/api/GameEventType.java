package icu.ytlsnb.ytls.framework.event.api;

/**
 * 语义化事件类型枚举。扩展时在 {@link icu.ytlsnb.ytls.framework.event.bridge.ForgeEventBridge} 中增加桥接。
 */
public enum GameEventType {
    COMMON_SETUP,
    CLIENT_SETUP,
    SERVER_STARTING,
    SERVER_STARTED,
    SERVER_STOPPING,
    PLAYER_LOGIN,
    PLAYER_LOGOUT,
    PLAYER_TICK,
    PLAYER_HURT,
    PLAYER_DEATH,
    LEVEL_LOAD,
    LEVEL_UNLOAD,
    BLOCK_BREAK,
    BLOCK_PLACE,
    ENTITY_SPAWN,
    ENTITY_DEATH,
    CHUNK_LOAD,
    DIMENSION_CHANGE,
    ITEM_USE,
    RIGHT_CLICK_BLOCK,
    ENTITY_INTERACT,
    CONFIG_RELOAD,
    RESOURCE_RELOAD
}
