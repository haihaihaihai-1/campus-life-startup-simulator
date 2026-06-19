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
public class AgileFinanceSystem {
    public static class Tool{ public String name; public int cost; public int efficiency; public int reqLevel;
        public Tool(String n,int c,int e,int r){name=n;cost=c;efficiency=e;reqLevel=r;} }
    public static final Tool[] TOOLS={
        new Tool("\u4e91\u8d22\u52a1",1000,8,2), new Tool("\u81ea\u52a8\u5bf9\u8d26",3000,15,4),
        new Tool("\u5b9e\u65f6\u62a5\u8868",8000,25,6), new Tool("\u667a\u80fd\u9884\u6d4b",20000,40,10),
        new Tool("\u5168\u7403\u5408\u5e76",80000,70,18)
    };
    private static final Map<UUID,Set<Integer>> deployed=new HashMap<>();
    private static int tickCounter=0;
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){
        if(event.phase!=TickEvent.Phase.END) return; tickCounter++;
        if(tickCounter%6000!=0||ServerLifecycleHooks.getCurrentServer()==null) return;
        for(ServerPlayerEntity player:ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()){
            UUID uuid=player.getUUID(); Set<Integer> has=deployed.get(uuid); if(has==null||has.isEmpty()) continue;
            int total=0; for(int i:has) total+=TOOLS[i].efficiency; final int bonus=total*8;
            player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m->m.addMoney(bonus));
            player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u654f\u6377\u8d22\u52a1\u6536\u76ca: +"+bonus+"\u91d1\u5e01"),uuid);
        }
    }
    public static boolean deploy(ServerPlayerEntity player,int idx){
        UUID uuid=player.getUUID();
        if(idx<1||idx>TOOLS.length){player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548!"),uuid);return false;}
        Set<Integer> has=deployed.getOrDefault(uuid,new HashSet<>());
        if(has.contains(idx-1)){player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u90e8\u7f72!"),uuid);return false;}
        Tool t=TOOLS[idx-1]; final int cost=t.cost; final int eff=t.efficiency; final String tname=t.name;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(cost)){has.add(idx-1);deployed.put(uuid,has);player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u90e8\u7f72:"+tname+"|\u6548\u7387+"+eff+"%"),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); Set<Integer> has=deployed.getOrDefault(uuid,new HashSet<>());
        int total=0; for(int i:has) total+=TOOLS[i].efficiency;
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7a\ud83d\udcb4 \u654f\u6377\u8d22\u52a1 (\u6548\u7387+"+total+"%)  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        for(int i=0;i<TOOLS.length;i++){Tool t=TOOLS[i];boolean owned=has.contains(i);player.sendMessage(new StringTextComponent("\u00a7e["+(i+1)+"] "+t.name+"|\u00a76 "+t.cost+"\u91d1\u5e01|\u00a7a +"+t.efficiency+"%|\u00a7b Lv."+t.reqLevel+"+|"+(owned?"\u00a7a\u2714":"\u00a77\u2716")),uuid);}
        player.sendMessage(new StringTextComponent("\u00a7e/agilefin <1-5> \u90e8\u7f72"),uuid);
    }
}
