# YTLS（ytls）模组架构说明

本文描述 Minecraft Forge **1.20.4** 工程 `icu.ytlsnb.ytls` 的包结构与扩展方式。

> **详细教程请参阅：**
> - [框架快速上手指南.md](doc/框架快速上手指南.md)
> - [第一优先级模块说明.md](doc/第一优先级模块说明.md)

## 设计目标

- **入口极薄**：`YtlsMod` 仅调用 `Framework.initialize()`，不写业务逻辑。
- **框架与业务分离**：`framework/` 封装 Forge 底层；`gameplay/` 承载创意玩法。
- **注解驱动**：注册、事件、网络、配置通过扫描 + 注解自动接入。
- **版本抽象**：业务层依赖框架接口，Forge 实现可随版本替换。

## 包结构一览

| 包路径 | 职责 |
|--------|------|
| `icu.ytlsnb.ytls` | 模组入口 `YtlsMod`、`ModConstants` |
| `framework.bootstrap` | 框架启动、生命周期 |
| `framework.registry` | 统一注册体系 |
| `framework.event` | 语义化事件 |
| `framework.network` / `framework.sync` | 网络与同步 |
| `framework.config` | 配置管理 |
| `framework.data` | 数据生成 |
| `framework.debug` | 调试命令与开关 |
| `gameplay.*` | 创意 Mod 业务代码（block/item/network/config/common） |

## 扩展示例

### 新增物品

1. 在 `gameplay/item/` 下创建类并标注 `@RegisterItem("name")`
2. 提供 public 无参构造函数
3. 可选：运行 `gradlew runData` 生成 lang 与 model

### 新增方块

1. 在 `gameplay/block/` 下创建类并标注 `@RegisterBlock("name")`
2. 放置贴图到 `assets/ytls/textures/block/<name>.png`
3. 运行 `gradlew runData`

### 新增网络消息

1. 在 `gameplay/network/` 下创建类 extends `NetworkPayload`
2. 标注 `@NetworkMessage(id = "...", direction = ...)`
3. 通过 `NetworkAccess.channel()` 发送

---

维护本文档时，若移动包或新增框架入口，请同步更新上表并修订 `doc/` 下教程。
