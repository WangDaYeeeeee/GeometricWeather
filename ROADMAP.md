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
| UI | Jetpack Compose（in-app）+ Widget/RemoteViews XML | Jetpack Compose + Material 3（Widget 除外） |
| 语言 | 第一方 Java 0（仅 vendored `com.xw.repo.BubbleSeekBar*`）+ Kotlin | 全 Kotlin |
| 模块化 | `:app` + `:core` + `:domain` + `:data` + `:presentation` + `:feature:search` + `:feature:settings`（main 仍在 app） | 多模块 (core/data/domain/presentation/feature) |
| 测试 | JUnit5 Jupiter + mockk（Robolectric 仍在 catalog，当前无 JUnit5 用例） | JUnit5 + Compose UI Test |

## 阶段规划

### 阶段 1：构建系统现代化
- [x] Groovy → Kotlin DSL (`build.gradle` → `build.gradle.kts`)
- [x] 启用 Version Catalog (`gradle/libs.versions.toml`)
- [x] `kapt` → `KSP` (Hilt；Room 随阶段 2 引入；Glide 已在阶段 5 移除)
- [x] 升级 Gradle Wrapper / AGP / Kotlin / Compose Compiler 至最新稳定版（Gradle 8.13 / AGP 8.13.2 / Kotlin 2.3.21 / KSP 2.3.11；Compose Compiler Gradle 插件 `org.jetbrains.kotlin.plugin.compose` 随 Kotlin 版本；Hilt **2.58**（最后支持 AGP 8 且可读 Kotlin 2.3 metadata；2.59+ 要求 AGP 9）；Compose BOM 仍为 2024.06.00 以避免 UI 视觉变化。未升 AGP 9.x：Gradle 9.x、Build Tools 36、内置 Kotlin / new DSL 超出本阶段范围）
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
- [x] 添加 Coil 依赖
- [x] Compose 侧 Image 组件 → Coil AsyncImage
- [x] View 侧 (Widget / RemoteViews) 视情况迁移或保留 Glide
- [x] 移除 Glide 依赖

In-app ImageViews (`ImageHelper`, icon-provider store/GitHub/Chronus icons, WeChat donate) load local `@DrawableRes` via Coil `ImageRequest`. Compose screens still use `painterResource` for local assets (`AboutActivity`, `AllergenActivity`); `coil-compose` is on the classpath for future URL/`AsyncImage` use. AppWidget / RemoteViews / wallpaper already use `setImageViewResource` and resource-provider bitmaps, so Glide was not required there and is fully removed. Unused `R.string.glide` / `about_glide` credits remain in translations and are not referenced.

### 阶段 6：UI 全面 Compose 化
- [x] 主界面壳层：MainActivity `setContent` + 城市管理列表 Compose（宽屏分栏 / 窄屏覆盖层）
- [x] 主界面天气首页：Compose `HomeScreen` 通过 `AndroidView` 托管 `fragment_home` + Material weather `WeatherView`、`MainAdapter` / `MainLayoutManager`、`SwipeSwitchLayout`（非 Fragment）
- [x] 搜索页面 → Compose（`SearchActivity` `setContent` + `search/compose/`；结果列表、IME 搜索、天气源筛选底栏；仍返回 `RESULT_OK` + `KEY_LOCATION`）
- [x] 每日详情 → Compose（`DailyWeatherActivity` `setContent` + `daily/compose/` HorizontalPager）
- [x] 设置 leftover → Compose（卡片/日趋势/小时趋势排序、`PreviewIconActivity`；设置/关于/数据源已有 Compose）
- [x] Navigation Compose：各 in-app Activity 内 `NavHost`（`InAppRoute`）；设置栈内 appearance leftover 为嵌套路由。跨功能仍走 Intent（保留 MainActivity `ACTION_*` 与搜索 `KEY_LOCATION`）
- [x] 清理已无 inflate 的 in-app XML / 适配器 / `HomeFragment` / `ManagementFragment` / `search/ui`
- [x] 日详情页已离开 XML；Home 内日/小时趋势图仍为自定义 View（`item_trend_*` + trend adapters），未改写 Compose Canvas

