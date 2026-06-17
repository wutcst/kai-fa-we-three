package cn.edu.whut.sept.zuul.web;

import cn.edu.whut.sept.zuul.CommandResult;
import cn.edu.whut.sept.zuul.GameService;
import cn.edu.whut.sept.zuul.GameState;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
