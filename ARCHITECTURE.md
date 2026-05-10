# YTLS（ytls）模组架构说明

本文描述 Minecraft Forge **1.20.4** 工程 `icu.ytlsnb.ytls` 的包结构与扩展方式，便于后续持续开发。

## 设计目标

- **入口极薄**：`@Mod` 类只做模组总线挂载，不写业务逻辑。
- **注册集中**：所有 `DeferredRegister` 放在 `init` 包，并由 `ModBusSetup` 统一订阅到 `modEventBus`。
- **侧向分离**：客户端专用代码放在 `client`；逻辑层事件放在 `common`；对外部 HTTP 等放在 `integration`。
- **命名空间一致**：`ModConstants.MOD_ID` 与 `META-INF/mods.toml` 中的 `modId`、资源路径前缀保持一致；纹理等使用 `ModResources.loc(...)`，避免硬编码 `modid`。

## 包结构一览

| 包路径 | 职责 |
|--------|------|
| `icu.ytlsnb.ytls` | 模组入口 `YtlsMod` |
| `core` | `ModConstants`、`ModResources` 等跨模组基础工具 |
| `init` | `ModBlocks`、`ModItems`、`ModBlockEntities`、`ModEntityTypes`、`ModCreativeTabs`、`ModBusSetup` |
| `block` / `item` / `entity` | 方块、物品、实体等**游戏对象实现**（保持与原版类型相近的分层） |
| `client` | 仅客户端：`EntityRenderer` 注册、客户端聊天与 UI 相关监听等 |
| `common` | 逻辑侧（含单机集成服务端）：属性创建、向原版创造标签注入条目等 MOD 总线事件 |
| `integration` | 与外部系统交互（例如 HTTP AI），便于替换实现或加配置 |

已移除顶层零散的 `*Registry` / `CommonRegistry` / `Constants` / `Main` 命名，改为语义更清晰的 `Mod*` 与 `YtlsMod`。

## 注册与事件：该把代码写在哪？

### DeferredRegister（方块 / 物品 / 实体类型等）

1. 在 `init` 下对应的 `Mod*` 类中增加 `register(...)` 条目。
2. 若新增了新的 `DeferredRegister` 类型，在 `ModBusSetup.subscribeDeferredRegisters` 中增加一行 `.register(modEventBus)`。

### MOD 总线（`Bus.MOD`）

- 与注册数据定义强相关、且**不需要**客户端独占的：放在 `common`（例如 `EntityAttributeCreationEvent`、`BuildCreativeModeTabContentsEvent`）。
- 客户端资源（渲染器、模型层等）：放在 `client`，并使用 `@Mod.EventBusSubscriber(..., value = Dist.CLIENT, bus = Bus.MOD)`。

### FORGE 总线（`Bus.FORGE`）

- 运行期游戏事件（客户端聊天、tick、实体交互等）：按侧拆分；当前示例中客户端聊天处理在 `client.ClientChatHandler`。

## 扩展示例

### 新增一组物品

1. 在 `item` 下新增物品类。
2. 在 `ModItems` 中 `ITEMS.register("registry_name", ...)`。
3. 若需要出现在自定义创造标签 `ModCreativeTabs.TEST_TAB` 中，编辑 `displayItems`；若需要进原版标签，在 `CreativeTabVanillaInjection` 中追加。

### 新增实体

1. 在 `entity` 下实现实体类。
2. 在 `ModEntityTypes` 中注册 `EntityType`。
3. 若有属性表，在 `ModEntityAttributes` 的 `EntityAttributeCreationEvent` 中 `event.put(...)`。
4. 若有渲染，在 `client.ClientEntityRenderers` 中注册渲染器；纹理路径使用 `ModResources.loc("textures/entity/....png")`，并把 png 放到 `assets/ytls/textures/entity/`。

### 外部服务（HTTP 等）

- 将调用细节封装在 `integration`（当前为 `integration.ai.AiChatClient`），由 `client` 或 `common` 中的事件处理器调用，避免在事件类里堆叠 URL 与 JSON 细节。

## 与 Gradle / `mods.toml` 的对应关系

- 运行时模组 ID 以 **`ModConstants.MOD_ID`** 与 **`mods.toml` 的 `modId`** 为准（当前为 `ytls`）。
- `gradle.properties` 中的 `mod_id` 仍可能与模板默认不一致；发布或生成资源时注意两处统一，避免混淆。

## 可选后续演进

当内容量增大时，可在 **不改变上述分层原则** 的前提下：

- 按主题拆子包（例如 `content/obsidian`），仍通过 `init.Mod*` 汇总注册；
- 引入 `network` 包存放自定义 payload；
- 将 `AiChatClient` 的 URL 与超时迁入配置文件或 `ModConstants` 旁的专用配置类。

---

维护本文档时：若移动包或新增总线订阅入口，请同步更新「包结构一览」与「注册与事件」两节。
