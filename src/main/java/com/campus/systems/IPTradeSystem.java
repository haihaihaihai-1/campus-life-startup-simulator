package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.*;

@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class IPTradeSystem {
    public static class IPAsset { public String name; public int price; public int royalty; public int reqLevel;
        public IPAsset(String n,int p,int r,int rl){name=n;price=p;royalty=r;reqLevel=rl;} }
    public static final IPAsset[] IP_MARKET={
        new IPAsset("\u5feb\u901f\u5145\u7535\u4e13\u5229",2000,50,3),
        new IPAsset("\u667a\u80fd\u7b97\u6cd5\u4e13\u5229",5000,120,5),
        new IPAsset("\u533a\u5757\u94fe\u6280\u672f",10000,300,8),
        new IPAsset("\u91cf\u5b50\u8ba1\u7b97\u6280\u672f",30000,800,12),
        new IPAsset("\u8111\u673a\u63a5\u53e3\u4e13\u5229",80000,2500,20),
        new IPAsset("\u5168\u81ea\u52a8\u5316\u4e13\u5229",200000,8000,30)
    };
    private static final Map<UUID,Set<Integer>> owned=new HashMap<>();
    public static boolean buy(ServerPlayerEntity player,int idx){
        UUID uuid=player.getUUID();
        if(idx<1||idx>IP_MARKET.length){player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548!"),uuid);return false;}
        Set<Integer> has=owned.getOrDefault(uuid,new HashSet<>());
        if(has.contains(idx-1)){player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u62e5\u6709!"),uuid);return false;}
        IPAsset ip=IP_MARKET[idx-1];
        final int price=ip.price; final int royalty=ip.royalty; final String ipname=ip.name;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(price)){has.add(idx-1);owned.put(uuid,has);player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u8d2d\u4e70:"+ipname+"|\u7248\u7a0e:"+royalty+"/\u5468"),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); Set<Integer> has=owned.getOrDefault(uuid,new HashSet<>());
        int totalRoyalty=0; for(int i:has) totalRoyalty+=IP_MARKET[i].royalty;
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7d\u2139 IP\u4ea4\u6613 (\u7248\u7a0e:"+totalRoyalty+"/\u5468)  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        for(int i=0;i<IP_MARKET.length;i++){IPAsset ip=IP_MARKET[i];boolean owned2=has.contains(i);player.sendMessage(new StringTextComponent("\u00a7e["+(i+1)+"] "+ip.name+"|\u00a76 "+ip.price+"\u91d1\u5e01|\u00a7a \u7248\u7a0e"+ip.royalty+"/\u5468|\u00a7b Lv."+ip.reqLevel+"+|"+(owned2?"\u00a7a\u2714":"\u00a77\u2716")),uuid);}
        player.sendMessage(new StringTextComponent("\u00a7e/iptrade <1-6> \u8d2d\u4e70"),uuid);
    }
    public static int getRoyalty(UUID uuid){Set<Integer> has=owned.getOrDefault(uuid,new HashSet<>());int t=0;for(int i:has)t+=IP_MARKET[i].royalty;return t;}
}
