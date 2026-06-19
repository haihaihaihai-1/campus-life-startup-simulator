package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CompetitorSystem {
    public static class Competitor{ public String name; public int marketShare; public int strength; public String strategy; public Competitor(String n,int ms,int s,String st){name=n;marketShare=ms;strength=s;strategy=st;} }
    public static final Competitor[] COMPETITORS={new Competitor("\u6821\u56ed\u4e91\u96c6\u56e2",25,60,"\u4ef7\u683c\u6218"),new Competitor("\u667a\u80fd\u79d1\u6280",20,70,"\u6280\u672f\u9886\u5148"),new Competitor("\u5feb\u521b\u8054\u76df",15,50,"\u5dee\u5f02\u5316"),new Competitor("\u521b\u65b0\u5de5\u574a",10,40,"\u5229\u57fa\u5e02\u573a"),new Competitor("\u72ec\u89d2\u517d\u516c\u53f8",5,80,"\u9ad8\u7aef\u5e02\u573a")};
    private static final Map<UUID,Integer> intelLevel=new HashMap<>();
    public static boolean gatherIntel(ServerPlayerEntity player,int idx){
        UUID uuid=player.getUUID(); if(idx<1||idx>COMPETITORS.length) return false;
        Competitor c=COMPETITORS[idx-1]; int cost=500*(intelLevel.getOrDefault(uuid,0)+1);
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(cost)){intelLevel.merge(uuid,1,Integer::sum);player.sendMessage(new StringTextComponent("\u00a7b\u2714 \u60c5\u62a5:"+c.name+"|\u5e02\u5360:"+c.marketShare+"%|\u5b9e\u529b:"+c.strength+"|\u7b56\u7565:"+c.strategy),uuid);int counter=5+intelLevel.get(uuid);ReputationSystem.addReputation(uuid,counter);player.sendMessage(new StringTextComponent("\u00a7e\u5bf9\u7b56\u52a0\u6210+\u58f0\u8a89"+counter),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3! \u9700"+cost),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); int intel=intelLevel.getOrDefault(uuid,0);
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7c\u2694 \u7ade\u4e89\u5bf9\u624b\u5206\u6790 (\u60c5\u62a5:"+intel+")  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        for(int i=0;i<COMPETITORS.length;i++){Competitor c=COMPETITORS[i];player.sendMessage(new StringTextComponent("\u00a7e["+(i+1)+"] "+c.name+"|\u00a7a \u5e02\u5360:"+c.marketShare+"%|\u00a7c \u5b9e\u529b:"+c.strength+"|\u00a7b "+c.strategy),uuid);}
        player.sendMessage(new StringTextComponent("\u00a7e/competitor <1-5> \u6536\u96c6\u60c5\u62a5"),uuid);
    }
}
