# Zuul 闯关冒险游戏

> 软件工程实践（二）小组协同开发项目。项目基于经典文字冒险游戏 World of Zuul 进行扩展，实现了 Canvas 图形化 Web 界面、RPG 属性与任务系统、NPC 对话与商店、物品合成、SQLite 完整存档、排行榜、自动化测试和 GitHub Actions 持续集成。

## 项目简介

本项目是一个以「校园探索」为主题的闯关冒险游戏 **World of Zuul · Campus Adventure**。玩家通过浏览器注册/登录，在校园各房间之间移动，与 NPC 对话答题、购买道具、收集材料，在实验室合成 **`perfect_report`** 并提交至校门口提交箱，修复校园网络系统并触发多种结局评分。

项目保留了 World of Zuul 原有的命令式游戏核心，同时接入 Spring Boot Web 后端与 Vue 3 + Canvas 前端。默认以 Web 方式运行，亦可通过 `--cli` 进入控制台模式。开发过程采用多人分支协作、Pull Request 合并、JUnit 测试和 GitHub Actions 自动构建。

## 功能特性

### 游戏核心

- **RPG 属性系统**：等级、经验、HP、攻击/防御/SP、金币、负重与步数统计。
- **房间探索**：6 个校园场景（校门外、剧场、咖啡厅、实验室、办公室、传送室），支持 `go` / `back` 与 WASD 移动。
- **物品与背包**：拾取/丢弃、超重限制、任务物品 `lockedByNpc` 锁定机制。
- **食物增益**：食用 `cookie` / `coffee` 各永久 +5kg 最大负重。
- **NPC 交互**：条件分支对话；管理员/老师答题（`answer`）；咖啡厅商店（`buy`）。
- **物品合成**：在实验室将 `code_data` + `reference` + `signature` 合成为 `perfect_report`（`combine`）。
- **传送机制**：传送室随机传送，Lv.2 解锁；Web 端支持夜间时空传送（调整时间 + 随机跳转）。
- **任务引擎**：`QuestEngine` 驱动主线与支线；`status` / `quests` 查看状态与任务日志。
- **结局系统**：`EndingCalculator` 根据步数、答题、探索等表现判定 7 种结局类型。

### Web 前端（Canvas + Vue 3）

- **多阶段流程**：`login` → `resume`（有存档时）→ `select`（选角色）→ `time`（选出发时间）→ `playing`。
- **Canvas 场景**：房间背景图、可点击 NPC/物品、角色行走动画、场景切换过渡。
- **HUD 状态栏**：时间/天数、EXP/等级、HP、负重、金币；任务、状态、存档、读档快捷按钮。
- **交互面板**：背包 eat/drop、`campus_map` 全图、`welcome_brochure` 欢迎手册、合成台、商店、答题弹窗、提交箱。
- **小地图**：拾取 `campus_map` 后显示已探索房间与当前位置。
- **续玩支持**：老用户登录后可选「继续上次冒险」或「开始新游戏」；Web 端默认使用 `quicksave` 存档名。

### 持久化与账号

- **账号系统**：`register` / `login` / `logout`，密码存储于 SQLite。
- **完整存档**：保存位置、背包、房间物品、任务进度、RPG 属性、游戏时间、结局与统计数据。
- **排行榜**：通关后可写入 `leaderboard` 表，前端可通过 API 查询。

### 工程化

- JUnit 4 单元测试覆盖命令、合成、结局、Repository 与存档服务。
- GitHub Actions 自动执行 `mvn test` 与 `mvn package`。

## 技术栈

| 模块 | 技术 |
|---|---|
| 后端语言 | Java 17 |
| Web 框架 | Spring Boot 2.7.18 |
| 前端页面 | HTML、CSS、JavaScript、Vue 3 CDN、Canvas 2D |
| 数据库 | SQLite 3 |
| 数据库访问 | JDBC |
| 构建工具 | Maven |
| 测试框架 | JUnit 4 |
| 持续集成 | GitHub Actions |

## 项目架构

```text
浏览器前端（Vue 3 + Canvas）
  └── src/main/resources/static/index.html
        │  HTTP/JSON
        ▼
Web 控制层
  └── cn.edu.whut.sept.zuul.web.GameController
        │
        ▼
服务层
  └── GameService / SaveService
        │
        ├── 游戏核心：Game、Player、Room、Item、NPC、CommandWords
        ├── 子系统：QuestEngine、CraftingManager、WorldState、EndingCalculator
        │
        └── 持久化：DatabaseManager、PlayerRepository、SaveRepository、
                    WorldDataRepository、LeaderboardRepository
```

### 分层说明

- **前端交互层**：多阶段页面、Canvas 场景渲染、HUD/背包/小地图，将点击与按键转换为 REST 命令。
- **控制器层**：`/api/game` 下提供状态查询、命令执行、重置、排行榜与时间设置接口。
- **服务层**：统一封装命令执行、`GameState` 同步、存档读写与排行榜写入。
- **游戏模型层**：维护玩家、房间、物品、NPC、任务、合成、胜利/死亡与结局判定。
- **持久化层**：SQLite 建表/迁移、玩家账号、完整快照存档与世界种子数据。
- **测试与 CI 层**：JUnit 验证核心逻辑与数据库行为，GitHub Actions 保证合并后可构建。

