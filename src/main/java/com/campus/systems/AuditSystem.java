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
 * 财务审计系统 - 定期审计+合规检查
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AuditSystem {

    private static int tickCounter = 0;
    private static final Map<UUID, AuditRecord> records = new HashMap<>();

    public static class AuditRecord {
        public int auditScore; public int lastAuditTime; public int totalAudits; public int totalFines;
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;
        if (tickCounter % 24000 == 0 && ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                runAudit(player);
            }
        }
    }

    public static void runAudit(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        AuditRecord rec = records.getOrDefault(uuid, new AuditRecord());
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        int money = player.getCapability(MoneyCapability.MONEY_CAP).map(MoneyCapability.IMoney::getMoney).orElse(0);
        int emp = EmployeeSystem.calculateTotalEmployees(uuid);
        int taxPaid = TaxSystem.getRecord(uuid).getTotalPaid();

        int score = 60 + level + Math.min(20, taxPaid/100) - Math.max(0, emp - level*2) + new Random().nextInt(20) - 10;
        score = Math.max(0, Math.min(100, score));
        rec.auditScore = score; rec.lastAuditTime = tickCounter; rec.totalAudits++;
        records.put(uuid, rec);

        if (score >= 80) {
            int bonus = score * 2;
            player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(bonus));
            player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5ba1\u8ba1\u901a\u8fc7! \u8bc4\u5206:" + score + " | \u5956\u52b1:" + bonus + "\u91d1\u5e01"), uuid);
        } else if (score < 40) {
            int fine = (40 - score) * 20;
            player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.spendMoney(Math.min(fine, m.getMoney())));
            rec.totalFines += fine;
            player.sendMessage(new StringTextComponent("\u00a7c\u2718 \u5ba1\u8ba1\u4e0d\u5408\u683c! \u7f5a\u6b3e:" + fine + "\u91d1\u5e01 | \u8bc4\u5206:" + score), uuid);
        } else {
            player.sendMessage(new StringTextComponent("\u00a7e\u5ba1\u8ba1\u57fa\u672c\u5408\u683c | \u8bc4\u5206:" + score), uuid);
        }
    }

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        AuditRecord rec = records.getOrDefault(uuid, new AuditRecord());
        player.sendMessage(new StringTextComponent("\u00a76\u2500\u2500\u2500 \u8d22\u52a1\u5ba1\u8ba1 \u2500\u2500\u2500"), player.getUUID());
        player.sendMessage(new StringTextComponent("\u00a7e\u5ba1\u8ba1\u8bc4\u5206: " + rec.auditScore + "/100"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u5ba1\u8ba1\u6b21\u6570: " + rec.totalAudits), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u7d2f\u8ba1\u7f5a\u6b3e: \u00a7c" + rec.totalFines), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u5ba1\u8ba1\u5468\u671f: \u00a7f\u6bcf20\u5206\u949f\u4e00\u6b21"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e/audit run \u624b\u52a8\u5ba1\u8ba1 (\u8d39\u7528100\u91d1\u5e01)"), uuid);
    }

    public static boolean manualAudit(ServerPlayerEntity player) {
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(100)) { runAudit(player); return true; }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"), player.getUUID()); return false;
        }).orElse(false);
    }

    public static int getScore(UUID uuid) { return records.getOrDefault(uuid, new AuditRecord()).auditScore; }
}
