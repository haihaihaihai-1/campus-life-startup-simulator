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
public class FoundationSystem {
    private static final Map<UUID,Integer> fundBalance=new HashMap<>();
    private static final Map<UUID,Integer> impactScore=new HashMap<>();
    private static int tickCounter=0;
    public static boolean create(ServerPlayerEntity player,int initialFund){
        UUID uuid=player.getUUID();
        if(fundBalance.containsKey(uuid)){player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u6709\u57fa\u91d1\u4f1a!"),uuid);return false;}
        if(initialFund<5000){player.sendMessage(new StringTextComponent("\u00a7c\u6700\u4f4e5000\u91d1\u5e01!"),uuid);return false;}
        final int fund=initialFund;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(fund)){fundBalance.put(uuid,fund);impactScore.put(uuid,0);player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u6210\u7acb\u4f01\u4e1a\u57fa\u91d1\u4f1a! \u521d\u59cb\u8d44\u91d1:"+fund),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){
        if(event.phase!=TickEvent.Phase.END) return; tickCounter++;
        if(tickCounter%12000!=0||ServerLifecycleHooks.getCurrentServer()==null) return;
        for(ServerPlayerEntity player:ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()){
            UUID uuid=player.getUUID(); int balance=fundBalance.getOrDefault(uuid,0); if(balance<=0) continue;
            int impact=balance/100; impactScore.merge(uuid,impact,Integer::sum);
            int repGain=impact/2; ReputationSystem.addReputation(uuid,repGain);
            ESGSystem.invest(player,2,impact/5);
            fundBalance.put(uuid,balance+impact*2);
            player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u57fa\u91d1\u4f1a\u8fd0\u8425: \u5f71\u54cd\u529b+"+impact+"|\u57fa\u91d1\u4f59\u989d:"+fundBalance.get(uuid)),uuid);
        }
    }
    public static boolean donate(ServerPlayerEntity player,int amount){
        UUID uuid=player.getUUID(); int balance=fundBalance.getOrDefault(uuid,0);
        if(balance<amount){player.sendMessage(new StringTextComponent("\u00a7c\u57fa\u91d1\u4f59\u989d\u4e0d\u8db3!"),uuid);return false;}
        fundBalance.put(uuid,balance-amount); impactScore.merge(uuid,amount/10,Integer::sum);
        ReputationSystem.addReputation(uuid,amount/100); final int a=amount;
        player.getCapability(SkillCapability.SKILL_CAP).ifPresent(s->s.addExp(a/50));
        player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u57fa\u91d1\u4f1a\u6350\u8d60: "+a+"\u91d1\u5e01|\u5f71\u54cd\u529b+"+(a/10)+"|\u58f0\u8a89+"+(a/100)),uuid);
        return true;
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); int balance=fundBalance.getOrDefault(uuid,0); int impact=impactScore.getOrDefault(uuid,0);
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7a\ud83c\udfe2 \u4f01\u4e1a\u57fa\u91d1\u4f1a  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u57fa\u91d1\u4f59\u989d: \u00a76"+balance+"  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u793e\u4f1a\u5f71\u54cd\u529b: \u00a7b"+impact+"  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        if(balance==0) player.sendMessage(new StringTextComponent("\u00a7e/foundation create <\u91d1\u989d> \u6210\u7acb (\u6700\u4f4e5000)"),uuid);
        else player.sendMessage(new StringTextComponent("\u00a7e/foundation donate <\u91d1\u989d> \u6350\u8d60"),uuid);
    }
}
