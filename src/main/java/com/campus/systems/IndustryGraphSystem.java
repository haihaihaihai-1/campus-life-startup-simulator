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
public class IndustryGraphSystem {
    public static class Link{ public String name; public int cost; public int synergy; public int reqLevel;
        public Link(String n,int c,int s,int r){name=n;cost=c;synergy=s;reqLevel=r;} }
    public static final Link[] LINKS={
        new Link("\u4e0a\u6e38\u8054\u7cfb",1000,10,2), new Link("\u4e0b\u6e38\u8054\u7cfb",2000,15,3),
        new Link("\u6a2a\u5411\u534f\u4f5c",5000,25,5), new Link("\u4ea7\u4e1a\u96c6\u7fa4",15000,40,8),
        new Link("\u5168\u7403\u4ea7\u4e1a\u94fe",50000,70,15)
    };
    private static final Map<UUID,Set<Integer>> connected=new HashMap<>();
    public static boolean connect(ServerPlayerEntity player,int idx){
        UUID uuid=player.getUUID();
        if(idx<1||idx>LINKS.length){player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548!"),uuid);return false;}
        Set<Integer> has=connected.getOrDefault(uuid,new HashSet<>());
        if(has.contains(idx-1)){player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u8fde\u63a5!"),uuid);return false;}
        Link l=LINKS[idx-1]; int level=player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if(level<l.reqLevel){player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv."+l.reqLevel),uuid);return false;}
        final int cost=l.cost; final int syn=l.synergy; final String lname=l.name;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(cost)){has.add(idx-1);connected.put(uuid,has);int bonus=syn*15;player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(mm->mm.addMoney(bonus));player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u8fde\u63a5: "+lname+"|\u534f\u540c+"+syn+"%|\u5956\u52b1:"+bonus+"\u91d1\u5e01"),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); Set<Integer> has=connected.getOrDefault(uuid,new HashSet<>());
        int total=0; for(int i:has) total+=LINKS[i].synergy;
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\ud83d\udd17 \u4ea7\u4e1a\u94fe\u56fe\u8c31 (\u534f\u540c+"+total+"%)  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        for(int i=0;i<LINKS.length;i++){Link l=LINKS[i];boolean owned=has.contains(i);player.sendMessage(new StringTextComponent("\u00a7e["+(i+1)+"] "+l.name+"|\u00a76 "+l.cost+"\u91d1\u5e01|\u00a7a +"+l.synergy+"%|\u00a7b Lv."+l.reqLevel+"+|"+(owned?"\u00a7a\u2714":"\u00a77\u2716")),uuid);}
        player.sendMessage(new StringTextComponent("\u00a7e/igraph <1-5> \u8fde\u63a5"),uuid);
    }
    public static int getSynergy(UUID uuid){Set<Integer> has=connected.getOrDefault(uuid,new HashSet<>());int t=0;for(int i:has)t+=LINKS[i].synergy;return t;}
}
