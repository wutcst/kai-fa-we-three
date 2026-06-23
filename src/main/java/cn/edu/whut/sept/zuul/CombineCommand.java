package cn.edu.whut.sept.zuul;

import java.util.Map;

public class CombineCommand extends Command {
    @Override
    public boolean execute(Game game) {
        Player player = game.getPlayer();
        CraftingManager cm = game.getCraftingManager();

        if (!hasSecondWord()) {
            // 显示可合成的所有配方
            System.out.println("=== 合成工作台 ===");
            boolean any = false;
            for (Map.Entry<String, java.util.List<String>> e : cm.getRecipes().entrySet()) {
                String result = e.getKey();
                java.util.List<String> ings = e.getValue();
                boolean canCraft = true;
                for (String ing : ings) {
                    if (!game.hasItemInInventory(ing)) { canCraft = false; break; }
                }
                String status = canCraft ? "✅ 可合成" : "❌ 缺材料";
                System.out.println(status + " " + result + " ← " + String.join(" + ", ings));
                System.out.println("  " + cm.getDescription(result));
                any = true;
            }
            if (!any) System.out.println("暂无可合成配方。");
            return false;
        }

        String target = getSecondWord();
        java.util.List<String> ingredients = cm.getIngredients(target);
        if (ingredients == null) {
            System.out.println("没有这个合成配方：" + target);
            return false;
        }

        // 检查材料
        for (String ing : ingredients) {
            if (!game.hasItemInInventory(ing)) {
                System.out.println("缺少材料：" + ing + "，无法合成 " + target);
                return false;
            }
        }

        // 执行合成
        for (String ing : ingredients) {
            player.dropItem(ing);
        }
        player.takeItem(new Item(target, target.equals("perfect_report") ? 3 : 1));
        System.out.println("✨ 合成成功！获得了【" + target + "】！");

        // 完美报告的奖励
        if ("perfect_report".equals(target)) {
            game.addScore(50);
            game.setHp(game.getHp() + 20);
            System.out.println("分数+50，生命值+20！");
            Map<String, String> qp = game.getQuestProgressMap();
            QuestEngine qe = game.getQuestEngine();
            if (qe.advanceObjective("main_quest", QuestObjective.ObjectiveType.COMBINE,
                    "perfect_report", qp)) {
                qe.checkAndAdvanceStage("main_quest", qp, game.getPlayer());
            }
        } else if ("study_notes".equals(target)) {
            System.out.println("📖 学习笔记：查看后可获得下一道题的答案提示。");
        } else if ("energy_boost".equals(target)) {
            System.out.println("⚡ 能量合剂：使用后连续两回合答题伤害翻倍！");
        }

        return false;
    }
}
