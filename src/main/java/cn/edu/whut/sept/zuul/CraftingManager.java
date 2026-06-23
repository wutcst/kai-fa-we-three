package cn.edu.whut.sept.zuul;

import java.util.*;

/**
 * 合成管理器 —— 管理所有合成配方，检查材料、执行合成。
 */
public class CraftingManager {

    /** 配方：结果物品 → 所需材料列表 */
    private final Map<String, List<String>> recipes = new LinkedHashMap<>();
    /** 配方描述 */
    private final Map<String, String> descriptions = new LinkedHashMap<>();

    public CraftingManager() {
        // 主线：完美实验报告
        register("perfect_report",
                Arrays.asList("code_data", "reference", "signature"),
                "完美实验报告 — 主线任务物品，提交到正门即可通关");

        // 学习笔记：查看后可获得答题提示
        register("study_notes",
                Arrays.asList("notebook", "pen"),
                "学习笔记 — 使用后可查看一道题的答案提示");

        // 能量合剂：连续两回合答题伤害翻倍
        register("energy_boost",
                Arrays.asList("energy_drink", "coffee"),
                "能量合剂 — 使用后连续两回合答题伤害翻倍");

    }

    private void register(String result, List<String> ingredients, String desc) {
        recipes.put(result, ingredients);
        descriptions.put(result, desc);
    }

    /** 获取所有配方 */
    public Map<String, List<String>> getRecipes() { return recipes; }

    /** 获取配方描述 */
    public String getDescription(String result) {
        return descriptions.getOrDefault(result, "");
    }

    /**
     * 检查玩家背包是否满足某配方的材料要求。
     * @return 可合成的结果物品名，null 表示没有可合成的
     */
    public String findCraftable(Player player) {
        for (Map.Entry<String, List<String>> entry : recipes.entrySet()) {
            if (canCraft(player, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    /** 获取所有可合成的配方 */
    public List<String> findAllCraftable(Player player) {
        List<String> results = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : recipes.entrySet()) {
            if (canCraft(player, entry.getValue())) {
                results.add(entry.getKey());
            }
        }
        return results;
    }

    /** 获取某个配方的材料是否齐全 */
    public String getMissingIngredients(Player player, String result) {
        List<String> ingredients = recipes.get(result);
        if (ingredients == null) return "未知配方";
        StringBuilder sb = new StringBuilder();
        for (String ing : ingredients) {
            boolean has = false;
            for (Item item : player.getInventoryItems()) {
                if (item.getDescription().equals(ing)) { has = true; break; }
            }
            if (!has) sb.append(ing).append(" ");
        }
        return sb.toString().trim();
    }

    /** 获取某个结果物品的配方（null=没有） */
    public List<String> getIngredients(String result) {
        return recipes.get(result);
    }

    /** 获取所有配方中需要的材料总数（用于探索者判定） */
    public Set<String> getAllIngredientNames() {
        Set<String> names = new HashSet<>();
        for (List<String> list : recipes.values()) {
            names.addAll(list);
        }
        return names;
    }

    private boolean canCraft(Player player, List<String> ingredients) {
        for (String ing : ingredients) {
            boolean has = false;
            for (Item item : player.getInventoryItems()) {
                if (item.getDescription().equals(ing)) { has = true; break; }
            }
            if (!has) return false;
        }
        return true;
    }
}
