package icu.ytlsnb.ytls.framework.event.api;

/**
 * 框架语义化事件基类，携带完整上下文供业务层使用。
 */
public abstract class GameEvent {
    private boolean cancelled;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
