package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * 营业执照系统 - 解锁经营范围
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LicenseSystem {

    public static class License {
        public String id, name, description;
        public int fee, requiredLevel;
        public String[] unlocks;

        public License(String id, String name, String desc, int fee, int reqLvl, String... unlocks) {
            this.id = id; this.name = name; this.description = desc; this.fee = fee; this.requiredLevel = reqLvl; this.unlocks = unlocks;
        }
    }

    public static final License[] LICENSES = {
        new License("retail", "\u96f6\u552e\u8bb8\u53ef\u8bc1", "\u89e3\u9501\u57fa\u7840\u96f6\u552e\u4e1a\u52a1", 500, 1, "\u5496\u5561\u5e97", "\u6587\u5177\u5e97"),
        new License("food_service", "\u9910\u996e\u670d\u52a1\u8bc1", "\u89e3\u9501\u9910\u996e\u4e1a\u52a1", 1500, 3, "\u5feb\u9910\u5e97", "\u98df\u5802"),
        new License("tech_service", "\u79d1\u6280\u670d\u52a1\u8bc1", "\u89e3\u9501\u79d1\u6280\u4e1a\u52a1", 3000, 5, "\u79d1\u6280\u5e97"),
        new License("manufacturing", "\u751f\u4ea7\u5236\u9020\u8bc1", "\u89e3\u9501\u751f\u4ea7\u4e1a\u52a1", 8000, 8, "\u751f\u4ea7\u7ebf"),
        new License("real_estate", "\u623f\u5730\u4ea7\u8bc1", "\u89e3\u9501\u623f\u5730\u4ea7\u4e1a\u52a1", 20000, 12, "\u5199\u5b57\u697c", "\u5546\u573a"),
        new License("finance", "\u91d1\u878d\u670d\u52a1\u8bc1", "\u89e3\u9501\u91d1\u878d\u4e1a\u52a1", 50000, 20, "\u80a1\u7968", "\u57fa\u91d1"),
        new License("international", "\u56fd\u9645\u8d38\u6613\u8bc1", "\u89e3\u9501\u8de8\u5883\u4e1a\u52a1", 100000, 30, "\u8de8\u533a\u57df", "\u56fd\u9645\u62cd\u5356")
    };

    private static final Map<UUID, Set<String>> obtained = new HashMap<>();

    public static void show(ServerPlayerEntity player) {
        UUID uuid = player.getUUID();
        Set<String> has = obtained.getOrDefault(uuid, new HashSet<>());
        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);

        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\ud83d\udcc4 \u8425\u4e1a\u6267\u7167 (" + has.size() + "/" + LICENSES.length + ")  \u00a76\u2551"), uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"), uuid);

        for (int i = 0; i < LICENSES.length; i++) {
            License l = LICENSES[i];
            boolean owned = has.contains(l.id);
            boolean canGet = !owned && level >= l.requiredLevel;
            String status = owned ? "\u00a7a\u2714\u5df2\u83b7\u53d6" : (canGet ? "\u00a7e\u53ef\u7533\u8bf7" : "\u00a7c\u672a\u8fbe\u6807");
            player.sendMessage(new StringTextComponent(
                    "\u00a7e[" + (i+1) + "] " + l.name +
                    " \u00a7f|\u00a76 " + l.fee + "\u91d1\u5e01" +
                    " \u00a7f|\u00a7b Lv." + l.requiredLevel + "+" +
                    " \u00a7f|\u00a77 \u89e3\u9501:" + String.join("/", l.unlocks) +
                    " \u00a7f| " + status), uuid);
        }
        player.sendMessage(new StringTextComponent("\u00a7e\u8f93\u5165 /license <1-7> \u7533\u8bf7\u6267\u7167"), uuid);
    }

    public static boolean apply(ServerPlayerEntity player, int idx) {
        UUID uuid = player.getUUID();
        if (idx < 1 || idx > LICENSES.length) { player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548\u7f16\u53f7!"), uuid); return false; }
        License l = LICENSES[idx - 1];
        Set<String> has = obtained.getOrDefault(uuid, new HashSet<>());
        if (has.contains(l.id)) { player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u6301\u6709!"), uuid); return false; }

        int level = player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if (level < l.requiredLevel) { player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv." + l.requiredLevel), uuid); return false; }

        return player.getCapability(MoneyCapability.MONEY_CAP).map(m -> {
            if (m.spendMoney(l.fee)) {
                has.add(l.id);
                obtained.put(uuid, has);
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u83b7\u53d6\u6267\u7167! " + l.name), uuid);
                player.sendMessage(new StringTextComponent("\u00a7e\u89e3\u9501\u7ecf\u8425\u8303\u56f4: " + String.join(", ", l.unlocks)), uuid);
                return true;
            }
            player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3! \u9700" + l.fee), uuid); return false;
        }).orElse(false);
    }

    public static boolean has(UUID uuid, String licenseId) {
        return obtained.getOrDefault(uuid, new HashSet<>()).contains(licenseId);
    }

    public static Set<String> getLicenses(UUID uuid) { return obtained.getOrDefault(uuid, new HashSet<>()); }
}
