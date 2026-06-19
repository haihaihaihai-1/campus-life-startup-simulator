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
 * 税收系统拼图 - 所得税+财产税+营业税
 * 参考: 真实税制模型
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TaxSystem {

    private static int tickCounter = 0;
    private static final Map<UUID, TaxRecord> taxRecords = new HashMap<>();

    // 税率配置
    private static final double INCOME_TAX_RATE = 0.10;      // 所得税10%
    private static final double PROPERTY_TAX_RATE = 0.02;    // 财产税2%
    private static final double BUSINESS_TAX_RATE = 0.05;    // 营业税5%
    private static final int TAX_FREE_THRESHOLD = 500;       // 免征额500

    public static class TaxRecord {
        public int totalIncomeTaxPaid;
        public int totalPropertyTaxPaid;
        public int totalBusinessTaxPaid;
        public int lastTaxAmount;
        public long lastTaxTime;

        public int getTotalPaid() {
            return totalIncomeTaxPaid + totalPropertyTaxPaid + totalBusinessTaxPaid;
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;

        // 每15分钟征收一次税(18000 ticks)
        if (tickCounter % 18000 == 0 && ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                collectTax(player);
            }
        }
    }

    public static void collectTax(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        TaxRecord record = taxRecords.getOrDefault(uuid, new TaxRecord());

        int money = player.getCapability(MoneyCapability.MONEY_CAP).map(MoneyCapability.IMoney::getMoney).orElse(0);
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        int employeeIncome = EmployeeSystem.calculateTotalIncome(uuid);

        // 计算各项税款
        int incomeTax = Math.max(0, (int)((money - TAX_FREE_THRESHOLD) * INCOME_TAX_RATE));
        int propertyTax = (int)(money * PROPERTY_TAX_RATE);
        int businessTax = (int)(employeeIncome * BUSINESS_TAX_RATE * 4); // 乘4因为15分钟≈3个5分钟周期
        int totalTax = incomeTax + propertyTax + businessTax;

        if (totalTax <= 0) {
            player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u672c\u671f\u514d\u7a0e! (\u8d44\u4ea7\u672a\u8d85\u514d\u5f81\u989d)"), uuid);
            return;
        }

        boolean success = player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            int actualTax = Math.min(totalTax, m.getMoney());
            m.spendMoney(actualTax);

            record.totalIncomeTaxPaid += incomeTax;
            record.totalPropertyTaxPaid += propertyTax;
            record.totalBusinessTaxPaid += businessTax;
            record.lastTaxAmount = actualTax;
            record.lastTaxTime = System.currentTimeMillis();
            taxRecords.put(uuid, record);

            player.sendMessage(new StringTextComponent("\u00a76\u2500\u2500\u2500 \u7a0e\u52a1\u5f81\u6536 \u2500\u2500\u2500"), uuid);
            player.sendMessage(new StringTextComponent("\u00a7e\u6240\u5f97\u7a0e(10%): \u00a7c" + incomeTax + " \u91d1\u5e01"), uuid);
            player.sendMessage(new StringTextComponent("\u00a7e\u8d22\u4ea7\u7a0e(2%): \u00a7c" + propertyTax + " \u91d1\u5e01"), uuid);
            player.sendMessage(new StringTextComponent("\u00a7e\u8425\u4e1a\u7a0e(5%): \u00a7c" + businessTax + " \u91d1\u5e01"), uuid);
            player.sendMessage(new StringTextComponent("\u00a7e\u672c\u671f\u5408\u8ba1\u7a0e\u6b3e: \u00a7c" + actualTax + " \u91d1\u5e01"), uuid);
            if (actualTax < totalTax) {
                player.sendMessage(new StringTextComponent("\u00a7c\u26a0 \u8d44\u91d1\u4e0d\u8db3\uff0c\u5c11\u5f81 " + (totalTax - actualTax) + " \u91d1\u5e01!"), uuid);
            }
            player.sendMessage(new StringTextComponent("\u00a77\u514d\u5f81\u989d: " + TAX_FREE_THRESHOLD + " | \u7d2f\u8ba1\u7eb3\u7a0e: " + record.getTotalPaid()), uuid);
            return true;
        }).orElse(false);
    }

    public static void showTaxInfo(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        TaxRecord record = taxRecords.getOrDefault(uuid, new TaxRecord());
        int money = player.getCapability(MoneyCapability.MONEY_CAP).map(MoneyCapability.IMoney::getMoney).orElse(0);
        int employeeIncome = EmployeeSystem.calculateTotalIncome(uuid);

        int estIncomeTax = Math.max(0, (int)((money - TAX_FREE_THRESHOLD) * INCOME_TAX_RATE));
        int estPropertyTax = (int)(money * PROPERTY_TAX_RATE);
        int estBusinessTax = (int)(employeeIncome * BUSINESS_TAX_RATE * 4);

        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u270d \u7a0e\u52a1\u4fe1\u606f  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u5f53\u524d\u8d44\u4ea7: \u00a76" + money + " \u91d1\u5e01"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u514d\u5f81\u989d: \u00a7a" + TAX_FREE_THRESHOLD + " \u91d1\u5e01"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u9884\u4f30\u6240\u5f97\u7a0e(10%): \u00a7c" + estIncomeTax), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u9884\u4f30\u8d22\u4ea7\u7a0e(2%): \u00a7c" + estPropertyTax), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u9884\u4f30\u8425\u4e1a\u7a0e(5%): \u00a7c" + estBusinessTax), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u9884\u4f30\u603b\u7a0e: \u00a7c" + (estIncomeTax + estPropertyTax + estBusinessTax)), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u7d2f\u8ba1\u7eb3\u7a0e: \u00a7c" + record.getTotalPaid() + " \u91d1\u5e01"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u7a0e\u52a1\u5468\u671f: \u00a7f\u6bcf15\u5206\u949f\u5f81\u6536\u4e00\u6b21"), uuid);
    }

    public static TaxRecord getRecord(UUID uuid) { return taxRecords.getOrDefault(uuid, new TaxRecord()); }
}
