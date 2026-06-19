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
public class SmartSchedulingSystem {
    private static final Map<UUID,Integer> scheduleLevel=new HashMap<>();
    private static final Map<UUID,ActiveSchedule> active=new HashMap<>();
    private static int tickCounter=0;
    public static class ActiveSchedule{ public int batches; public int remainingTicks; public int reward; public ActiveSchedule(int b,int t,int r){batches=b;remainingTicks=t;reward=r;} }
    public static final int[] COSTS={500,1500,4000,10000,30000};
    public static final int[] BOOSTS={8,15,25,40,60};
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){
        if(event.phase!=TickEvent.Phase.END) return; tickCounter++;
        if(tickCounter%1200!=0||ServerLifecycleHooks.getCurrentServer()==null) return;
        for(ServerPlayerEntity player:ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()){
            UUID uuid=player.getUUID(); ActiveSchedule s=active.get(uuid); if(s==null) continue;
            s.remainingTicks-=1200;
            if(s.remainingTicks<=0){
                final int reward=s.reward; final int batches=s.batches;
                player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m->m.addMoney(reward));
                player.getCapability(SkillCapability.SKILL_CAP).ifPresent(sk->sk.addExp(reward/10));
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u6392\u4ea7\u5b8c\u6210! \u4ea7\u91fa:"+batches+"\u6279|\u6536\u76ca:"+reward+"\u91d1\u5e01"),uuid);
                active.remove(uuid);
            }
        }
    }
    public static boolean start(ServerPlayerEntity player,int complexity){
        UUID uuid=player.getUUID();
        if(active.containsKey(uuid)){player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u6709\u6392\u4ea7\u4e2d!"),uuid);return false;}
        if(complexity<1||complexity>5){player.sendMessage(new StringTextComponent("\u00a7c\u590d\u6742\u5ea61-5!"),uuid);return false;}
        int cost=COSTS[complexity-1]; int ticks=complexity*1200; int reward=complexity*800; final int r=reward; final int b=complexity*3; final int t=ticks;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(cost)){active.put(uuid,new ActiveSchedule(b,t,r));player.sendMessage(new StringTextComponent("\u00a7b\u2714 \u542f\u52a8\u6392\u4ea7! \u4ea7\u91fa:"+b+"\u6279|\u5468\u671f:"+(t/1200)+"\u5206\u949f|\u9884\u8ba1\u6536\u76ca:"+r+"\u91d1\u5e01"),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3! \u9700"+cost),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); ActiveSchedule s=active.get(uuid); int level=scheduleLevel.getOrDefault(uuid,0);
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\u2699 \u667a\u80fd\u6392\u4ea7  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        if(s!=null){player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u6392\u4ea7\u4e2d: "+s.batches+"\u6279|\u5269\u4f59:"+(s.remainingTicks/1200)+"\u5206\u949f"),uuid);}
        else{player.sendMessage(new StringTextComponent("\u00a7e/schedule <1-5> \u542f\u52a8\u6392\u4ea7"),uuid);for(int i=0;i<5;i++) player.sendMessage(new StringTextComponent("\u00a7e  ["+(i+1)+"] \u590d\u6742\u5ea6"+(i+1)+"|\u00a76 "+COSTS[i]+"\u91d1\u5e01|\u00a7a \u6548\u7387+"+BOOSTS[i]+"%"),uuid);}
    }
}
