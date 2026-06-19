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
public class RiskWarningSystem {
    private static final Map<UUID,Integer> warningLevel=new HashMap<>();
    private static final Map<UUID,Integer> monitoringLevel=new HashMap<>();
    private static int tickCounter=0;
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){
        if(event.phase!=TickEvent.Phase.END) return; tickCounter++;
        if(tickCounter%6000!=0||ServerLifecycleHooks.getCurrentServer()==null) return;
        for(ServerPlayerEntity player:ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()){
            UUID uuid=player.getUUID();
            int money=player.getCapability(MoneyCapability.MONEY_CAP).map(MoneyCapability.IMoney::getMoney).orElse(0);
            int monLevel=monitoringLevel.getOrDefault(uuid,0);
            int risk=0;
            if(money<500) risk+=40; else if(money<2000) risk+=20;
            if(EmployeeSystem.calculateTotalEmployees(uuid)>0 && money<1000) risk+=20;
            risk+=10;
            risk+=15;
            risk=Math.max(0,risk-monLevel*5);
            warningLevel.put(uuid,risk);
            if(risk>=60) player.sendMessage(new StringTextComponent("\u00a7c\u26a0 \u98ce\u9669\u9884\u8b66: \u9ad8\u98ce\u9669! \u98ce\u9669\u503c:"+risk+"/100|\u5efa\u8bae\u91c7\u53d6\u63aa\u65bd"),uuid);
            else if(risk>=30) player.sendMessage(new StringTextComponent("\u00a7e\u26a0 \u98ce\u9669\u9884\u8b66: \u4e2d\u7b49\u98ce\u9669 \u98ce\u9669\u503c:"+risk+"/100"),uuid);
        }
    }
    public static boolean upgrade(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); int cost=2000*(monitoringLevel.getOrDefault(uuid,0)+1);
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(cost)){monitoringLevel.merge(uuid,1,Integer::sum);player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u76d1\u63a7\u5347\u7ea7! Lv."+monitoringLevel.get(uuid)+"|\u98ce\u9669\u62b5\u5146-"+(monitoringLevel.get(uuid)*5)),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); int risk=warningLevel.getOrDefault(uuid,0); int monLvl=monitoringLevel.getOrDefault(uuid,0);
        String level=risk>=60?"\u00a7c\u9ad8\u98ce\u9669":risk>=30?"\u00a7e\u4e2d\u98ce\u9669":"\u00a7a\u4f4e\u98ce\u9669";
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7c\u26a0 \u98ce\u9669\u9884\u8b66 "+level+"  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u98ce\u9669\u503c: "+risk+"/100  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u76d1\u63a7\u7b49\u7ea7: Lv."+monLvl+"  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        player.sendMessage(new StringTextComponent("\u00a7e/risk upgrade \u5347\u7ea7\u76d1\u63a7 (\u8d39\u7528:"+2000*(monLvl+1)+")"),uuid);
    }
    public static int getRisk(UUID uuid){return warningLevel.getOrDefault(uuid,0);}
}
