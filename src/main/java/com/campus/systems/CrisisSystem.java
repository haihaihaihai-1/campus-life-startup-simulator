package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.*;

/**
 * 危机管理系统 - 突发危机处理
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CrisisSystem {

    public static class Crisis {
        public String name, description;
        public int damagePerTick, maxDuration;
        public CrisisAction[] actions;

        public Crisis(String name, String desc, int dmg, int dur, CrisisAction[] actions) {
            this.name = name; this.description = desc; this.damagePerTick = dmg; this.maxDuration = dur; this.actions = actions;
        }
    }

    public static class CrisisAction {
        public String name;
        public int cost;
        public int resolvePercent;
        public String description;

        public CrisisAction(String name, int cost, int resolve, String desc) {
            this.name = name; this.cost = cost; this.resolvePercent = resolve; this.description = desc;
        }
    }

    public static final Crisis[] CRISIS_POOL = {
        new Crisis("\u8d44\u91d1\u94fe\u65ad\u88c2", "\u73b0\u91d1\u6d41\u7d27\u5f20\uff0c\u6bcf\u79d2\u635f\u5931\u8d44\u91d1", 5, 3600, new CrisisAction[]{
            new CrisisAction("\u7d27\u6025\u878d\u8d44", 500, 50, "\u82b1\u8d39500\u91d1\u5e01\u89e3\u51b350%"),
            new CrisisAction("\u88c1\u5458\u8282\u6d41", 200, 30, "\u88c1\u5458\u8282\u6d41\u89e3\u51b330%"),
            new CrisisAction("\u5168\u529b\u6295\u5165", 1000, 100, "\u5168\u529b\u89e3\u51b3")
        }),
        new Crisis("\u5458\u5de5\u7f62\u5de5", "\u5458\u5de5\u4e0d\u6ee1\u7f62\u5de5\uff0c\u6536\u5165\u505c\u6ede", 3, 2400, new CrisisAction[]{
            new CrisisAction("\u52a0\u85aa\u5b89\u629a", 800, 60, "\u52a0\u85aa\u89e3\u51b360%"),
            new CrisisAction("\u8c08\u5224\u534f\u5546", 300, 40, "\u8c08\u5224\u89e3\u51b340%"),
            new CrisisAction("\u5168\u989d\u5151\u73b0", 1500, 100, "\u5168\u989d\u89e3\u51b3")
        }),
        new Crisis("\u4ea7\u54c1\u53ec\u56de", "\u4ea7\u54c1\u8d28\u91cf\u95ee\u9898\uff0c\u9700\u7d27\u6025\u5904\u7406", 8, 1800, new CrisisAction[]{
            new CrisisAction("\u5feb\u901f\u53ec\u56de", 600, 70, "\u5feb\u901f\u89e3\u51b370%"),
            new CrisisAction("\u8865\u507f\u5ba2\u6237", 400, 40, "\u8865\u507f\u89e3\u51b340%"),
            new CrisisAction("\u5168\u9762\u6574\u6539", 1200, 100, "\u5168\u9762\u89e3\u51b3")
        }),
        new Crisis("\u7ade\u4e89\u8005\u5165\u4fb5", "\u5f3a\u52bf\u7ade\u4e89\u5bf9\u624b\u62a2\u593a\u5e02\u573a", 4, 3000, new CrisisAction[]{
            new CrisisAction("\u4ef7\u683c\u6218", 700, 50, "\u4ef7\u683c\u6218\u89e3\u51b350%"),
            new CrisisAction("\u5dee\u5f02\u5316", 500, 40, "\u5dee\u5f02\u5316\u89e3\u51b340%"),
            new CrisisAction("\u6536\u8d2d\u5bf9\u624b", 2000, 100, "\u6536\u8d2d\u89e3\u51b3")
        })
    };

    private static final Map<UUID, ActiveCrisis> activeCrises = new HashMap<>();
    private static int tickCounter = 0;
    private static final Random RAND = new Random();

    public static class ActiveCrisis {
        public int crisisIdx;
        public int remainingTicks;
        public int resolveProgress;

        public ActiveCrisis(int idx) { this.crisisIdx = idx; this.remainingTicks = CRISIS_POOL[idx].maxDuration; this.resolveProgress = 0; }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;

        // 随机触发危机
        if (tickCounter % 18000 == 0 && ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                if (!activeCrises.containsKey(player.getUUID()) && RAND.nextInt(100) < 30) {
                    triggerCrisis(player);
                }
            }
        }

        // 危机每秒造成损失
        if (tickCounter % 20 == 0) {
            Iterator<Map.Entry<UUID, ActiveCrisis>> it = activeCrises.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UUID, ActiveCrisis> entry = it.next();
                ActiveCrisis ac = entry.getValue();
                ac.remainingTicks -= 20;

                ServerPlayerEntity player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(entry.getKey());
                if (player != null) {
                    int damage = CRISIS_POOL[ac.crisisIdx].damagePerTick;
                    player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.spendMoney(Math.min(damage, m.getMoney())));
                }

                if (ac.remainingTicks <= 0 || ac.resolveProgress >= 100) {
                    if (player != null) {
                        if (ac.resolveProgress >= 100) {
                            player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5371\u673a\u5df2\u89e3\u51b3! " + CRISIS_POOL[ac.crisisIdx].name), player.getUUID());
                        } else {
                            player.sendMessage(new StringTextComponent("\u00a7c\u5371\u673a\u81ea\u7136\u7ed3\u675f\uff0c\u635f\u5931\u5df2\u9020\u6210"), player.getUUID());
                        }
                    }
                    it.remove();
                }
            }
        }
    }

    private static void triggerCrisis(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        int idx = RAND.nextInt(CRISIS_POOL.length);
        activeCrises.put(uuid, new ActiveCrisis(idx));
        Crisis c = CRISIS_POOL[idx];

        player.sendMessage(new StringTextComponent("\u00a7c\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7c\u2551  \u26a0 \u5371\u673a\u8b66\u62a5! " + c.name + "  \u00a7c\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7c\u2551  \u00a7f" + c.description + "  \u00a7c\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7c\u2551  \u00a7e\u6bcf\u79d2\u635f\u5931: " + c.damagePerTick + "\u91d1\u5e01 | \u00a7e\u6301\u7eed: " + (c.maxDuration/1200) + "\u5206\u949f  \u00a7c\u2551"), uuid);
        for (int i = 0; i < c.actions.length; i++) {
            player.sendMessage(new StringTextComponent("\u00a7c\u2551  \u00a7e/" + (i+1) + " " + c.actions[i].name + " (" + c.actions[i].cost + "\u91d1\u5e01, \u89e3\u51b3" + c.actions[i].resolvePercent + "%)  \u00a7c\u2551"), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7c\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /crisis solve <1-3> \u5904\u7406\u5371\u673a!"), uuid);
    }

    public static boolean solve(ServerPlayerEntity player, int actionIdx) {
        UUID uuid = player.getUUID();
        ActiveCrisis ac = activeCrises.get(uuid);
        if (ac == null) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6d3b\u8dc3\u5371\u673a!"), uuid); return false; }

        Crisis c = CRISIS_POOL[ac.crisisIdx];
        if (actionIdx < 1 || actionIdx > c.actions.length) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u64cd\u4f5c!"), uuid); return false; }

        CrisisAction action = c.actions[actionIdx - 1];
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(action.cost)) {
                ac.resolveProgress += action.resolvePercent;
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u6267\u884c: " + action.name + " | \u89e3\u51b3\u8fdb\u5ea6: " + Math.min(100, ac.resolveProgress) + "%"), uuid);
                if (ac.resolveProgress >= 100) {
                    activeCrises.remove(uuid);
                    player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5371\u673a\u5b8c\u5168\u89e3\u51b3!"), uuid);
                }
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"), uuid);
            return false;
        }).orElse(false);
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        ActiveCrisis ac = activeCrises.get(uuid);
        if (ac == null) {
            player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5f53\u524d\u65e0\u5371\u673a"), uuid);
        } else {
            Crisis c = CRISIS_POOL[ac.crisisIdx];
            player.sendMessage(new StringTextComponent("\u00a7c\u5371\u673a: " + c.name + " | \u89e3\u51b3: " + Math.min(100, ac.resolveProgress) + "% | \u5269\u4f59: " + (ac.remainingTicks/1200) + "\u5206\u949f"), uuid);
            player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /crisis solve <1-" + c.actions.length + "> \u5904\u7406"), uuid);
        }
    }
}
