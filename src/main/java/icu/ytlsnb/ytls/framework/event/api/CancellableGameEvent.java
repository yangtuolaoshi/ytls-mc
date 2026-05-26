package icu.ytlsnb.ytls.framework.event.api;

/**
 * 可取消的语义事件。桥接层会在 {@link #isCancelled()} 为 true 时回写 Forge 原事件的取消状态。
 */
public abstract class CancellableGameEvent extends GameEvent {
}
