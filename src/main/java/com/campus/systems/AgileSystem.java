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
public class AgileSystem {
    private static final Map<UUID,Sprint> activeSprint=new HashMap<>();
    private static int tickCounter=0;
    public static class Sprint{ public int storyPoints; public int completedPoints; public int remainingTicks; public int reward; public Sprint(int sp,int ticks,int r){storyPoints=sp;completedPoints=0;remainingTicks=ticks;reward=r;} }
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){
        if(event.phase!=TickEvent.Phase.END) return; tickCounter++;
        if(tickCounter%1200!=0||ServerLifecycleHooks.getCurrentServer()==null) return;
        for(ServerPlayerEntity player:ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()){
            UUID uuid=player.getUUID(); Sprint s=activeSprint.get(uuid); if(s==null) continue;
            s.remainingTicks-=1200; s.completedPoints=Math.min(s.storyPoints,s.completedPoints+s.storyPoints/4);
            if(s.remainingTicks<=0){
                int completion=(int)(s.completedPoints*100.0/s.storyPoints);
                int payout=s.reward*completion/100;
                player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m->m.addMoney(payout));
                player.getCapability(SkillCapability.SKILL_CAP).ifPresent(sk->sk.addExp(payout/10));
                player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u51b2\u523a\u5b8c\u6210! \u5b8c\u6210\u7387:"+completion+"%|\u5956\u52b1:"+payout+"\u91d1\u5e01"),uuid);
                activeSprint.remove(uuid);
            }
        }
    }
    public static boolean startSprint(ServerPlayerEntity player,int complexity){
        UUID uuid=player.getUUID();
        if(activeSprint.containsKey(uuid)){player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u6709\u8fdb\u884c\u4e2d\u7684\u51b2\u523a!"),uuid);return false;}
        int sp=complexity*5; int ticks=complexity*1200; int reward=complexity*500;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(complexity*200)){activeSprint.put(uuid,new Sprint(sp,ticks,reward));player.sendMessage(new StringTextComponent("\u00a7b\u2714 \u542f\u52a8\u51b2\u523a! \u6545\u4e8b\u70b9:"+sp+"|\u5468\u671f:"+(ticks/1200)+"\u5206\u949f|\u5956\u52b1:"+reward+"\u91d1\u5e01"),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); Sprint s=activeSprint.get(uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\u26a1 \u654f\u6377\u5f00\u53d1  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        if(s!=null){int progress=s.storyPoints>0?s.completedPoints*100/s.storyPoints:0;player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u51b2\u523a\u8fdb\u884c\u4e2d: "+progress+"%|"+s.completedPoints+"/"+s.storyPoints+"\u70b9|\u5269\u4f59:"+(s.remainingTicks/1200)+"\u5206\u949f"),uuid);}
        else{player.sendMessage(new StringTextComponent("\u00a7e/agile <1-5> \u542f\u52a8\u51b2\u523a (\u590d\u6742\u5ea61-5)"),uuid);}
    }
}
