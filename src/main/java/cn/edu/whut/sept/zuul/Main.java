package cn.edu.whut.sept.zuul;

public class Main {

    public static void main(String[] args) {
        try {
            DatabaseManager databaseManager = new DatabaseManager();
            databaseManager.initialize();
        } catch (Exception e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            return;
        }

        Game game = new Game();
        game.play();
    }
}
