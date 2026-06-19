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
public class AICustomerServiceSystem {
    public static class Tier{ public String name; public int cost; public int satisfaction; public int reqLevel;
        public Tier(String n,int c,int s,int r){name=n;cost=c;satisfaction=s;reqLevel=r;} }
    public static final Tier[] TIERS={
        new Tier("\u57fa\u7840\u5ba2\u670d",500,10,1), new Tier("\u667a\u80fd\u5ba2\u670d",2000,20,3),
        new Tier("AI\u5bf9\u8bdd",6000,35,6), new Tier("\u5168\u6e20\u9053\u5ba2\u670d",18000,50,10),
        new Tier("\u8d85\u7ea7AI\u52a9\u7406",60000,80,15)
    };
    private static final Map<UUID,Set<Integer>> deployed=new HashMap<>();
    private static int tickCounter=0;
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){
        if(event.phase!=TickEvent.Phase.END) return; tickCounter++;
        if(tickCounter%6000!=0||ServerLifecycleHooks.getCurrentServer()==null) return;
        for(ServerPlayerEntity player:ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()){
            UUID uuid=player.getUUID(); Set<Integer> has=deployed.get(uuid); if(has==null||has.isEmpty()) continue;
            int total=0; for(int i:has) total+=TIERS[i].satisfaction; final int bonus=total*5;
            player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m->m.addMoney(bonus));
            ReputationSystem.addReputation(uuid,1); ReputationSystem.getReputation(uuid);
            player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5ba2\u670d\u6536\u76ca: +"+bonus+"\u91d1\u5e01|\u6ee1\u610f\u5ea6+"+total),uuid);
        }
    }
    public static boolean deploy(ServerPlayerEntity player,int idx){
        UUID uuid=player.getUUID();
        if(idx<1||idx>TIERS.length){player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548!"),uuid);return false;}
        Set<Integer> has=deployed.getOrDefault(uuid,new HashSet<>());
        if(has.contains(idx-1)){player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u90e8\u7f72!"),uuid);return false;}
        Tier t=TIERS[idx-1]; final int cost=t.cost; final int sat=t.satisfaction; final String tname=t.name;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(cost)){has.add(idx-1);deployed.put(uuid,has);player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u90e8\u7f72:"+tname+"|\u6ee1\u610f\u5ea6+"+sat),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); Set<Integer> has=deployed.getOrDefault(uuid,new HashSet<>());
        int total=0; for(int i:has) total+=TIERS[i].satisfaction;
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\ud83e\udd16 AI\u667a\u80fd\u5ba2\u670d (\u6ee1\u610f\u5ea6+"+total+")  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        for(int i=0;i<TIERS.length;i++){Tier t=TIERS[i];boolean owned=has.contains(i);player.sendMessage(new StringTextComponent("\u00a7e["+(i+1)+"] "+t.name+"|\u00a76 "+t.cost+"\u91d1\u5e01|\u00a7a +"+t.satisfaction+"|\u00a7b Lv."+t.reqLevel+"+|"+(owned?"\u00a7a\u2714":"\u00a77\u2716")),uuid);}
        player.sendMessage(new StringTextComponent("\u00a7e/aics <1-5> \u90e8\u7f72"),uuid);
    }
}
