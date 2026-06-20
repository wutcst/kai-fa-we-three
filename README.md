# Zuul 闯关冒险游戏

> 软件工程实践（二）小组协同开发项目。项目基于经典文字冒险游戏 World of Zuul 进行扩展，实现了 Web 图形化交互、玩家背包、房间探索、NPC 对话、传送房间、数据库存档、自动化测试和 GitHub Actions 持续集成。

## 项目简介

本项目是一个以“校园探索”为主题的闯关冒险游戏。玩家通过 Web 页面登录游戏，在不同房间之间移动，观察环境、拾取物品、与 NPC 对话、管理背包、保存和读取进度，并最终完成任务物品提交以通关。

项目保留了 World of Zuul 原有的命令式游戏核心，同时加入 Spring Boot Web 后端和 Vue 前端页面，使游戏既可以通过浏览器演示，也可以保留控制台运行方式。开发过程采用多人分支协作、Pull Request 合并、JUnit 测试和 GitHub Actions 自动构建，重点体现软件工程实践中的需求拆分、团队协作、版本控制、测试验证和持续集成过程。

## 功能特性

- Web 图形化游戏界面：提供场景展示、玩家状态、背包、房间物品、NPC、命令输入和操作日志。
- 玩家登录：支持玩家名称登录，为后续存档和读档提供用户身份。
- 房间探索：支持 `go` 命令和快捷移动按钮，在多个房间之间进行探索。
- 物品系统：房间中可放置多个物品，物品具有名称、描述和重量。
- 背包系统：玩家可以使用 `take` 和 `drop` 拾取或丢弃物品，并受到最大负重限制。
- 魔法饼干：玩家可以使用 `eat` 命令食用特殊物品，提高可携带重量。
- NPC 对话：房间中支持 NPC，玩家可使用 `talk` 命令获取提示信息。
- 传送房间：部分房间具有随机传送效果，增强游戏不确定性。
- 回退机制：支持 `back` 命令逐步返回经过的房间。
- 生命值、分数与胜利判定：游戏具有明确闯关目标和状态反馈。
- 数据库存档：基于 SQLite 保存玩家、存档、背包、房间物品和任务进度。
- 自动化测试：覆盖背包、游戏流程、服务接口、数据库、Repository 和存档服务。
- 持续集成：通过 GitHub Actions 自动执行 Maven 测试和打包。

## 技术栈

| 模块 | 技术 |
|---|---|
| 后端语言 | Java 8 |
| Web 框架 | Spring Boot 2.7.18 |
| 前端页面 | HTML、CSS、JavaScript、Vue 3 CDN |
| 数据库 | SQLite |
| 数据库访问 | JDBC |
| 构建工具 | Maven |
| 测试框架 | JUnit 4 |
| 持续集成 | GitHub Actions |

## 项目架构

```text
浏览器前端
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
        ├── 游戏核心模型：Game、Player、Room、Item、NPC、Command
        │
        └── 持久化层：DatabaseManager、PlayerRepository、SaveRepository、WorldDataRepository
```

### 分层说明

- 前端交互层：负责展示游戏画面、玩家状态、背包、房间信息和操作日志，并向后端发送命令。
- 控制器层：通过 REST API 接收前端请求，将命令转交给服务层处理。
- 服务层：统一管理命令执行、状态封装、游戏重置、保存和读取存档。
- 游戏模型层：维护玩家、房间、物品、NPC、命令、生命值、分数和胜利状态等核心规则。
- 持久化层：负责 SQLite 数据库初始化、玩家信息、存档数据和世界配置读写。
- 测试与 CI 层：通过 JUnit 和 GitHub Actions 验证核心逻辑、服务接口和数据库行为。

## 目录结构

```text
.
├── .github/workflows/maven.yml       # GitHub Actions 自动测试与打包
├── data/                             # SQLite 数据库文件目录
├── docs/                             # 项目说明文档
├── src/
│   ├── main/
│   │   ├── java/cn/edu/whut/sept/zuul/
│   │   │   ├── web/                  # Spring Boot REST 控制器
│   │   │   ├── Game.java             # 游戏核心流程
│   │   │   ├── GameService.java      # Web 与游戏核心之间的服务层
│   │   │   ├── Player.java           # 玩家状态与背包
│   │   │   ├── Room.java             # 房间、出口、物品、NPC
│   │   │   ├── SaveService.java      # 存档业务逻辑
│   │   │   └── *Command.java         # 各类游戏命令
│   │   └── resources/
│   │       ├── static/index.html     # Vue 游戏页面
│   │       ├── static/assets/        # 页面资源
│   │       └── db/schema.sql         # 数据库建表脚本
│   └── test/java/cn/edu/whut/sept/zuul/
│       ├── BagTest.java
│       ├── GameBasicTest.java
│       ├── GameServiceTest.java
│       ├── SaveServiceTest.java
│       └── *RepositoryTest.java
├── pom.xml
├── README.md
└── REPORT.docx
```

## 快速开始

### 环境要求

- JDK 8 或更高版本
- Maven 3.6 或更高版本
- 现代浏览器

### 克隆项目

```bash
git clone <repository-url>
cd kai-fa-we-three
```

### 启动 Web 版本

```bash
mvn spring-boot:run
```

启动成功后，在浏览器中访问：

```text
http://localhost:8080
```

### 控制台模式

项目保留了原始控制台运行方式，可通过 `--cli` 参数启动：

```bash
mvn -q exec:java -Dexec.mainClass=cn.edu.whut.sept.zuul.Main -Dexec.args="--cli"
```

如果当前环境未配置 `exec-maven-plugin`，也可以先打包后运行：