## 目录结构

```text
.
├── .github/workflows/maven.yml       # GitHub Actions 自动测试与打包
├── data/                             # SQLite 数据库文件（运行时自动创建）
├── src/
│   ├── main/
│   │   ├── java/cn/edu/whut/sept/zuul/
│   │   │   ├── web/GameController.java
│   │   │   ├── Game.java / GameService.java / GameState.java
│   │   │   ├── Player.java / Room.java / Item.java / NPC.java
│   │   │   ├── QuestEngine.java / CraftingManager.java / EndingCalculator.java
│   │   │   ├── SaveService.java / DatabaseManager.java
│   │   │   ├── *Repository.java    # 玩家、存档、世界、排行榜
│   │   │   └── *Command.java         # 20+ 游戏命令
│   │   └── resources/
│   │       ├── static/index.html     # Vue + Canvas 游戏页面
│   │       ├── static/assets/        # 场景/NPC/角色图片资源
│   │       ├── db/schema.sql         # 数据库建表脚本
│   │       └── application.properties
│   └── test/java/cn/edu/whut/sept/zuul/
│       ├── BagTest.java / GameBasicTest.java / GameServiceTest.java
│       ├── GoCommandTest.java / AnswerCommandTest.java
│       ├── CraftingManagerTest.java / EndingCalculatorTest.java
│       ├── DatabaseManagerTest.java / SaveServiceTest.java
│       └── *RepositoryTest.java
├── pom.xml
├── README.md
└── REPORT.docx
```

## 快速开始

### 环境要求

- JDK 17 或更高版本
- Maven 3.6 或更高版本
- 现代浏览器（Chrome / Edge / Firefox 等）

### 克隆项目

```bash
git clone <repository-url>
cd kai-fa-we-three
```

### 启动 Web 版本（默认）

```bash
mvn spring-boot:run
```

启动成功后，在浏览器中访问：

```text
http://localhost:8080
```

**Web 端推荐流程**：

1. 注册或登录账号；
2. 若有存档，选择「继续上次冒险」或「开始新游戏」；
3. 选择角色形象与出发时间（影响 NPC 出现与氛围）；
4. 在校门外与保安对话开始任务，探索校园、收集材料、合成并提交 `perfect_report`。

### 控制台模式

保留原始命令行玩法，通过 `--cli` 参数启动：

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--cli"
```

或打包后运行：

```bash
mvn package
java -jar target/zuul-1.0-SNAPSHOT.jar --cli
```

## Web 界面说明

| 区域 | 功能 | 对应命令/API |
|---|---|---|
| 登录/续玩 | 注册、登录；有存档时续玩或新游戏 | `register` / `login`；`load quicksave` |
| HUD 状态栏 | 时间、EXP/等级、HP、负重、金币 | 每次命令后刷新 `GameState` |
| Canvas 场景 | 背景、NPC/物品点击、WASD 移动 | `go` / `take` / `talk` / `combine` / `drop` / `buy` |
| 背包栏 | 物品列表、eat/drop | `eat` / `drop` |
| 小地图/全图 | 探索进度；`campus_map` 查看大地图 | 需拾取 `campus_map` |
| 合成台 | 实验室合成 `perfect_report` | `combine perfect_report` |
| 提交箱 | 校门外提交通关物品 | `drop perfect_report` |
| 设置菜单 | 暂停、重开、退出登录 | `/api/game/reset`、`logout` |

## 游戏操作说明

### 命令一览

| 命令 | 说明 | 示例 |
|---|---|---|
| `register` | 注册新账号 | `register Alice 123456` |
| `login` | 登录 | `login Alice 123456` |
| `logout` | 退出登录 | `logout` |
| `go` | 向指定方向移动 | `go east` |
| `back` | 返回上一个房间 | `back` |
| `look` | 查看当前房间 | `look` |
| `take` | 拾取物品 | `take keycard` |
| `drop` | 丢弃/提交物品 | `drop perfect_report` |
| `items` | 查看背包 | `items` |
| `eat` | 食用食物 | `eat cookie` |
| `talk` | 与 NPC 对话 | `talk admin` |
| `answer` | 回答 NPC 题目 | `answer B` |
| `combine` | 合成物品 | `combine perfect_report` |
| `buy` | 在商店购买 | `buy coffee` |
| `status` | 查看角色状态 | `status` |
| `quests` | 查看任务日志 | `quests` |
| `save` | 保存游戏 | `save quicksave` |
| `load` | 读取存档 | `load quicksave` |
| `saves` | 列出存档 | `saves` |
| `delete-save` | 删除存档 | `delete-save quicksave` |
| `help` | 查看帮助 | `help` |
| `quit` | 退出控制台游戏 | `quit` |

### 主线流程（简要）

1. 校门外与 **保安** 对话，拾取 `campus_map` 与 `welcome_brochure`；
2. 到 **办公室** 找 **管理员** 答题，获得 `keycard` 与 `reference`；
3. 到 **剧场** 找 **老师** 答题，获得 `signature`；
4. 持 `keycard` 进入 **实验室** 拾取 `code_data`，在合成台合成 `perfect_report`；
5. 回 **校门外** 向提交箱提交 `perfect_report` 通关。

> 隐藏结局「时空旅者」：多次进入传送室并完成隐藏条件后，与 `perfect_report` 一并提交。

## API 简介

后端接口位于 `/api/game` 下，前端通过 JSON 与后端通信。

| 方法 | 地址 | 说明 |
|---|---|---|
| `GET` | `/api/game/state` | 获取当前 `GameState` |
| `POST` | `/api/game/command` | 执行一条游戏命令 |
| `POST` | `/api/game/reset` | 重置游戏（新游戏） |
| `GET` | `/api/game/leaderboard` | 获取排行榜（`?limit=20`） |
| `POST` | `/api/game/leaderboard/join` | 将当前成绩写入排行榜 |
| `POST` | `/api/game/time` | 设置出发时间（分钟，如 `480` = 08:00） |

执行命令示例：

```bash
curl -X POST http://localhost:8080/api/game/command \
  -H "Content-Type: application/json" \
  -d "{\"command\":\"look\"}"
