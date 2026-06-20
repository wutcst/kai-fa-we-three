-- player: 玩家档案表，保存玩家基础信息
CREATE TABLE IF NOT EXISTS player (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,
    max_weight REAL NOT NULL DEFAULT 10,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);

-- game_save: 游戏存档表，保存某一时刻的游戏状态
CREATE TABLE IF NOT EXISTS game_save (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    player_id INTEGER NOT NULL,
    save_name TEXT NOT NULL DEFAULT 'default',
    current_room_name TEXT NOT NULL,
    score INTEGER NOT NULL DEFAULT 0,
    health INTEGER NOT NULL DEFAULT 100,
    current_weight REAL NOT NULL DEFAULT 0,
    max_weight REAL NOT NULL DEFAULT 10,
    is_victory INTEGER NOT NULL DEFAULT 0,
    saved_at TEXT NOT NULL,
    FOREIGN KEY (player_id) REFERENCES player(id) ON DELETE CASCADE,
    UNIQUE(player_id, save_name)
);

-- inventory_item: 背包物品表，保存某次存档中玩家携带的物品
CREATE TABLE IF NOT EXISTS inventory_item (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    save_id INTEGER NOT NULL,
    item_name TEXT NOT NULL,
    weight REAL NOT NULL,
    FOREIGN KEY (save_id) REFERENCES game_save(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_game_save_player_id ON game_save(player_id);
CREATE INDEX IF NOT EXISTS idx_inventory_item_save_id ON inventory_item(save_id);

-- room_item: 房间物品表，保存各房间剩余物品（世界状态）
CREATE TABLE IF NOT EXISTS room_item (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    save_id INTEGER NOT NULL,
    room_name TEXT NOT NULL,
    item_name TEXT NOT NULL,
    weight REAL NOT NULL,
    FOREIGN KEY (save_id) REFERENCES game_save(id) ON DELETE CASCADE
);

-- quest_progress: 任务进度表，按 quest_key 保存任务状态
CREATE TABLE IF NOT EXISTS quest_progress (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    save_id INTEGER NOT NULL,
    quest_key TEXT NOT NULL,
    progress_value TEXT NOT NULL,
    FOREIGN KEY (save_id) REFERENCES game_save(id) ON DELETE CASCADE,
    UNIQUE(save_id, quest_key)
);

CREATE INDEX IF NOT EXISTS idx_room_item_save_id ON room_item(save_id);
CREATE INDEX IF NOT EXISTS idx_quest_progress_save_id ON quest_progress(save_id);

-- world_room: 游戏世界房间配置
CREATE TABLE IF NOT EXISTS world_room (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,
    description TEXT NOT NULL,
    is_start_room INTEGER NOT NULL DEFAULT 0,
    room_type TEXT NOT NULL DEFAULT 'normal'
);

-- world_room_exit: 房间出口连接
CREATE TABLE IF NOT EXISTS world_room_exit (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    from_room_id INTEGER NOT NULL,
    direction TEXT NOT NULL,
    to_room_id INTEGER NOT NULL,
    FOREIGN KEY (from_room_id) REFERENCES world_room(id) ON DELETE CASCADE,
    FOREIGN KEY (to_room_id) REFERENCES world_room(id) ON DELETE CASCADE,
    UNIQUE(from_room_id, direction)
);

-- world_item: 物品配置
CREATE TABLE IF NOT EXISTS world_item (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,
    description TEXT NOT NULL,
    weight REAL NOT NULL
);

-- world_room_item: 房间初始物品关联
CREATE TABLE IF NOT EXISTS world_room_item (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    room_id INTEGER NOT NULL,
    item_id INTEGER NOT NULL,
    FOREIGN KEY (room_id) REFERENCES world_room(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES world_item(id) ON DELETE CASCADE,
    UNIQUE(room_id, item_id)
);

CREATE INDEX IF NOT EXISTS idx_world_room_exit_from ON world_room_exit(from_room_id);
CREATE INDEX IF NOT EXISTS idx_world_room_item_room ON world_room_item(room_id);

-- leaderboard: 排行榜视图所需的高分缓存表
CREATE TABLE IF NOT EXISTS leaderboard (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    player_id INTEGER NOT NULL,
    player_name TEXT NOT NULL,
    score INTEGER NOT NULL DEFAULT 0,
    health INTEGER NOT NULL DEFAULT 100,
    is_victory INTEGER NOT NULL DEFAULT 0,
    saved_at TEXT NOT NULL,
    FOREIGN KEY (player_id) REFERENCES player(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_leaderboard_score ON leaderboard(score DESC);