**Honest leftovers (in-app, not widgets):** Home 天气动画必须保持真实 `WeatherView`（`AndroidView`）。Home 卡片/header/趋势 RecyclerView 仍走 `MainAdapter`（与 `WeatherView` scroll、SwipeSwitch、动画高度强耦合）— **未转 Compose**，避免打断 WeatherView 滚动联动。日/小时趋势图为 View canvas（`Polyline`/`Histogram` 等），**不是** Compose Canvas（视觉对齐风险大，本轮跳过）。日详情 overview 天气图标仍为 `AnimatableIconView`（`AndroidView`）。Alert / Allergen / About / Search / Settings / Daily 主路径已是 Compose。

**Out of scope (unchanged):** AppWidget / RemoteViews / `remoteviews/config/*` / live wallpaper 布局与代码。

### 阶段 7：Java → Kotlin 全量迁移
- [x] 迁移 `common/basic/models/weather` 领域模型（保留 Java getter 名如 `getPM25()` / `getUV()` / `isDaylight()`）
- [x] 迁移 Room `db/` converters、DAO、entities、controllers、`DatabaseHelper`、`db/generators`
- [x] 迁移 Hilt modules（`UtilsModule` / `ApiModule`）、`WeatherServiceSet`、`SearchActivityRepository`、widget `AppWidgetProvider`、fdroid/gplay 空壳 location/Bugly stubs、部分 interceptor/receiver
- [x] 迁移 `WeatherService`、`CommonConverter`、`LocationHelper`、location `ApiModule` / `LocationException`、TLS/Network/Object/Log helpers、pub 风味 Baidu/AMap `LocationService`、daily adapter ViewModel 数据类
- [x] 迁移 weather result converters（Accu/Caiyun/Mf/Owm）、`DailyWeatherAdapter`、`IntentHelper`/`DisplayUtils`/`LanguageUtils` 及 sibling helpers、polling `PollingManager`/`WorkerHelper`、settings dialogs
- [x] 迁移 background polling leftovers（Hilt workers、`UpdateService`/`ForegroundUpdateService` 族、permanent observer、QS `TileService`）
- [x] 迁移 main View 层 adapters/holders/dialogs/layouts/utils/trend adapters（仍为 RecyclerView，未改 Compose）
- [x] 迁移剩余 Java 文件（进行中；见下方 leftover 清单）
- [ ] 消除重复样板代码，采用 Kotlin 惯用法
- [ ] 统一代码风格（ktlint）— 本轮不引入 ktlint Gradle 插件（避免风格战争）；手写惯用 Kotlin

**Phase 7 language migration this branch:** Remaining first-party Java is **0**. Custom Views, WeatherView implementors, RemoteViews presenters/notifications, widget config, and live wallpaper are Kotlin and stay **View-based** (no Compose rewrite). Vendored `com.xw.repo.BubbleSeekBar*` is intentionally still Java.

**Leftover Java:** only `app/src/main/java/com/xw/repo/Bubble*.java`.

`:app:assembleFdroidDebug` / `:app:assembleGplayDebug` / `:app:testFdroidDebugUnitTest` recorded after this conversion (JDK 21, jvmTarget 17).

