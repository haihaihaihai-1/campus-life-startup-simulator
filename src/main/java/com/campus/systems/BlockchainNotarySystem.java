package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.*;

@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlockchainNotarySystem {
    private static final Map<UUID,List<String>> notarized=new HashMap<>();
    private static final Map<UUID,Integer> trustScore=new HashMap<>();
    public static boolean notarize(ServerPlayerEntity player,String document){
        UUID uuid=player.getUUID();
        final String doc=document; final String hash="0x"+Integer.toHexString(document.hashCode())+System.currentTimeMillis()%10000;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(500)){notarized.computeIfAbsent(uuid,k->new ArrayList<>()).add(doc+"|"+hash);trustScore.merge(uuid,10,Integer::sum);ReputationSystem.addReputation(uuid,3);player.sendMessage(new StringTextComponent("\u00a7b\u2714 \u533a\u5757\u94fe\u5b58\u8bc1\u6210\u529f! "+doc+"|\u54c8\u5e0c:"+hash+"|\u4fe1\u4efb+"+10),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d39\u7528500\u91d1\u5e01!"),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); List<String> list=notarized.getOrDefault(uuid,new ArrayList<>()); int trust=trustScore.getOrDefault(uuid,0);
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\u26d3 \u533a\u5757\u94fe\u5b58\u8bc1 ("+list.size()+"\u6761|\u4fe1\u4efb:"+trust+")  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        for(int i=Math.max(0,list.size()-5);i<list.size();i++) player.sendMessage(new StringTextComponent("\u00a7b  "+(i+1)+". "+list.get(i)),uuid);
        if(list.size()>5) player.sendMessage(new StringTextComponent("\u00a77  ...+"+(list.size()-5)+"\u6761"),uuid);
        player.sendMessage(new StringTextComponent("\u00a7e/blockchain <\u6587\u6863\u540d> \u5b58\u8bc1 (500\u91d1\u5e01)"),uuid);
    }
    public static int getTrust(UUID uuid){return trustScore.getOrDefault(uuid,0);}
}
