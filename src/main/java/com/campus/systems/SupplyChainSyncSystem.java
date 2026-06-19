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
public class SupplyChainSyncSystem {
    public static class Partner { public String name; public int cost; public int efficiency; public int reqLevel;
        public Partner(String n,int c,int e,int r){name=n;cost=c;efficiency=e;reqLevel=r;} }
    public static final Partner[] PARTNERS={
        new Partner("\u4e0a\u6e38\u539f\u6750\u6599\u5546",1000,8,2),
        new Partner("\u4e0b\u6e38\u5206\u9500\u5546",2000,12,4),
        new Partner("\u7269\u6d41\u4f9b\u5e94\u5546",3000,15,6),
        new Partner("\u6280\u672f\u670d\u52a1\u5546",5000,20,8),
        new Partner("\u5168\u7403\u4f9b\u5e94\u7f51",15000,35,15)
    };
    private static final Map<UUID,Set<Integer>> partnered=new HashMap<>();
    public static boolean partner(ServerPlayerEntity player,int idx){
        UUID uuid=player.getUUID();
        if(idx<1||idx>PARTNERS.length){player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548!"),uuid);return false;}
        Set<Integer> has=partnered.getOrDefault(uuid,new HashSet<>());
        if(has.contains(idx-1)){player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u5408\u4f5c!"),uuid);return false;}
        Partner p=PARTNERS[idx-1];
        int level=player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if(level<p.reqLevel){player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv."+p.reqLevel),uuid);return false;}
        final int cost=p.cost; final int eff=p.efficiency; final String pname=p.name;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(cost)){has.add(idx-1);partnered.put(uuid,has);player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5408\u4f5c:"+pname+"|\u6548\u7387+"+eff+"%"),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); Set<Integer> has=partnered.getOrDefault(uuid,new HashSet<>());
        int total=0; for(int i:has) total+=PARTNERS[i].efficiency;
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\ud83d\udd17 \u4f9b\u5e94\u94fe\u534f\u540c (\u6548\u7387+"+total+"%)  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        for(int i=0;i<PARTNERS.length;i++){Partner p=PARTNERS[i];boolean owned=has.contains(i);player.sendMessage(new StringTextComponent("\u00a7e["+(i+1)+"] "+p.name+"|\u00a76 "+p.cost+"\u91d1\u5e01|\u00a7a +"+p.efficiency+"%|\u00a7b Lv."+p.reqLevel+"+|"+(owned?"\u00a7a\u2714":"\u00a77\u2716")),uuid);}
        player.sendMessage(new StringTextComponent("\u00a7e/scsync <1-5> \u5408\u4f5c"),uuid);
    }
    public static int getEfficiency(UUID uuid){Set<Integer> has=partnered.getOrDefault(uuid,new HashSet<>());int t=0;for(int i:has)t+=PARTNERS[i].efficiency;return t;}
}
