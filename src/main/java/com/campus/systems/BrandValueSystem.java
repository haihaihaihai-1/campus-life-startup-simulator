package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BrandValueSystem {
    private static final Map<UUID, Integer> brandValue = new HashMap<>();
    private static final Map<UUID, Set<String>> brandActions = new HashMap<>();

    public static class BrandAction {
        final String name;
        final String desc;
        final int cost;
        final int value;

        BrandAction(String name, String desc, int cost, int value) {
            this.name = name;
            this.desc = desc;
            this.cost = cost;
            this.value = value;
        }
    }

    public static final BrandAction[] ACTIONS = {
        new BrandAction("品牌设计", "提升品牌形象", 500, 100),
        new BrandAction("公益形象", "参与公益活动", 1000, 200),
        new BrandAction("媒体报道", "媒体宣传", 2000, 400),
        new BrandAction("用户口碑", "口碑营销", 3000, 600),
        new BrandAction("品牌授权", "品牌授权合作", 8000, 1500),
        new BrandAction("国际化", "品牌国际化", 20000, 4000)
    };

    public static boolean invest(ServerPlayerEntity player, int idx) {
        UUID uuid = player.getUUID();
        if (idx < 1 || idx > ACTIONS.length) return false;
        BrandAction action = ACTIONS[idx - 1];
        Set<String> done = brandActions.getOrDefault(uuid, new HashSet<>());
        if (done.contains(action.name)) {
            player.sendMessage(new StringTextComponent("§c已投资!"), uuid);
            return false;
        }
        int cost = action.cost;
        int value = action.value;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(cost)) {
                done.add(action.name);
                brandActions.put(uuid, done);
                brandValue.merge(uuid, value, Integer::sum);
                player.sendMessage(new StringTextComponent("§a✔ " + action.name + "|品牌价值+" + value), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("§c资金不足!"), uuid);
            return false;
        }).orElse(false);
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        int value = brandValue.getOrDefault(uuid, 0);
        Set<String> done = brandActions.getOrDefault(uuid, new HashSet<>());
        String grade = value >= 10000 ? "§d传奇品牌" : value >= 5000 ? "§a知名品牌" : value >= 2000 ? "§b优质品牌" : value >= 500 ? "§e成长品牌" : "§7新兴品牌";
        player.sendMessage(new StringTextComponent("§6╔════════════════════════╗"), uuid);
        player.sendMessage(new StringTextComponent("§6║  §d★ 品牌价值 " + grade + "  §6║"), uuid);
        player.sendMessage(new StringTextComponent("§6║  §e品牌价值: §6" + value + "  §6║"), uuid);
        player.sendMessage(new StringTextComponent("§6╚═════════════════════════╝"), uuid);
        for (int i = 0; i < ACTIONS.length; i++) {
            BrandAction a = ACTIONS[i];
            boolean has = done.contains(a.name);
            player.sendMessage(new StringTextComponent("§e[" + (i + 1) + "] " + a.name + "|§7 " + a.desc + "|§6 " + a.cost + "金币|§a +" + a.value + "|" + (has ? "§a✔" : "§7✖")), uuid);
        }
        player.sendMessage(new StringTextComponent("§e/brand <1-6> 投资"), uuid);
    }

    public static int getValue(UUID uuid) {
        return brandValue.getOrDefault(uuid, 0);
    }
}
