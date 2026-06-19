package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.*;

/**
 * IPO上市系统 - 发行股票+公众持股+股价波动
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class IPOSystem {

    private static final Map<UUID, IPOStatus> ipoStatus = new HashMap<>();
    private static final Map<UUID, Integer> sharePrice = new HashMap<>();
    private static int tickCounter = 0;
    private static final Random RAND = new Random();

    public static class IPOStatus {
        public boolean listed; public int totalShares; public int publicShares;
        public int raisedCapital; public long listTime;

        public IPOStatus(int shares, int capital) {
            this.listed = true; this.totalShares = shares; this.publicShares = shares / 3;
            this.raisedCapital = capital; this.listTime = System.currentTimeMillis();
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;
        if (tickCounter % 2400 == 0 && ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                UUID uuid = player.getUUID();
                if (!ipoStatus.containsKey(uuid)) continue;
                int price = sharePrice.getOrDefault(uuid, 100);
                int change = (int)(price * (RAND.nextDouble() * 0.2 - 0.1));
                sharePrice.put(uuid, Math.max(10, price + change));
                IPOStatus status = ipoStatus.get(uuid);
                int dividend = status.publicShares * sharePrice.get(uuid) / 100;
                player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(dividend));
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u80a1\u606f\u6536\u5165: \u00a76" + dividend + "\u91d1\u5e01 (\u80a1\u4ef7:" + sharePrice.get(uuid) + ")"), uuid);
            }
        }
    }

    public static boolean goIPO(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        if (ipoStatus.containsKey(uuid)) { player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u4e0a\u5e02!"), uuid); return false; }
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if (level < 40) { player.sendMessage(new StringTextComponent("\u00a7c\u4e0a\u5e02\u9700Lv.40+"), uuid); return false; }
        int vcRound = VCSystem.getRound(uuid);
        if (vcRound < 5) { player.sendMessage(new StringTextComponent("\u00a7c\u9700\u5b8c\u6210C\u8f6e\u878d\u8d44\u624d\u80fd\u4e0a\u5e02!"), uuid); return false; }

        int valuation = VCSystem.getValuation(uuid);
        int shares = valuation / 100;
        int capital = valuation / 4;
        ipoStatus.put(uuid, new IPOStatus(shares, capital));
        sharePrice.put(uuid, 100);
        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(capital));
        player.getCapability(SkillCapability.SKILL_CAP).ifPresent(s -> s.addExp(5000));

        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7a\ud83c\udfe5 IPO\u4e0a\u5e02\u6210\u529f!  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u603b\u80a1\u672c: \u00a7f" + shares + "\u80a1  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u52df\u96c6\u8d44\u91d1: \u00a76" + capital + "\u91d1\u5e01  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u521d\u59cb\u80a1\u4ef7: 100\u91d1\u5e01/\u80a1  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u6bcf\u5468\u83b7\u5f97\u80a1\u606f\u6536\u5165  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);
        return true;
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        if (!ipoStatus.containsKey(uuid)) {
            player.sendMessage(new StringTextComponent("\u00a7e\u672a\u4e0a\u5e02 | \u9700\u8981: Lv.40+ | C\u8f6e\u878d\u8d44\u5b8c\u6210 | \u8f93\u5165 /ipo go \u4e0a\u5e02"), uuid);
            return;
        }
        IPOStatus status = ipoStatus.get(uuid);
        int price = sharePrice.getOrDefault(uuid, 100);
        int marketCap = status.totalShares * price;
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7a\ud83c\udfe5 \u4e0a\u5e02\u4fe1\u606f  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u80a1\u4ef7: \u00a76" + price + "\u91d1\u5e01/\u80a1  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u603b\u80a1\u672c: \u00a7f" + status.totalShares + "\u80a1  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u6d41\u901a\u80a1: \u00a7b" + status.publicShares + "\u80a1  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u5e02\u503c: \u00a76" + marketCap + "\u91d1\u5e01  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);
    }

    public static boolean isListed(UUID uuid) { return ipoStatus.containsKey(uuid); }
    public static int getMarketCap(UUID uuid) {
        IPOStatus s = ipoStatus.get(uuid);
        return s != null ? s.totalShares * sharePrice.getOrDefault(uuid, 100) : 0;
    }
}
