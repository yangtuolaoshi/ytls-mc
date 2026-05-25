package icu.ytlsnb.ytls.framework.event.api;

/**
 * 事件监听优先级，数值越小越先执行。
 */
public enum GameEventPriority {
    HIGHEST(0),
    HIGH(100),
    NORMAL(500),
    LOW(900),
    LOWEST(1000);

    private final int weight;

    GameEventPriority(int weight) {
        this.weight = weight;
    }

    public int weight() {
        return weight;
    }
}
