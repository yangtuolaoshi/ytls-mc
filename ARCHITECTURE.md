# YTLS（ytls）模组架构说明

本文提供仓库级架构速览。完整设计、接入方式与测试说明请参阅：

**[doc/YTLS框架介绍与使用手册.md](doc/YTLS框架介绍与使用手册.md)**

需求与验收条款（独立维护，不在手册内重复）：

- [doc/Minecraft Forge开发框架需求文档.md](doc/Minecraft%20Forge开发框架需求文档.md)
- [doc/框架验收要求.md](doc/框架验收要求.md)

## 设计目标

- **入口极薄**：`YtlsMod` 仅调用 `Framework.initialize()`。
- **框架与业务分离**：`framework/` 封装 Forge；`gameplay/` 承载创意玩法。
- **注解驱动**：注册、事件、网络、配置等通过扫描自动接入。
- **版本抽象**：业务依赖框架接口，Forge 实现可随版本替换。

## 包结构一览

| 包路径 | 职责 |
|--------|------|
| `icu.ytlsnb.ytls` | `YtlsMod`、`ModConstants` |
| `framework.bootstrap` | 启动、生命周期 |
| `framework.registry` | 统一注册（12 RegistryKind） |
| `framework.event` | 语义化事件 |
| `framework.network` / `framework.sync` | 网络与同步 |
| `framework.config` / `framework.data` / `framework.debug` | 配置、datagen、调试 |
| `framework.component` / `framework.ai` / `framework.skill` / `framework.worldrule` / `framework.render` | 第二优先级能力 |
| `gameplay.*` | 创意 Mod 业务与示例 |

## 启动顺序（摘要）

`Framework` 构造：`ConfigManager` → `RegistryScanner` → `NetworkChannel` → 组件/技能/世界规则扫描 → `LifecycleManager` + 事件桥接 → `CONSTRUCT`。

详见手册 [§4 启动与初始化流程](doc/YTLS框架介绍与使用手册.md#4-启动与初始化流程)。

---

维护：包结构或 `Framework.initialize` 顺序变更时，请同步更新本文件与《YTLS框架介绍与使用手册.md》。
