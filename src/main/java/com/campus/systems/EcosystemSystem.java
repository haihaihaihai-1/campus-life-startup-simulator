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
public class EcosystemSystem {
    public static class Node { public String name; public int cost; public int income; public int reqLevel;
        public Node(String n,int c,int i,int r){name=n;cost=c;income=i;reqLevel=r;} }
    public static final Node[] NODES={
        new Node("\u6821\u56ed\u521b\u4e1a\u793e",500,20,1),
        new Node("\u5b75\u5316\u5668A\u5ea7",2000,80,3),
        new Node("\u521b\u6295\u5496\u5561\u9986",5000,200,5),
        new Node("\u5171\u4eab\u529e\u516c\u7a7a\u95f4",10000,400,8),
        new Node("\u4ea7\u4e1a\u56ed\u533a",30000,1200,12),
        new Node("\u521b\u65b0\u793e\u533a",80000,3000,18),
        new Node("\u5168\u7403\u521b\u4e1a\u7f51",250000,10000,25)
    };
    private static final Map<UUID,Set<Integer>> nodes=new HashMap<>();
    private static int tickCounter=0;
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){
        if(event.phase!=TickEvent.Phase.END) return; tickCounter++;
        if(tickCounter%6000!=0||ServerLifecycleHooks.getCurrentServer()==null) return;
        for(ServerPlayerEntity player:ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()){
            UUID uuid=player.getUUID(); Set<Integer> has=nodes.get(uuid);
            if(has==null||has.isEmpty()) continue;
            int total=0; for(int i:has) total+=NODES[i].income;
            final int income=total;
            player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m->m.addMoney(income));
            player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u751f\u6001\u7f51\u7edc\u6536\u5165: \u00a76"+income+"\u91d1\u5e01 ("+has.size()+"\u8282\u70b9)"),uuid);
        }
    }
    public static boolean build(ServerPlayerEntity player,int idx){
        UUID uuid=player.getUUID();
        if(idx<1||idx>NODES.length){player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548!"),uuid);return false;}
        Set<Integer> has=nodes.getOrDefault(uuid,new HashSet<>());
        if(has.contains(idx-1)){player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u5efa\u7acb!"),uuid);return false;}
        Node n=NODES[idx-1];
        int level=player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if(level<n.reqLevel){player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv."+n.reqLevel),uuid);return false;}
        final int cost=n.cost; final int income=n.income; final String nname=n.name;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(cost)){has.add(idx-1);nodes.put(uuid,has);player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5efa\u7acb:"+nname+"|\u6536\u5165:"+income+"/\u5468"),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); Set<Integer> has=nodes.getOrDefault(uuid,new HashSet<>());
        int total=0; for(int i:has) total+=NODES[i].income;
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7a\ud83c\udf0d \u521b\u4e1a\u751f\u6001 ("+has.size()+"/"+NODES.length+"|\u6536\u5165:"+total+"/\u5468)  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        for(int i=0;i<NODES.length;i++){Node n=NODES[i];boolean owned=has.contains(i);player.sendMessage(new StringTextComponent("\u00a7e["+(i+1)+"] "+n.name+"|\u00a76 "+n.cost+"\u91d1\u5e01|\u00a7a "+n.income+"/\u5468|\u00a7b Lv."+n.reqLevel+"+|"+(owned?"\u00a7a\u2714":"\u00a77\u2716")),uuid);}
        player.sendMessage(new StringTextComponent("\u00a7e/ecosystem <1-7> \u5efa\u7acb"),uuid);
    }
}
