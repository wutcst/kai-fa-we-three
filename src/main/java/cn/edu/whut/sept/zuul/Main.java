package cn.edu.whut.sept.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ConfigurationPropertiesScan("cn.edu.whut.sept.zuul")
public class Main {

    public static void main(String[] args) {
        if (args.length == 0 || !"--cli".equals(args[0])) {
            SpringApplication.run(Main.class, args);
            return;
        }

        DatabaseManager databaseManager;
        try {
            databaseManager = new DatabaseManager();
            databaseManager.initialize();
        } catch (Exception e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            return;
        }

        Game game = new Game(databaseManager);
        game.play();
    }
}
