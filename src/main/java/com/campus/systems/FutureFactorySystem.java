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
public class FutureFactorySystem {
    public static class Upgrade{ public String name; public int cost; public int production; public int reqLevel;
        public Upgrade(String n,int c,int p,int r){name=n;cost=c;production=p;reqLevel=r;} }
    public static final Upgrade[] UPGRADES={
        new Upgrade("\u81ea\u52a8\u5316\u6539\u9020",2000,30,3),
        new Upgrade("\u673a\u5668\u4eba\u751f\u4ea7\u7ebf",8000,80,5),
        new Upgrade("\u6570\u5b57\u5b6a\u751f\u7cfb\u7edf",20000,150,8),
        new Upgrade("\u9ed1\u706f\u5de5\u5382",50000,300,12),
        new Upgrade("AI\u9a7b\u52a8\u751f\u4ea7",150000,800,18),
        new Upgrade("\u91cf\u5b50\u5236\u9020",500000,3000,25)
    };
    private static final Map<UUID,Set<Integer>> installed=new HashMap<>();
    private static int tickCounter=0;
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){
        if(event.phase!=TickEvent.Phase.END) return; tickCounter++;
        if(tickCounter%6000!=0||ServerLifecycleHooks.getCurrentServer()==null) return;
        for(ServerPlayerEntity player:ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()){
            UUID uuid=player.getUUID(); Set<Integer> has=installed.get(uuid); if(has==null||has.isEmpty()) continue;
            int total=0; for(int i:has) total+=UPGRADES[i].production; final int income=total*3;
            player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m->m.addMoney(income));
            player.sendMessage(new StringTextComponent("\u00a7b\u2714 \u672a\u6765\u5de5\u5382\u4ea7\u51fa: +"+income+"\u91d1\u5e01 ("+has.size()+"\u9879\u5347\u7ea7)"),uuid);
        }
    }
    public static boolean install(ServerPlayerEntity player,int idx){
        UUID uuid=player.getUUID();
        if(idx<1||idx>UPGRADES.length){player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548!"),uuid);return false;}
        Set<Integer> has=installed.getOrDefault(uuid,new HashSet<>());
        if(has.contains(idx-1)){player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u5b89\u88c5!"),uuid);return false;}
        Upgrade u=UPGRADES[idx-1];
        int level=player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if(level<u.reqLevel){player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv."+u.reqLevel),uuid);return false;}
        final int cost=u.cost; final int prod=u.production; final String uname=u.name;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(cost)){has.add(idx-1);installed.put(uuid,has);player.sendMessage(new StringTextComponent("\u00a7b\u2714 \u5b89\u88c5: "+uname+"|\u4ea7\u80fd+"+prod+"/\u5468"),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); Set<Integer> has=installed.getOrDefault(uuid,new HashSet<>());
        int total=0; for(int i:has) total+=UPGRADES[i].production;
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\ud83c\udfed \u672a\u6765\u5de5\u5382 ("+has.size()+"/"+UPGRADES.length+"|\u4ea7\u80fd:"+total+"/\u5468)  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        for(int i=0;i<UPGRADES.length;i++){Upgrade u=UPGRADES[i];boolean owned=has.contains(i);player.sendMessage(new StringTextComponent("\u00a7e["+(i+1)+"] "+u.name+"|\u00a76 "+u.cost+"\u91d1\u5e01|\u00a7a +"+u.production+"/\u5468|\u00a7b Lv."+u.reqLevel+"+|"+(owned?"\u00a7a\u2714":"\u00a77\u2716")),uuid);}
        player.sendMessage(new StringTextComponent("\u00a7e/factory <1-6> \u5b89\u88c5"),uuid);
    }
}
