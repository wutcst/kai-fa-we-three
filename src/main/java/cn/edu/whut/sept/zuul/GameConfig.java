package cn.edu.whut.sept.zuul;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 游戏配置类，从 application.properties 读取可配置参数。
 */
@Configuration
@ConfigurationProperties(prefix = "game")
public class GameConfig {

    private DatabaseConfig database = new DatabaseConfig();
    private PlayerConfig player = new PlayerConfig();

    public DatabaseConfig getDatabase() {
        return database;
    }

    public void setDatabase(DatabaseConfig database) {
        this.database = database;
    }

    public PlayerConfig getPlayer() {
        return player;
    }

    public void setPlayer(PlayerConfig player) {
        this.player = player;
    }

    /**
     * 数据库相关配置。
     */
    public static class DatabaseConfig {
        private String path = "data/zuul.db";

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    /**
     * 玩家默认配置。
     */
    public static class PlayerConfig {
        private String defaultName = "Adventurer";
        private double defaultMaxWeight = 10;
        private int initialHp = 100;
        private int initialScore = 0;

        public String getDefaultName() {
            return defaultName;
        }

        public void setDefaultName(String defaultName) {
            this.defaultName = defaultName;
        }

        public double getDefaultMaxWeight() {
            return defaultMaxWeight;
        }

        public void setDefaultMaxWeight(double defaultMaxWeight) {
            this.defaultMaxWeight = defaultMaxWeight;
        }

        public int getInitialHp() {
            return initialHp;
        }

        public void setInitialHp(int initialHp) {
            this.initialHp = initialHp;
        }

        public int getInitialScore() {
            return initialScore;
        }

        public void setInitialScore(int initialScore) {
            this.initialScore = initialScore;
        }
    }
}
