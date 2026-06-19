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
 * 随机事件系统拼图 - 经济危机/繁荣/政策变动
 * 参考: 模拟经营随机事件机制
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RandomEventSystem {

    private static int tickCounter = 0;
    private static int nextEventIn = 6000; // 5分钟后首个事件
    private static final Random RAND = new Random();

    public static class GameEvent {
        public String name;
        public String description;
        public EventType type;
        public int magnitude; // 影响程度 1-100

        public GameEvent(String name, String desc, EventType type, int magnitude) {
            this.name = name;
            this.description = desc;
            this.type = type;
            this.magnitude = magnitude;
        }
    }

    public enum EventType {
        BOOM,       // 经济繁荣
        CRASH,      // 经济危机
        POLICY,     // 政策利好
        REGULATION, // 监管收紧
        DISCOVERY,  // 新发现
        PANDEMIC    // 疫情冲击
    }

    public static final GameEvent[] EVENT_POOL = {
        new GameEvent("\u7ecf\u6d4e\u7e41\u8363", "\u5e02\u573a\u9700\u6c42\u6fc0\u589e\uff0c\u6240\u6709\u4ea7\u54c1\u552e\u4ef7\u4e0a\u6da8!", EventType.BOOM, 30),
        new GameEvent("\u7ecf\u6d4e\u5371\u673a", "\u5e02\u573a\u8427\u6761\uff0c\u4ea7\u54c1\u4ef7\u683c\u66b4\u8dcc!", EventType.CRASH, 40),
        new GameEvent("\u521b\u4e1a\u6276\u6301\u653f\u7b56", "\u653f\u5e9c\u51fa\u53f0\u6263\u6301\uff0c\u6bcf\u4eba\u83b7\u53d1\u521b\u4e1a\u8865\u8d34!", EventType.POLICY, 500),
        new GameEvent("\u73af\u4fdd\u76d1\u7ba1", "\u65b0\u73af\u4fdd\u6cd5\u89c4\u51fa\u53f0\uff0c\u751f\u4ea7\u6210\u672c\u589e\u52a0!", EventType.REGULATION, 200),
        new GameEvent("\u6280\u672f\u7a81\u7834", "\u91cd\u5927\u6280\u672f\u7a81\u7834\uff0c\u79d1\u6280\u4ea7\u54c1\u4ef7\u503c\u98d9\u5347!", EventType.DISCOVERY, 50),
        new GameEvent("\u539f\u6750\u6599\u77ed\u7f3a", "\u539f\u6750\u6599\u4f9b\u5e94\u7d27\u5f20\uff0c\u4ef7\u683c\u7ffb\u500d!", EventType.CRASH, 60),
        new GameEvent("\u6821\u56ed\u62d3\u5ba2", "\u6821\u56ed\u6d3b\u52a8\u5e26\u6765\u5927\u91cf\u5ba2\u6d41!", EventType.BOOM, 25),
        new GameEvent("\u521b\u6295\u57fa\u91d1\u5165\u573a", "\u521b\u6295\u57fa\u91d1\u7eff\u706f\u653e\u884c\uff0c\u878d\u8d44\u73af\u5883\u5bbd\u677e!", EventType.POLICY, 300),
        new GameEvent("\u7ade\u4e89\u5bf9\u624b\u5165\u573a", "\u5f3a\u52bf\u7ade\u4e89\u5bf9\u624b\u51fa\u73b0\uff0c\u5e02\u573a\u4efd\u989d\u53d7\u538b!", EventType.REGULATION, 150),
        new GameEvent("\u7f51\u7ea2\u4ea7\u54c1", "\u67d0\u4ea7\u54c1\u7a81\u7136\u8d70\u7ea2\uff0c\u9700\u6c42\u66b4\u589e!", EventType.DISCOVERY, 45)
    };

    private static GameEvent currentEvent = null;
    private static int eventDuration = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;

        if (currentEvent != null) {
            eventDuration--;
            if (eventDuration <= 0) {
                endEvent();
            }
        } else {
            nextEventIn--;
            if (nextEventIn <= 0) {
                triggerRandomEvent();
            }
        }
    }

    private static void triggerRandomEvent() {
        GameEvent evt = EVENT_POOL[RAND.nextInt(EVENT_POOL.length)];
        currentEvent = evt;
        eventDuration = 6000 + RAND.nextInt(6000); // 持续5-10分钟

        if (ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                String typeColor;
                switch (evt.type) {
                    case BOOM: case DISCOVERY: typeColor = "\u00a7a"; break;
                    case CRASH: case PANDEMIC: typeColor = "\u00a7c"; break;
                    case POLICY: typeColor = "\u00a7b"; break;
                    case REGULATION: typeColor = "\u00a76"; break;
                    default: typeColor = "\u00a7e";
                }

                player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), player.getUUID());
                player.sendMessage(new StringTextComponent("\u00a76\u2551  " + typeColor + "\u26a0 \u7a81\u53d1\u4e8b\u4ef6: " + evt.name + "  \u00a76\u2551"), player.getUUID());
                player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7f" + evt.description + "  \u00a76\u2551"), player.getUUID());
                player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u5f71\u54cd\u7a0b\u5ea6: " + evt.magnitude + " | \u00a7e\u6301\u7eed: " + (eventDuration/1200) + "\u5206\u949f  \u00a76\u2551"), player.getUUID());

                // 直接影响
                switch (evt.type) {
                    case POLICY:
                        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(evt.magnitude));
                        player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u83b7\u5f97\u653f\u5e9c\u8865\u8d34 " + evt.magnitude + " \u91d1\u5e01!"), player.getUUID());
                        break;
                    case REGULATION:
                        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.spendMoney(Math.min(evt.magnitude, m.getMoney())));
                        player.sendMessage(new StringTextComponent("\u00a7c\u2718 \u76d1\u7ba1\u6210\u672c " + evt.magnitude + " \u91d1\u5e01!"), player.getUUID());
                        break;
                    case CRASH:
                        int loss = (int)(player.getCapability(MoneyCapability.MONEY_CAP).map(MoneyCapability.IMoney::getMoney).orElse(0) * (evt.magnitude / 1000.0));
                        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.spendMoney(Math.min(loss, m.getMoney())));
                        player.sendMessage(new StringTextComponent("\u00a7c\u2718 \u5371\u673a\u635f\u5931 " + loss + " \u91d1\u5e01!"), player.getUUID());
                        break;
                    case BOOM:
                        int bonus = evt.magnitude * 10;
                        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(bonus));
                        player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u7e41\u8363\u7ea2\u5229 " + bonus + " \u91d1\u5e01!"), player.getUUID());
                        break;
                    case DISCOVERY:
                        int techBonus = evt.magnitude * 5;
                        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(techBonus));
                        player.sendMessage(new StringTextComponent("\u00a7b\u2714 \u6280\u672f\u7ea2\u5229 " + techBonus + " \u91d1\u5e01!"), player.getUUID());
                        break;
                }
                player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), player.getUUID());
            }
        }
        CampusLife.LOGGER.info("Random event triggered: " + evt.name);
    }

    private static void endEvent() {
        if (currentEvent != null && ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                player.sendMessage(new StringTextComponent("\u00a77\u4e8b\u4ef6\u300c" + currentEvent.name + "\u300d\u5df2\u7ed3\u675f"), player.getUUID());
            }
        }
        currentEvent = null;
        eventDuration = 0;
        nextEventIn = 6000 + new Random().nextInt(12000); // 5-15分钟后下一个
    }

    public static void showEventStatus(ServerPlayerEntity player) {
        if (currentEvent != null) {
            player.sendMessage(new StringTextComponent(
                    "\u00a7e\u5f53\u524d\u4e8b\u4ef6: \u00a76" + currentEvent.name +
                    " \u00a7f|\u00a7e \u5269\u4f59: " + (eventDuration/1200) + "\u5206\u949f"), player.getUUID());
        } else {
            int mins = nextEventIn / 1200;
            player.sendMessage(new StringTextComponent(
                    "\u00a7e\u4e0b\u4e00\u4e2a\u4e8b\u4ef6: \u00a76" + mins + " \u5206\u949f\u540e"), player.getUUID());
        }
    }

    public static GameEvent getCurrentEvent() { return currentEvent; }
}
