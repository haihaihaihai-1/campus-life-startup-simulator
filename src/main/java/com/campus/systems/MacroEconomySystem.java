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
public class MacroEconomySystem {
    private static int tickCounter=0;
    private static double gdpGrowth=0.05; private static double inflation=0.02; private static double interestRate=0.03;
    private static final Random RAND=new Random();
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){
        if(event.phase!=TickEvent.Phase.END) return; tickCounter++;
        if(tickCounter%12000==0){
            gdpGrowth=Math.max(-0.1,Math.min(0.15,gdpGrowth+(RAND.nextDouble()*0.04-0.02)));
            inflation=Math.max(0,Math.min(0.15,inflation+(RAND.nextDouble()*0.03-0.015)));
            interestRate=Math.max(0,Math.min(0.1,inflation+0.01));
            if(ServerLifecycleHooks.getCurrentServer()!=null) for(ServerPlayerEntity p:ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) p.sendMessage(new StringTextComponent("\u00a7b\u2500\u2500\u2500 \u5b8f\u89c2\u7ecf\u6d4e\u66f4\u65b0 \u2500\u2500\u2500"),p.getUUID());
        }
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID();
        String gdpStr=gdpGrowth>=0?"\u00a7a+"+(int)(gdpGrowth*100)+"%":"\u00a7c"+(int)(gdpGrowth*100)+"%";
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\ud83c\udfe6 \u5b8f\u89c2\u7ecf\u6d4e  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        player.sendMessage(new StringTextComponent("\u00a7eGDP\u589e\u957f\u7387: "+gdpStr),uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u901a\u8d27\u81a8\u80c0: \u00a7c"+(int)(inflation*100)+"%"),uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u5229\u7387: \u00a7b"+(int)(interestRate*100)+"%"),uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u7ecf\u6d4e\u72b6\u6001: "+(gdpGrowth>0.05?"\u00a7a\u7e41\u8363":gdpGrowth>0?"\u00a7e\u590d\u82cf":"\u00a7c\u8870\u9000")),uuid);
        player.sendMessage(new StringTextComponent("\u00a77\u5b8f\u89c2\u7ecf\u6d4e\u5f71\u54cd\u6240\u6709\u4ea4\u6613\u4e0e\u6536\u5165"),uuid);
    }
    public static double getGdpGrowth(){return gdpGrowth;}
    public static double getInflation(){return inflation;}
    public static double getInterestRate(){return interestRate;}
}
