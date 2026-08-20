# GeometricWeather 重构路线图 (ROADMAP)

> 目标：在保持功能不变的前提下，将项目逐步迁移至最新的 Android 官方推荐技术栈。
> 分支：`refactor-modern-stack`
> 基线：`adapt-android-15`

## 现状总览

| 维度 | 当前技术 | 目标技术 |
|------|----------|----------|
| 构建脚本 | Gradle Groovy DSL | Gradle Kotlin DSL |
| 依赖管理 | build.gradle 硬编码版本 | Version Catalog (`libs.versions.toml`) |
| 注解处理 | kapt | KSP |
| 数据库 | Room (KSP) | Room (KSP) |
| 异步 | Kotlin Coroutines + Flow (StateFlow) | Kotlin Coroutines + Flow (StateFlow) |
| 序列化 | kotlinx.serialization | kotlinx.serialization |
| 图片加载 | Glide | Coil |
| UI | XML View 系统 (约 397 个布局) | Jetpack Compose + Material 3 |
| 语言 | Java (345 文件) + Kotlin (97 文件) | 全 Kotlin |
| 模块化 | 单模块 app | 多模块 (core/data/domain/presentation/feature) |
| 测试 | JUnit4 + PowerMock + Robolectric | JUnit5 + Compose UI Test |

## 阶段规划

### 阶段 1：构建系统现代化
- [x] Groovy → Kotlin DSL (`build.gradle` → `build.gradle.kts`)
- [x] 启用 Version Catalog (`gradle/libs.versions.toml`)
- [x] `kapt` → `KSP` (Hilt、Glide；Room 随阶段 2 引入)
- [ ] 升级 Gradle Wrapper / AGP / Kotlin / Compose Compiler 至最新稳定版（当前仍对齐基线：Gradle 8.7 / AGP 8.4.1 / Kotlin 1.9.24，Kotlin 2.x 需单独处理 Compose Compiler 插件）
- [x] 启用 kotlinx.serialization 插件
- [x] 验证三种 flavor (pub / gplay / fdroid) 均可构建

### 阶段 2：数据库迁移 GreenDAO → Room
- [x] 引入 Room + KSP 依赖
- [x] 定义 Room Entity（对齐现有 GreenDAO schema）
- [x] 编写 DAO 与 Database
- [x] 实现用户数据迁移方案
- [x] 替换 `db/controllers` 调用，移除 GreenDAO 依赖

用户库文件名仍为 `Geometric_Weather_db`。GreenDAO `schemaVersion` 62 对应 SQLite `user_version`；Room 版本为 63。升级时按列交集拷贝 `LOCATION_ENTITY` / `WEATHER_ENTITY` 等表（兼容 GreenDAO 主键 `ID` 或 `_id`），不删除地点与天气缓存。新安装直接由 Room 建表。

### 阶段 3：异步层迁移 RxJava2 → Coroutines + Flow
- [x] 添加 kotlinx-coroutines 依赖，移除 RxJava2
- [x] Repository 层：Observable/Flowable → suspend fun / Flow
- [x] ViewModel 层：LiveData → StateFlow / SharedFlow
- [x] Retrofit 适配器替换为协程支持
- [x] WorkManager Worker 协程化
- [x] 清理 `common/rxjava` 及 RxLifecycle 相关代码

EventBus / `BusLiveData` / `EqualtableLiveData` remain for UI event wiring that is not part of the weather/location request stack.

### 阶段 4：序列化迁移 Gson → kotlinx.serialization
- [x] 启用 kotlinx.serialization 插件
- [x] 迁移 `weather/json/` 下全部 JSON 模型
- [x] 替换 converter-gson 为 kotlinx-serialization converter
- [x] 移除 Gson 依赖及 `common/retrofit` 中相关封装

Date fields use `GsonCompatibleDateSerializer` matching Gson `setDateFormat("yyyy-MM-dd'T'HH:mm:ss")` (trailing offsets such as `+08:00` are ignored). Nested Accu types whose JSON property names matched their Java class names were renamed `*Bean` because Kotlin cannot declare both in the same class.

### 阶段 5：图片加载迁移 Glide → Coil
- [ ] 添加 Coil 依赖
- [ ] Compose 侧 Image 组件 → Coil AsyncImage
- [ ] View 侧 (Widget / RemoteViews) 视情况迁移或保留 Glide
- [ ] 移除 Glide 依赖

### 阶段 6：UI 全面 Compose 化
- [ ] 主界面 (MainActivity + Home/Management Fragment) → Compose
- [ ] 搜索页面 → Compose
- [ ] 每日/每小时趋势、设置、关于等页面 → Compose
- [ ] 统一 Navigation Compose 路由
- [ ] 清理 XML 布局及 View 系控件（保留 Widget/RemoteViews）
- [ ] 迁移 View 系自定义控件 (trend chart 等) 至 Compose Canvas

### 阶段 7：Java → Kotlin 全量迁移
- [ ] 迁移剩余 Java 文件（编译器辅助 + 人工清理）
- [ ] 消除重复样板代码，采用 Kotlin 惯用法
- [ ] 统一代码风格（ktlint）

### 阶段 8：模块化拆分
- [ ] 拆分 `:core`（基础组件/主题/工具）
- [ ] 拆分 `:data`（网络/数据库/API 服务）
- [ ] 拆分 `:domain`（模型/用例）
- [ ] 拆分 `:feature`（按功能模块，如 main/search/settings）
- [ ] 配置构建缓存与依赖隔离

## 横切工作

- **测试保障**：每一阶段完成时保证现有测试通过，且补充必要的单元/集成测试
- **提交粒度**：每个阶段拆分为若干原子提交，便于回滚与 review
- **构建验证**：每个阶段结束执行一次完整 `./gradlew assembleFdroidDebug assembleGplayDebug` 验证

## 非目标 (Out of Scope)

- 不改变 App 的产品功能与 UI 视觉
- 不迁移 AppWidget / RemoteViews（保留 View 系统实现）
- 不迁移 Live Wallpaper / 第三方 SDK 接入逻辑