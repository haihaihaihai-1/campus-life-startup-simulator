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

@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SmartLogisticsSystem {
    public static class Hub{ public String name; public int cost; public int capacity; public int income; public int reqLevel;
        public Hub(String n,int c,int cap,int i,int r){name=n;cost=c;capacity=cap;income=i;reqLevel=r;} }
    public static final Hub[] HUBS={
        new Hub("\u6821\u56ed\u914d\u9001\u70b9",500,50,15,1),
        new Hub("\u57ce\u5e02\u4ed3\u5e93",2000,200,60,3),
        new Hub("\u533a\u57df\u5206\u62e8\u4e2d\u5fc3",8000,1000,300,6),
        new Hub("\u5168\u56fd\u7269\u6d41\u7f51",30000,5000,1500,10),
        new Hub("\u5168\u7403\u4f9b\u5e94\u94fe",100000,30000,8000,18)
    };
    private static final Map<UUID,Set<Integer>> built=new HashMap<>();
    private static int tickCounter=0;
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){
        if(event.phase!=TickEvent.Phase.END) return; tickCounter++;
        if(tickCounter%6000!=0||ServerLifecycleHooks.getCurrentServer()==null) return;
        for(ServerPlayerEntity player:ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()){
            UUID uuid=player.getUUID(); Set<Integer> has=built.get(uuid); if(has==null||has.isEmpty()) continue;
            int total=0; for(int i:has) total+=HUBS[i].income; final int income=total;
            player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m->m.addMoney(income));
            player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u7269\u6d41\u6536\u5165: +"+income+"\u91d1\u5e01 ("+has.size()+"\u4e2a\u679a\u7ebd"),uuid);
        }
    }
    public static boolean build(ServerPlayerEntity player,int idx){
        UUID uuid=player.getUUID();
        if(idx<1||idx>HUBS.length){player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548!"),uuid);return false;}
        Set<Integer> has=built.getOrDefault(uuid,new HashSet<>());
        if(has.contains(idx-1)){player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u5efa\u8bbe!"),uuid);return false;}
        Hub h=HUBS[idx-1];
        int level=player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if(level<h.reqLevel){player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv."+h.reqLevel),uuid);return false;}
        final int cost=h.cost; final int cap=h.capacity; final int income=h.income; final String hname=h.name;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(cost)){has.add(idx-1);built.put(uuid,has);player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5efa\u8bbe: "+hname+"|\u5bb9\u91cf:"+cap+"|\u6536\u5165:"+income+"/\u5468"),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); Set<Integer> has=built.getOrDefault(uuid,new HashSet<>());
        int totalCap=0,totalIncome=0; for(int i:has){totalCap+=HUBS[i].capacity;totalIncome+=HUBS[i].income;}
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\ud83d\ude9a \u667a\u6167\u7269\u6d41 ("+has.size()+"/"+HUBS.length+"|\u5bb9\u91cf:"+totalCap+"|\u6536\u5165:"+totalIncome+"/\u5468)  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        for(int i=0;i<HUBS.length;i++){Hub h=HUBS[i];boolean owned=has.contains(i);player.sendMessage(new StringTextComponent("\u00a7e["+(i+1)+"] "+h.name+"|\u00a76 "+h.cost+"\u91d1\u5e01|\u00a7b \u5bb9:"+h.capacity+"|\u00a7a "+h.income+"/\u5468|\u00a7b Lv."+h.reqLevel+"+|"+(owned?"\u00a7a\u2714":"\u00a77\u2716")),uuid);}
        player.sendMessage(new StringTextComponent("\u00a7e/logistics <1-5> \u5efa\u8bbe"),uuid);
    }
}