```bash
mvn package
java -jar target/zuul-1.0-SNAPSHOT.jar --cli
```

## 游戏操作说明

| 命令 | 说明 | 示例 |
|---|---|---|
| `login` | 登录或切换玩家 | `login Alice` |
| `go` | 向指定方向移动 | `go east` |
| `look` | 查看当前房间环境 | `look` |
| `take` | 拾取房间中的物品 | `take key` |
| `drop` | 丢弃背包中的物品 | `drop key` |
| `items` | 查看背包和物品信息 | `items` |
| `eat` | 食用魔法饼干 | `eat cookie` |
| `talk` | 与 NPC 对话 | `talk teacher` |
| `back` | 返回上一个房间 | `back` |
| `save` | 保存当前游戏 | `save default` |
| `load` | 读取指定存档 | `load default` |
| `saves` | 查看已有存档 | `saves` |
| `delete-save` | 删除存档 | `delete-save default` |
| `help` | 查看帮助 | `help` |
| `quit` | 退出控制台游戏 | `quit` |

## API 简介

后端接口位于 `/api/game` 下，前端页面通过 JSON 与后端通信。

| 方法 | 地址 | 说明 |
|---|---|---|
| `GET` | `/api/game/state` | 获取当前游戏状态 |
| `POST` | `/api/game/command` | 执行一条游戏命令 |
| `POST` | `/api/game/reset` | 重置游戏状态 |

执行命令示例：

```bash
curl -X POST http://localhost:8080/api/game/command \
  -H "Content-Type: application/json" \
  -d "{\"command\":\"look\"}"
```

## 数据库设计

项目使用 SQLite 保存玩家信息、游戏存档和世界配置。数据库文件默认位于：

```text
data/zuul.db
```

建表脚本位于：

```text
src/main/resources/db/schema.sql
```

核心数据表如下：

| 表名 | 作用 |
|---|---|
| `player` | 保存玩家账号和基础属性 |
| `game_save` | 保存一次游戏存档的全局状态 |
| `inventory_item` | 保存玩家背包物品 |
| `room_item` | 保存各房间剩余物品 |
| `quest_progress` | 保存任务进度 |
| `world_room` | 保存初始房间配置 |
| `world_room_exit` | 保存房间出口关系 |
| `world_item` | 保存初始物品配置 |
| `world_room_item` | 保存房间与物品初始关系 |

## 测试

运行全部测试：

```bash
mvn test
```

主要测试内容：

- `BagTest`：验证背包、负重、拾取和丢弃逻辑。
- `GameBasicTest`：验证基础游戏流程。
- `GameServiceTest`：验证 Web 服务层命令执行和状态同步。
- `DatabaseManagerTest`：验证数据库初始化和连接。
- `PlayerRepositoryTest`：验证玩家数据读写。
- `SaveRepositoryTest`：验证存档数据读写。
- `WorldDataRepositoryTest`：验证世界配置加载。
- `SaveServiceTest`：验证保存、读取和恢复游戏状态。

## 持续集成

项目配置了 GitHub Actions 工作流：

```text
.github/workflows/maven.yml
```

触发条件：

- 任意分支 push
- 向 `master` 发起 Pull Request

流水线步骤：

1. 拉取仓库代码。
2. 配置 JDK 环境。
3. 执行 `mvn -B test`。
4. 执行 `mvn -B package -DskipTests`。
5. 上传构建生成的 jar 文件。

## 协作开发流程

本项目采用多人 feature 分支开发模式。每位成员在自己的功能分支上完成任务，推送到 GitHub 后通过 Pull Request 合并。

第一轮迭代主要分支：

- `feature/player-item`：玩家、物品、背包、魔法饼干。
- `feature/room-npc`：房间、传送房间、NPC 对话。
- `shenyang`：命令系统、回退、生命值、分数和胜利规则。

第二轮迭代主要分支：

- `database`：SQLite 数据库、存档、世界配置持久化。
- `feature/test-ci`：JUnit 测试、Maven 构建、GitHub Actions。
- `feature/ui-rewrite`：Spring Boot Web 接口和 Vue 前端页面。

合并原则：

- 每个成员先在个人分支完成本地自测。
- 推送后通过 Pull Request 进行集成。
- 合并前检查冲突、接口影响和测试结果。
- CI 通过后再合入主分支。

## 小组分工

| 成员 | 主要任务 |
|---|---|
| 成员一 | 玩家模型、物品与背包、魔法饼干、Web 前端页面与交互设计 |
| 成员二 | 房间系统、传送房间、NPC 对话、SQLite 数据库与存档表设计 |
| 成员三 | 命令系统、游戏规则、生命值与胜利条件、测试体系与持续集成 |

## 项目亮点

- 在原有命令行游戏基础上完成 Web 化改造，同时保留 CLI 模式。
- 使用统一 `GameState` 解决前后端状态同步问题。
- 存档不仅保存玩家位置，还保存背包、房间物品、任务进度等完整游戏状态。
- 通过 JUnit 测试和 GitHub Actions 保证核心逻辑在合并后仍可运行。
- 通过两轮迭代体现了需求分析、任务分工、分支协作、代码集成和项目交付过程。

## 后续改进方向

- 将前端页面进一步拆分为组件，提高可维护性。
- 增加更多关卡、NPC 行为和任务线。
- 补充接口测试和前端自动化测试。
- 对数据库访问层进行进一步抽象，减少重复 JDBC 代码。
- 增加部署脚本或 Docker 配置，方便在不同环境中运行。

## 许可证

本项目为课程实践项目，仅用于软件工程实践学习与教学展示。
s