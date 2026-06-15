package cn.edu.whut.sept.zuul;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin // 允许跨域，前端JS正常调用
@RestController
@RequestMapping("/game")
public class GameController {
    private static final GameService gameService = new GameService();

    /**
     * 执行游戏指令接口
     */
    @PostMapping("/command")
    public CommandResult executeCommand(@RequestParam String input) {
        return gameService.executeCommand(input);
    }

    /**
     * 获取当前游戏状态接口
     */
    @GetMapping("/state")
    public GameState getCurrentState() {
        return gameService.getCurrentState();
    }
}