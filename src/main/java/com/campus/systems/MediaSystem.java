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
public class MediaSystem {
    public static class Channel{ public String name; public int cost; public int followers; public int income; public int reqLevel;
        public Channel(String n,int c,int f,int i,int r){name=n;cost=c;followers=f;income=i;reqLevel=r;} }
    public static final Channel[] CHANNELS={
        new Channel("\u5fae\u535a\u8d26\u53f7",500,100,20,1),
        new Channel("\u6296\u97f3\u8d26\u53f7",2000,500,80,3),
        new Channel("B\u7ad9\u8d26\u53f7",5000,2000,200,5),
        new Channel("\u5fae\u4fe1\u516c\u4f17\u53f7",10000,5000,500,8),
        new Channel("\u5168\u5a92\u4f53\u77e9\u9635",50000,30000,3000,15)
    };
    private static final Map<UUID,Set<Integer>> owned=new HashMap<>();
    private static int tickCounter=0;
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){
        if(event.phase!=TickEvent.Phase.END) return; tickCounter++;
        if(tickCounter%6000!=0||ServerLifecycleHooks.getCurrentServer()==null) return;
        for(ServerPlayerEntity player:ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()){
            UUID uuid=player.getUUID(); Set<Integer> has=owned.get(uuid); if(has==null||has.isEmpty()) continue;
            int total=0; for(int i:has) total+=CHANNELS[i].income; final int income=total;
            player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m->m.addMoney(income));
            player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5a92\u4f53\u6536\u5165: +"+income+"\u91d1\u5e01"),uuid);
        }
    }
    public static boolean acquire(ServerPlayerEntity player,int idx){
        UUID uuid=player.getUUID();
        if(idx<1||idx>CHANNELS.length){player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548!"),uuid);return false;}
        Set<Integer> has=owned.getOrDefault(uuid,new HashSet<>());
        if(has.contains(idx-1)){player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u5f00\u901a!"),uuid);return false;}
        Channel c=CHANNELS[idx-1];
        int level=player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if(level<c.reqLevel){player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv."+c.reqLevel),uuid);return false;}
        final int cost=c.cost; final int followers=c.followers; final int income=c.income; final String cname=c.name;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(cost)){has.add(idx-1);owned.put(uuid,has);player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5f00\u901a: "+cname+"|\u7c89\u4e1d:"+followers+"|\u6536\u5165:"+income+"/\u5468"),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); Set<Integer> has=owned.getOrDefault(uuid,new HashSet<>());
        int totalFollowers=0,totalIncome=0; for(int i:has){totalFollowers+=CHANNELS[i].followers;totalIncome+=CHANNELS[i].income;}
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7d\ud83d\udcf0 \u4f01\u4e1a\u4f20\u5a92 ("+has.size()+"/"+CHANNELS.length+"|\u7c89\u4e1d:"+totalFollowers+"|\u6536\u5165:"+totalIncome+"/\u5468)  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        for(int i=0;i<CHANNELS.length;i++){Channel c=CHANNELS[i];boolean owned2=has.contains(i);player.sendMessage(new StringTextComponent("\u00a7e["+(i+1)+"] "+c.name+"|\u00a76 "+c.cost+"\u91d1\u5e01|\u00a7b \u7c89:"+c.followers+"|\u00a7a "+c.income+"/\u5468|\u00a7b Lv."+c.reqLevel+"+|"+(owned2?"\u00a7a\u2714":"\u00a77\u2716")),uuid);}
        player.sendMessage(new StringTextComponent("\u00a7e/media <1-5> \u5f00\u901a"),uuid);
    }
}
