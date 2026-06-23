package cn.edu.whut.sept.zuul;

/**
 * 世界时间状态。
 * 时间以分钟为单位 (0-1439)，480 = 8:00，1080 = 18:00。
 */
public class WorldState {

    private int gameTimeMinutes = 480;   // 默认 8:00 AM
    private int dayCount = 1;

    /** 推进指定分钟数，自动处理跨天 */
    public void advanceTime(int minutes) {
        gameTimeMinutes += minutes;
        while (gameTimeMinutes >= 1440) {
            gameTimeMinutes -= 1440;
            dayCount++;
        }
    }

    public String getTimeDisplay() {
        int hours = gameTimeMinutes / 60;
        int mins = gameTimeMinutes % 60;
        return String.format("%02d:%02d", hours, mins);
    }

    /** 是否白天 (6:00 - 18:00) */
    public boolean isDaytime() {
        return gameTimeMinutes >= 360 && gameTimeMinutes < 1080;
    }

    public String getTimeOfDay() {
        if (gameTimeMinutes >= 360 && gameTimeMinutes < 720) return "早晨";
        if (gameTimeMinutes >= 720 && gameTimeMinutes < 1080) return "下午";
        if (gameTimeMinutes >= 1080 && gameTimeMinutes < 1200) return "傍晚";
        return "深夜";
    }

    public String getWeatherIcon() {
        return isDaytime() ? "☀️" : "🌙";
    }

    public int getGameTimeMinutes() { return gameTimeMinutes; }
    public void setGameTimeMinutes(int gameTimeMinutes) { this.gameTimeMinutes = gameTimeMinutes; }
    public int getDayCount() { return dayCount; }
    public void setDayCount(int dayCount) { this.dayCount = dayCount; }
}
