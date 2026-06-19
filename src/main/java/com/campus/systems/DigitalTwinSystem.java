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
public class DigitalTwinSystem {
    public static class SimLevel{ public String name; public int cost; public int optimization; public int reqLevel;
        public SimLevel(String n,int c,int o,int r){name=n;cost=c;optimization=o;reqLevel=r;} }
    public static final SimLevel[] LEVELS={
        new SimLevel("\u57fa\u7840\u6a21\u62df",3000,10,4), new SimLevel("\u5b9e\u65f6\u540c\u6b65",10000,20,6),
        new SimLevel("\u9884\u6d4b\u5206\u6790",30000,35,10), new SimLevel("\u4f18\u5316\u5f15\u64ce",100000,55,15),
        new SimLevel("\u5168\u666f\u6570\u5b57\u5b6a\u751f",500000,90,25)
    };
    private static final Map<UUID,Integer> twinLevel=new HashMap<>();
    private static int tickCounter=0;
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){
        if(event.phase!=TickEvent.Phase.END) return; tickCounter++;
        if(tickCounter%6000!=0||ServerLifecycleHooks.getCurrentServer()==null) return;
        for(ServerPlayerEntity player:ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()){
            UUID uuid=player.getUUID(); int level=twinLevel.getOrDefault(uuid,0); if(level==0) continue;
            int opt=LEVELS[Math.min(level,LEVELS.length)-1].optimization; final int bonus=opt*10;
            player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m->m.addMoney(bonus));
            player.sendMessage(new StringTextComponent("\u00a7b\u2714 \u6570\u5b57\u5b6a\u751f\u4f18\u5316: +"+bonus+"\u91d1\u5e01 (\u4f18\u5316+"+opt+"%)"),uuid);
        }
    }
    public static boolean upgrade(ServerPlayerEntity player,int idx){
        UUID uuid=player.getUUID();
        if(idx<1||idx>LEVELS.length){player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548!"),uuid);return false;}
        if(twinLevel.getOrDefault(uuid,0)>=idx){player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u5347\u7ea7!"),uuid);return false;}
        SimLevel s=LEVELS[idx-1]; int level=player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if(level<s.reqLevel){player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv."+s.reqLevel),uuid);return false;}
        final int cost=s.cost; final int opt=s.optimization; final String sname=s.name; final int newLevel=idx;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(cost)){twinLevel.put(uuid,newLevel);player.sendMessage(new StringTextComponent("\u00a7b\u2714 \u5347\u7ea7: "+sname+"|\u4f18\u5316+"+opt+"%"),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); int level=twinLevel.getOrDefault(uuid,0);
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\ud83e\uddea \u6570\u5b57\u5b6a\u751f Lv."+level+"  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        for(int i=0;i<LEVELS.length;i++){SimLevel s=LEVELS[i];boolean owned=(i+1)<=level;player.sendMessage(new StringTextComponent("\u00a7e["+(i+1)+"] "+s.name+"|\u00a76 "+s.cost+"\u91d1\u5e01|\u00a7a +"+s.optimization+"%|\u00a7b Lv."+s.reqLevel+"+|"+(owned?"\u00a7a\u2714":"\u00a77\u2716")),uuid);}
        player.sendMessage(new StringTextComponent("\u00a7e/dtwin <1-5> \u5347\u7ea7"),uuid);
    }
}
