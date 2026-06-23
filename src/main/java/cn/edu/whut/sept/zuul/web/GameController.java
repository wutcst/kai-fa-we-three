package cn.edu.whut.sept.zuul.web;

import cn.edu.whut.sept.zuul.CommandResult;
import cn.edu.whut.sept.zuul.GameService;
import cn.edu.whut.sept.zuul.GameState;
import cn.edu.whut.sept.zuul.LeaderboardEntry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/state")
    public GameState getState() {
        return gameService.getCurrentState();
    }

    @PostMapping("/command")
    public CommandResult executeCommand(@RequestBody CommandRequest request) {
        String command = request == null ? "" : request.getCommand();
        return gameService.executeCommand(command);
    }

    @PostMapping("/reset")
    public GameState resetGame() {
        gameService.resetGame();
        return gameService.getCurrentState();
    }

    @GetMapping("/leaderboard")
    public List<LeaderboardEntry> getLeaderboard(
            @RequestParam(defaultValue = "20") int limit) {
        return gameService.getLeaderboard(limit);
    }

    @PostMapping("/leaderboard/join")
    public Map<String, Object> joinLeaderboard() {
        gameService.joinLeaderboard();
        return Map.of("success", true);
    }

    @PostMapping("/time")
    public GameState setStartTime(@RequestBody Map<String, Integer> body) {
        int minutes = body.getOrDefault("minutes", 480);
        gameService.setStartTime(minutes);
        return gameService.getCurrentState();
    }
}
