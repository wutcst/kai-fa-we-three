# World of Zuul 数据库表结构说明

本文档描述项目 SQLite 数据库的核心表结构，可用于实训报告（REPORT）中的数据库设计章节。

## 1. 设计概述

数据库文件默认路径：`data/zuul.db`

系统采用 3 张核心数据表，形成如下关系：

```text
player (1) ──< (N) game_save (1) ──< (N) inventory_item
```

- 一名玩家可以拥有多个存档槽位。
- 每个存档记录一次游戏进度。
- 每个存档对应多条背包物品记录。

## 2. 表结构详情

### 2.1 player（玩家表）

用于保存玩家档案信息，是存档数据的归属主体。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | INTEGER | PRIMARY KEY, AUTOINCREMENT | 玩家唯一编号 |
| name | TEXT | NOT NULL, UNIQUE | 玩家名称 |
| max_weight | REAL | NOT NULL, DEFAULT 10 | 最大负重上限 |
| created_at | TEXT | NOT NULL | 创建时间（ISO-8601 字符串） |
| updated_at | TEXT | NOT NULL | 最近更新时间 |

**用途示例：**

- 注册新玩家时插入一条记录。
- 玩家吃掉魔法饼干后，更新 `max_weight` 与 `updated_at`。

---

### 2.2 game_save（游戏存档表）

用于保存某一时刻的游戏状态，对应 `GameState` 中的核心字段。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | INTEGER | PRIMARY KEY, AUTOINCREMENT | 存档唯一编号 |
| player_id | INTEGER | NOT NULL, FOREIGN KEY | 关联玩家 ID |
| save_name | TEXT | NOT NULL, DEFAULT 'default' | 存档槽名称，如 default / slot1 |
| current_room_name | TEXT | NOT NULL | 当前所在房间 |
| score | INTEGER | NOT NULL, DEFAULT 0 | 当前分数 |
| health | INTEGER | NOT NULL, DEFAULT 100 | 当前生命值 |
| current_weight | REAL | NOT NULL, DEFAULT 0 | 当前背包总重量 |
| is_victory | INTEGER | NOT NULL, DEFAULT 0 | 是否胜利（0=否，1=是） |
| saved_at | TEXT | NOT NULL | 存档保存时间 |

**约束说明：**

- `FOREIGN KEY (player_id) REFERENCES player(id) ON DELETE CASCADE`
- `UNIQUE(player_id, save_name)`：同一玩家下每个存档槽仅保留一条记录。

**用途示例：**

- 玩家点击“保存游戏”时，写入或更新对应存档。
- 读取存档时，根据 `player_id + save_name` 恢复房间、生命值、分数等状态。

---

### 2.3 inventory_item（背包物品表）

用于保存某次存档中玩家背包里的物品明细。

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | INTEGER | PRIMARY KEY, AUTOINCREMENT | 物品记录编号 |
| save_id | INTEGER | NOT NULL, FOREIGN KEY | 关联存档 ID |
| item_name | TEXT | NOT NULL | 物品名称 |
| weight | REAL | NOT NULL | 物品重量 |

**约束说明：**

- `FOREIGN KEY (save_id) REFERENCES game_save(id) ON DELETE CASCADE`
- 删除存档时，会自动删除其关联的背包物品。

**用途示例：**

- 保存游戏前，先删除该存档旧的 `inventory_item`，再批量插入当前背包物品。
- 读档后，根据本表重建玩家背包。

## 3. 索引设计

| 索引名 | 表 | 字段 | 作用 |
|--------|----|------|------|
| idx_game_save_player_id | game_save | player_id | 加速按玩家查询存档 |
| idx_inventory_item_save_id | inventory_item | save_id | 加速按存档查询背包物品 |

## 4. 与游戏对象的映射关系

| 游戏对象 / 类 | 对应数据表 | 说明 |
|---------------|------------|------|
| Player.name | player.name | 玩家名称 |
| Player.maxWeight | player.max_weight | 最大负重 |
| GameState.currentRoomName | game_save.current_room_name | 当前房间 |
| GameState.score | game_save.score | 分数 |
| GameState.health | game_save.health | 生命值 |
| GameState.currentWeight | game_save.current_weight | 当前负重 |
| GameState.inventoryItems | inventory_item | 背包物品列表 |
| Item.description / weight | inventory_item.item_name / weight | 单个物品信息 |

## 5. 建表 SQL 文件位置

完整建表语句位于：

`src/main/resources/db/schema.sql`

程序首次运行时，`DatabaseManager.initialize()` 会自动执行该 SQL 文件，创建全部数据表。

## 6. 示例数据流

### 保存游戏

1. 在 `player` 表中确保玩家存在。
2. 向 `game_save` 写入当前房间、生命值、分数等信息。
3. 向 `inventory_item` 批量写入背包中的每件物品。

### 读取游戏

1. 根据 `player_id` 和 `save_name` 查询 `game_save`。
2. 根据 `save_id` 查询 `inventory_item`。
3. 将查询结果还原为 `GameState` 与 `Player` 对象。
