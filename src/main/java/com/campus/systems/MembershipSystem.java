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

@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MembershipSystem {
    public enum VipLevel{ BRONZE("\u9752\u94dc\u4f1a\u5458",0,5,0),SILVER("\u767d\u94f6\u4f1a\u5458",1000,10,3),GOLD("\u9ec4\u91d1\u4f1a\u5458",5000,20,6),PLATINUM("\u94c2\u91d1\u4f1a\u5458",20000,35,10),DIAMOND("\u94bb\u77f3\u4f1a\u5458",100000,60,15);
        public String name; public int cost; public int incomeBoost; public int reqLevel;
        VipLevel(String n,int c,int b,int r){name=n;cost=c;incomeBoost=b;reqLevel=r;} }
    private static final Map<UUID,Integer> vipLevel=new HashMap<>();
    private static final Map<UUID,Integer> points=new HashMap<>();
    private static int tickCounter=0;
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){
        if(event.phase!=TickEvent.Phase.END) return; tickCounter++;
        if(tickCounter%6000!=0||ServerLifecycleHooks.getCurrentServer()==null) return;
        for(ServerPlayerEntity player:ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()){
            UUID uuid=player.getUUID(); int lvl=vipLevel.getOrDefault(uuid,0); if(lvl==0) continue;
            VipLevel[] levels=VipLevel.values(); if(lvl>levels.length) continue;
            int boost=levels[lvl-1].incomeBoost; int bonus=boost*10; final int b=bonus;
            player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m->m.addMoney(b));
            points.merge(uuid,boost,Integer::sum);
            player.sendMessage(new StringTextComponent("\u00a7a\u2714 VIP\u4f1a\u5458\u6536\u76ca: +"+b+"\u91d1\u5e01|\u79ef\u5206:"+points.get(uuid)),uuid);
        }
    }
    public static boolean upgrade(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); int current=vipLevel.getOrDefault(uuid,0);
        VipLevel[] levels=VipLevel.values(); if(current>=levels.length){player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u6701\u7ea7!"),uuid);return false;}
        VipLevel target=levels[current]; final int cost=target.cost; final String vname=target.name; final int newLevel=current+1;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(cost)){vipLevel.put(uuid,newLevel);player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5347\u7ea7\u6210\u4e3a: "+vname+"|\u6536\u5165+"+target.incomeBoost+"%"),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3! \u9700"+cost),uuid);return false;}).orElse(false);
    }
    public static boolean redeem(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); int pts=points.getOrDefault(uuid,0);
        if(pts<100){player.sendMessage(new StringTextComponent("\u00a7c\u79ef\u5206\u4e0d\u8db3! \u9700100+"),uuid);return false;}
        int reward=pts*2; points.put(uuid,0);
        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m->m.addMoney(reward));
        player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u79ef\u5206\u5151\u6362: "+pts+"\u79ef\u5206\u2192"+reward+"\u91d1\u5e01"),uuid);
        return true;
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); int lvl=vipLevel.getOrDefault(uuid,0); int pts=points.getOrDefault(uuid,0);
        String vname=lvl>0?VipLevel.values()[lvl-1].name:"\u975e\u4f1a\u5458";
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7d\u2728 \u4f1a\u5458\u4f53\u7cfb "+vname+"  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u79ef\u5206: "+pts+"  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        for(VipLevel v:VipLevel.values()) player.sendMessage(new StringTextComponent("\u00a7e  "+v.name+"|\u00a76 "+v.cost+"\u91d1\u5e01|\u00a7a +"+v.incomeBoost+"%|\u00a7b Lv."+v.reqLevel+"+"),uuid);
        player.sendMessage(new StringTextComponent("\u00a7e/vip upgrade | /vip redeem"),uuid);
    }
}
