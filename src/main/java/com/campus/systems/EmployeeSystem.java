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
 * 雇佣员工系统拼图 - 雇佣NPC产生被动收入
 * 参考: 经营类游戏的员工管理
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EmployeeSystem {

    public static class Employee {
        public String type;
        public String name;
        public int hireCost;
        public int dailyIncome;
        public String description;

        public Employee(String type, String name, int hireCost, int dailyIncome, String desc) {
            this.type = type;
            this.name = name;
            this.hireCost = hireCost;
            this.dailyIncome = dailyIncome;
            this.description = desc;
        }
    }

    public static final Employee[] EMPLOYEE_TYPES = {
        new Employee("intern", "\u5b9e\u4e60\u751f", 300, 20, "\u57fa\u7840\u6536\u5165"),
        new Employee("salesman", "\u9500\u552e\u5458", 800, 60, "\u63d0\u5347\u9500\u552e"),
        new Employee("engineer", "\u5de5\u7a0b\u5e08", 2000, 150, "\u7814\u53d1\u52a0\u6210"),
        new Employee("manager", "\u7ecf\u7406", 5000, 400, "\u7ba1\u7406\u6548\u7387"),
        new Employee("cto", "CTO", 15000, 1200, "\u6280\u672f\u9886\u8896"),
        new Employee("ceo", "CEO", 50000, 5000, "\u4f01\u4e1a\u9886\u8896")
    };

    private static final Map<UUID, Map<String, Integer>> employees = new HashMap<>();
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;

        // 每5分钟发放员工收入
        if (tickCounter % 6000 == 0 && ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                UUID uuid = player.getUUID();
                Map<String, Integer> emp = employees.get(uuid);
                if (emp == null || emp.isEmpty()) continue;

                int totalIncome = 0;
                for (Map.Entry<String, Integer> entry : emp.entrySet()) {
                    for (Employee e : EMPLOYEE_TYPES) {
                        if (e.type.equals(entry.getKey())) {
                            totalIncome += e.dailyIncome * entry.getValue();
                            break;
                        }
                    }
                }

                final int finalIncome = totalIncome;
                if (finalIncome > 0) {
                    player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m -> m.addMoney(finalIncome));
                    player.sendMessage(new StringTextComponent(
                            "\u00a7a\u2714 \u5458\u5de5\u6536\u5165: \u00a76" + finalIncome + " \u91d1\u5e01"), uuid);
                }
            }
        }
    }

    public static void showEmployees(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        Map<String, Integer> emp = employees.getOrDefault(uuid, new HashMap<>());

        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u26d3 \u5458\u5de5\u7ba1\u7406  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        for (Employee e : EMPLOYEE_TYPES) {
            int count = emp.getOrDefault(e.type, 0);
            player.sendMessage(new StringTextComponent(
                    "\u00a7e" + e.name +
                    " \u00a7f|\u00a76 \u96c7\u4f63:" + e.hireCost +
                    " \u00a7f|\u00a7a \u6536\u5165:" + e.dailyIncome + "/\u5468" +
                    " \u00a7f|\u00a7b \u5df2\u96c7:" + count +
                    " \u00a7f|\u00a77 " + e.description), uuid);
        }

        int totalIncome = calculateTotalIncome(uuid);
        int totalEmployees = calculateTotalEmployees(uuid);
        player.sendMessage(new StringTextComponent("\u00a7a\u603b\u5458\u5de5: " + totalEmployees + " \u4eba | \u00a76\u603b\u6536\u5165: " + totalIncome + " \u91d1\u5e01/\u5468"), uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /hire <\u7c7b\u578b> \u96c7\u4f63\u5458\u5de5"), uuid);
    }

    public static boolean hire(ServerPlayerEntity player, String type) {
        UUID uuid = player.getUUID();
        Employee target = null;
        for (Employee e : EMPLOYEE_TYPES) {
            if (e.type.equalsIgnoreCase(type) || e.name.equals(type)) {
                target = e;
                break;
            }
        }

        final Employee foundTarget = target;
        if (foundTarget == null) {
            player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u5458\u5de5\u7c7b\u578b!"), uuid);
            return false;
        }

        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(foundTarget.hireCost)) {
                Map<String, Integer> emp = employees.getOrDefault(uuid, new HashMap<>());
                emp.put(foundTarget.type, emp.getOrDefault(foundTarget.type, 0) + 1);
                employees.put(uuid, emp);
                player.sendMessage(new StringTextComponent(
                        "\u00a7a\u2714 \u96c7\u4f63\u6210\u529f! " + foundTarget.name + " x1"), uuid);
                player.sendMessage(new StringTextComponent(
                        "\u00a7e\u6d88\u8017 " + foundTarget.hireCost + " \u91d1\u5e01, \u6bcf\u5468\u6536\u5165 +" + foundTarget.dailyIncome), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3! \u9700\u8981 " + foundTarget.hireCost + " \u91d1\u5e01"), uuid);
            return false;
        }).orElse(false);
    }

    public static int calculateTotalIncome(UUID uuid) {
        Map<String, Integer> emp = employees.getOrDefault(uuid, new HashMap<>());
        int total = 0;
        for (Map.Entry<String, Integer> entry : emp.entrySet()) {
            for (Employee e : EMPLOYEE_TYPES) {
                if (e.type.equals(entry.getKey())) {
                    total += e.dailyIncome * entry.getValue();
                    break;
                }
            }
        }
        return total;
    }

    public static int calculateTotalEmployees(UUID uuid) {
        Map<String, Integer> emp = employees.getOrDefault(uuid, new HashMap<>());
        return emp.values().stream().mapToInt(Integer::intValue).sum();
    }
}
