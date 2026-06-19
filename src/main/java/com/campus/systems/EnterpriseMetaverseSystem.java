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
public class EnterpriseMetaverseSystem {
    public static class Asset{ public String name; public int cost; public int income; public int reqLevel;
        public Asset(String n,int c,int i,int r){name=n;cost=c;income=i;reqLevel=r;} }
    public static final Asset[] ASSETS={
        new Asset("\u865a\u62df\u529e\u516c\u5ba4",1000,30,2), new Asset("\u865a\u62df\u5c55\u5385",5000,120,5),
        new Asset("\u865a\u62df\u5546\u57ce",15000,400,8), new Asset("\u865a\u62df\u56ed\u533a",50000,1500,12),
        new Asset("\u5143\u5b87\u5b99\u603b\u90e8",200000,6000,20)
    };
    private static final Map<UUID,Set<Integer>> owned=new HashMap<>();
    private static int tickCounter=0;
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){
        if(event.phase!=TickEvent.Phase.END) return; tickCounter++;
        if(tickCounter%6000!=0||ServerLifecycleHooks.getCurrentServer()==null) return;
        for(ServerPlayerEntity player:ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()){
            UUID uuid=player.getUUID(); Set<Integer> has=owned.get(uuid); if(has==null||has.isEmpty()) continue;
            int total=0; for(int i:has) total+=ASSETS[i].income; final int income=total;
            player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m->m.addMoney(income));
            player.sendMessage(new StringTextComponent("\u00a7d\u2714 \u5143\u5b87\u5b99\u6536\u5165: +"+income+"\u91d1\u5e01 ("+has.size()+"\u9879\u8d44\u4ea7)"),uuid);
        }
    }
    public static boolean purchase(ServerPlayerEntity player,int idx){
        UUID uuid=player.getUUID();
        if(idx<1||idx>ASSETS.length){player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548!"),uuid);return false;}
        Set<Integer> has=owned.getOrDefault(uuid,new HashSet<>());
        if(has.contains(idx-1)){player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u6301\u6709!"),uuid);return false;}
        Asset a=ASSETS[idx-1]; int level=player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if(level<a.reqLevel){player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv."+a.reqLevel),uuid);return false;}
        final int cost=a.cost; final int income=a.income; final String aname=a.name;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(cost)){has.add(idx-1);owned.put(uuid,has);player.sendMessage(new StringTextComponent("\u00a7d\u2714 \u8d2d\u4e70: "+aname+"|\u6536\u5165:"+income+"/\u5468"),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); Set<Integer> has=owned.getOrDefault(uuid,new HashSet<>());
        int total=0; for(int i:has) total+=ASSETS[i].income;
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7d\u2604 \u4f01\u4e1a\u5143\u5b87\u5b99 ("+has.size()+"/"+ASSETS.length+"|\u6536\u5165:"+total+"/\u5468)  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        for(int i=0;i<ASSETS.length;i++){Asset a=ASSETS[i];boolean owned2=has.contains(i);player.sendMessage(new StringTextComponent("\u00a7e["+(i+1)+"] "+a.name+"|\u00a76 "+a.cost+"\u91d1\u5e01|\u00a7a "+a.income+"/\u5468|\u00a7b Lv."+a.reqLevel+"+|"+(owned2?"\u00a7a\u2714":"\u00a77\u2716")),uuid);}
        player.sendMessage(new StringTextComponent("\u00a7e/emetaverse <1-5> \u8d2d\u4e70"),uuid);
    }
}
