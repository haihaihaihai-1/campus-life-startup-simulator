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
 * 贷款系统拼图 - 银行贷款+利息计算
 * 参考: 金融系统利息计算模式
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LoanSystem {

    private static int tickCounter = 0;
    private static final Map<UUID, Loan> loans = new HashMap<>();

    public static class Loan {
        public UUID playerId;
        public int principal;      // 本金
        public int interestPaid;   // 已付利息
        public int totalOwed;      // 总欠款
        public int tickCreated;    // 创建tick
        public boolean active;

        public Loan(UUID playerId, int amount) {
            this.playerId = playerId;
            this.principal = amount;
            this.totalOwed = amount;
            this.interestPaid = 0;
            this.tickCreated = 0;
            this.active = true;
        }
    }

    private static final double INTEREST_RATE = 0.05; // 每次结算5%利息
    private static final int MAX_LOAN = 5000;

    public static boolean takeLoan(ServerPlayerEntity player, int amount) {
        UUID uuid = player.getUUID();

        if (loans.containsKey(uuid) && loans.get(uuid).active) {
            player.sendMessage(new StringTextComponent("\u00a7c\u4f60\u6709\u672a\u8fd8\u6e05\u7684\u8d37\u6b3e!"), uuid);
            return false;
        }

        if (amount > MAX_LOAN) {
            player.sendMessage(new StringTextComponent(
                    "\u00a7c\u6700\u9ad8\u8d37\u6b3e\u989d\u5ea6: " + MAX_LOAN + " \u91d1\u5e01!"), uuid);
            return false;
        }

        Loan loan = new Loan(uuid, amount);
        loan.tickCreated = tickCounter;
        loans.put(uuid, loan);

        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(amount));
        player.sendMessage(new StringTextComponent(
                "\u00a7a\u2714 \u8d37\u6b3e\u6210\u529f! \u83b7\u5f97 " + amount + " \u91d1\u5e01"), uuid);
        player.sendMessage(new StringTextComponent(
                "\u00a7e\u5229\u7387: \u00a7c5%/\u7ed3\u7b97\u5468 | \u00a7e\u5f85\u8fd8: \u00a7c" + amount + " \u91d1\u5e01"), uuid);
        return true;
    }

    public static boolean repayLoan(ServerPlayerEntity player, int amount) {
        UUID uuid = player.getUUID();
        Loan loan = loans.get(uuid);
        if (loan == null || !loan.active) {
            player.sendMessage(new StringTextComponent("\u00a7c\u4f60\u6ca1\u6709\u6d3b\u8dc3\u7684\u8d37\u6b3e!"), uuid);
            return false;
        }

        boolean success = player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            int actualPay = Math.min(amount, loan.totalOwed);
            if (m.spendMoney(actualPay)) {
                loan.totalOwed -= actualPay;
                if (loan.totalOwed <= 0) {
                    loan.active = false;
                    player.sendMessage(new StringTextComponent(
                            "\u00a7a\u2714 \u8d37\u6b3e\u5df2\u5168\u90e8\u8fd8\u6e05! \u606d\u559c!"), uuid);
                } else {
                    player.sendMessage(new StringTextComponent(
                            "\u00a7a\u5df2\u8fd8 " + actualPay + " \u91d1\u5e01, \u5269\u4f59\u6b20\u6b3e: " + loan.totalOwed), uuid);
                }
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u4f59\u989d\u4e0d\u8db3!"), uuid);
            return false;
        }).orElse(false);
        return success;
    }

    public static void showLoanInfo(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        Loan loan = loans.get(uuid);
        if (loan == null || !loan.active) {
            player.sendMessage(new StringTextComponent("\u00a7e\u4f60\u6ca1\u6709\u6d3b\u8dc3\u7684\u8d37\u6b3e"), uuid);
            return;
        }
        player.sendMessage(new StringTextComponent("\u00a76\u2500\u2500\u2500 \u8d37\u6b3e\u4fe1\u606f \u2500\u2500\u2500"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u672c\u91d1: \u00a7f" + loan.principal + " \u91d1\u5e01"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u5f85\u8fd8: \u00a7c" + loan.totalOwed + " \u91d1\u5e01"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u5229\u7387: \u00a7c5%/\u7ed3\u7b97\u5468"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /loan repay <\u91d1\u989d> \u8fd8\u6b3e"), uuid);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;

        // 每10分钟结算一次利息
        if (tickCounter % 12000 == 0) {
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                UUID uuid = player.getUUID();
                Loan loan = loans.get(uuid);
                if (loan != null && loan.active) {
                    int interest = (int)(loan.totalOwed * INTEREST_RATE);
                    if (interest > 0) {
                        loan.totalOwed += interest;
                        loan.interestPaid += interest;
                        player.sendMessage(new StringTextComponent(
                                "\u00a7c\u8d37\u6b3e\u5229\u606f\u7ed3\u7b97! \u65b0\u589e\u5229\u606f: " + interest + " \u91d1\u5e01"), uuid);
                        player.sendMessage(new StringTextComponent(
                                "\u00a7e\u5f53\u524d\u6b20\u6b3e: \u00a7c" + loan.totalOwed + " \u91d1\u5e01"), uuid);
                    }
                }
            }
        }
    }
}