```

设置时间示例：

```bash
curl -X POST http://localhost:8080/api/game/time \
  -H "Content-Type: application/json" \
  -d "{\"minutes\":840}"
```

## 数据库设计

项目使用 SQLite 保存玩家、存档、世界配置与排行榜。数据库文件默认位于：

```text
data/zuul.db
```

建表脚本位于 `src/main/resources/db/schema.sql`，程序启动时由 `DatabaseManager` 自动建表并迁移。

| 表名 | 作用 |
|---|---|
| `player` | 玩家账号（名称、密码、金币、负重上限等） |
| `game_save` | 存档主表（房间、HP/分数/RPG 属性、时间、结局、统计字段） |
| `inventory_item` | 某次存档中玩家背包物品 |
| `room_item` | 某次存档中各房间剩余物品 |
| `quest_progress` | 任务进度键值对 |
| `world_room` | 世界房间配置 |
| `world_room_exit` | 房间出口连接 |
| `world_item` | 物品配置 |
| `world_room_item` | 房间初始物品关联 |
| `leaderboard` | 排行榜缓存（分数、HP、结局标题等） |

## 测试

运行全部测试：

```bash
mvn test
```

主要测试类：

| 测试类 | 验证内容 |
|---|---|
| `BagTest` / `GameBasicTest` | 背包、负重、基础游戏流程 |
| `GoCommandTest` / `AnswerCommandTest` | 移动门禁/传送、答题逻辑 |
| `CraftingManagerTest` / `EndingCalculatorTest` | 合成配方、结局评分 |
| `GameServiceTest` | REST 服务层命令与状态同步 |
| `DatabaseManagerTest` | 建表、迁移、目录创建 |
| `PlayerRepositoryTest` / `SaveRepositoryTest` / `WorldDataRepositoryTest` | 数据读写 |
| `SaveServiceTest` | 存档完整恢复（位置、背包、任务等） |

## 持续集成

GitHub Actions 工作流：`.github/workflows/maven.yml`

- 触发：任意分支 push、向 `master` 发起 Pull Request
- 步骤：配置 JDK → `mvn -B test` → `mvn -B package -DskipTests` → 上传 jar

## 小组分工

| 成员 | 主要任务 |
|---|---|
| 时宝晗 | Player/Item 背包、RPG 属性、Canvas + Vue 前端与续玩交互 |
| 罗雨婧 | Room/TeleportRoom/NPC/Shop、SQLite 数据库与 Repository 存档 |
| 沈杨 | CommandWords 命令集成、Quest/Crafting/Ending 规则、测试与 CI |

## 项目亮点

- 命令行核心完整保留，并升级为 Canvas 可视化 Web 体验，同时支持 CLI 模式。
- 统一 `GameState` 驱动前后端状态同步，Web 点击/WASD 均转换为后端命令。
- 存档覆盖 RPG 属性、房间物品、任务进度与统计数据，支持登录后一键续玩。
- 主线合成 + 多结局评分 + 排行榜，形成完整闯关闭环。
- JUnit 与 GitHub Actions 保证核心逻辑在协作合并后仍可构建运行。

## 后续改进方向

- 将 `index.html` 拆分为 Vue 组件工程，提升前端可维护性。
- 补充 REST 接口测试与前端 E2E 测试。
- 增加更多关卡、NPC 行为与支线任务。
- 提供 Docker 或一键部署脚本，简化演示环境搭建。

## 许可证

本项目为课程实践项目，仅用于软件工程实践学习与教学展示。