### 阶段 8：模块化拆分
- [x] 拆分 `:core`（基础组件/主题/工具）— Android library `namespace` `wangdaye.com.geometricweather.core`；保留原 Kotlin 包名。含 DisplayUtils/LanguageUtils 等通用工具、无天气模型的 `common/ui/widgets`（`TrendRecyclerViewAdapter` 与依赖 `ThemeManager` 的 `FitSystemBarAppBarLayout` 仍在 `:app`）、snackbar、insets helpers，以及原 `app/src/main/res`（避免拆 strings/arrays 的跨 locale 手术）。库代码改为 `import wangdaye.com.geometricweather.core.R`；`:app` 仍用合并后的 `wangdaye.com.geometricweather.R`。
- [x] 拆分 `:data`（网络/数据库/API 服务）— Room（`db/`、`DatabaseHelper`、`FileUtils` + `city_list.txt`）**以及** Retrofit weather JSON/`apis`、`WeatherService*`/`converters`/`WeatherServiceSet`/`WeatherHelper`、Hilt `weather.di.ApiModule` + `location.di.ApiModule`、Baidu IP `BaiduIPLocationApi`/`BaiduIPLocationResult`。`:data` 自带 `buildConfigField`（与 `:app` 同一套 gradle 属性）；运行时 key/语言/降水单位走 `WeatherProviderSettings`（读 Settings 同一套 SharedPreferences，不依赖 `:app`）。OkHttp/`RetrofitModule`/Gzip interceptor 仍在 `:app`（Bugly/`GeometricWeather.debugMode`）。Flavor 定位（Baidu/AMap/GMS stubs）、`LocationService`/`LocationHelper`/`BaiduIPLocationService` 仍在 `:app`。
- [x] 拆分 `:domain`（模型/用例）— `common/basic/models`（天气/地点/options）。Android library（Context/`R`/Parcelable，不是纯 JVM）。用例层未抽；`Temperature` 通过 `exchangeDayNightTempEnabled` 钩子读取设置，避免依赖 `SettingsManager`。
- [x] 拆分 `:feature`（按功能模块，如 main/search/settings）— `:feature:search`（SearchActivity + compose + SearchActivityRepository）与 `:feature:settings`（settings/about/preview-icon Activities + compose 屏）。共享壳层抽到 **`:presentation`**（`GeoActivity` / `ThemeManager` / `GeometricWeatherTheme` / `InAppRoute` / `SettingsManager` / icon `ResourceProvider`），`:core` 仍不依赖 `:domain`。Widget/polling 经 `SettingsAppCallbacks` 留在 `:app`。**`:feature:main` 未拆**：MainActivity + Home `AndroidView` + WeatherView + location list 与 AppWidget/RemoteViews/wallpaper 同进程强耦合；本轮不把 main 或 widgets 迁出 `:app`。
- [x] 配置构建缓存与依赖隔离 — `org.gradle.caching=true` / `org.gradle.parallel=true`；依赖方向 `:app` → `:feature:*` → `:presentation` → `:data` → `:domain` → `:core`。完整 configuration-on-demand / 独立 feature 图未做。

## 横切工作

- **测试保障**：每一阶段完成时保证现有测试通过，且补充必要的单元/集成测试
- **JUnit5 迁移（本轮）**：已从 JUnit4 + PowerMock 迁到 JUnit5 Jupiter；PowerMock **已全部移除**（无 leftover）。`junit-vintage` 未引入。原 PowerMock 静态 `TextUtils` mock（`CardDisplayTest` / `DailyTrendDisplayTest`）改为 mockk `mockkStatic`；`Resources`/`Context` 用 mockk。不再需要 JDK `--add-opens`（那是给 PowerMock 的）。Robolectric 4.14.1 仍在 Version Catalog / `:app` test classpath，但 **4.14 没有官方 `RobolectricExtension`**（JUnit5 需第三方 `tech.apter` Gradle 插件或 vintage + `RobolectricTestRunner`）。当前用例不需要 Android framework：`GreenDaoToRoomMigrationTest` 只测列名映射。Compose UI Test：**未加** unit/instrumented Compose 用例——全屏需要 Hilt + Activity；JVM `runComposeUiTest` 依赖 Robolectric JUnit5。catalog 仍有 `compose-ui-test-junit4`（`:app` `androidTestImplementation`，`src/androidTest` 仍空）。`:core` 无 `src/test` 源。
- **提交粒度**：每个阶段拆分为若干原子提交，便于回滚与 review
- **构建验证**：每个阶段结束执行一次完整 `./gradlew assembleFdroidDebug assembleGplayDebug` 验证

## 非目标 (Out of Scope)

- 不改变 App 的产品功能与 UI 视觉
- 不迁移 AppWidget / RemoteViews（保留 View 系统实现）
- 不迁移 Live Wallpaper / 第三方 SDK 接入逻辑