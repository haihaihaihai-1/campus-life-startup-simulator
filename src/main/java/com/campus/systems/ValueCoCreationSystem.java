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
public class ValueCoCreationSystem {
    public static class Stakeholder { public String name; public int cost; public int value; public int reqLevel;
        public Stakeholder(String n,int c,int v,int r){name=n;cost=c;value=v;reqLevel=r;} }
    public static final Stakeholder[] STAKEHOLDERS={
        new Stakeholder("\u5ba2\u6237\u53c2\u4e0e",1000,15,2),
        new Stakeholder("\u4f9b\u5e94\u5546\u534f\u540c",2000,20,4),
        new Stakeholder("\u5458\u5de5\u5171\u521b",3000,25,5),
        new Stakeholder("\u793e\u533a\u5171\u5efa",5000,30,8),
        new Stakeholder("\u4ea7\u4e1a\u534f\u540c",10000,40,12),
        new Stakeholder("\u5168\u7403\u521b\u65b0\u7f51",30000,60,18)
    };
    private static final Map<UUID,Set<Integer>> engaged=new HashMap<>();
    private static int tickCounter=0;
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){
        if(event.phase!=TickEvent.Phase.END) return; tickCounter++;
        if(tickCounter%6000!=0||ServerLifecycleHooks.getCurrentServer()==null) return;
        for(ServerPlayerEntity player:ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()){
            UUID uuid=player.getUUID(); Set<Integer> has=engaged.get(uuid); if(has==null||has.isEmpty()) continue;
            int total=0; for(int i:has) total+=STAKEHOLDERS[i].value; final int bonus=total*3;
            player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m->m.addMoney(bonus));
            player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u4ef7\u503c\u5171\u521b\u6536\u5165: +"+bonus+"\u91d1\u5e01"),uuid);
        }
    }
    public static boolean engage(ServerPlayerEntity player,int idx){
        UUID uuid=player.getUUID();
        if(idx<1||idx>STAKEHOLDERS.length){player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548!"),uuid);return false;}
        Set<Integer> has=engaged.getOrDefault(uuid,new HashSet<>());
        if(has.contains(idx-1)){player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u53c2\u4e0e!"),uuid);return false;}
        Stakeholder s=STAKEHOLDERS[idx-1];
        int level=player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if(level<s.reqLevel){player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv."+s.reqLevel),uuid);return false;}
        final int cost=s.cost; final int val=s.value; final String sname=s.name;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(cost)){has.add(idx-1);engaged.put(uuid,has);player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5171\u521b:"+sname+"|\u4ef7\u503c+"+val),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); Set<Integer> has=engaged.getOrDefault(uuid,new HashSet<>());
        int total=0; for(int i:has) total+=STAKEHOLDERS[i].value;
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7a\ud83e\udd1d \u4ef7\u503c\u5171\u521b ("+has.size()+"/"+STAKEHOLDERS.length+"|\u4ef7\u503c+"+total+")  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        for(int i=0;i<STAKEHOLDERS.length;i++){Stakeholder s=STAKEHOLDERS[i];boolean owned=has.contains(i);player.sendMessage(new StringTextComponent("\u00a7e["+(i+1)+"] "+s.name+"|\u00a76 "+s.cost+"\u91d1\u5e01|\u00a7a +"+s.value+"|\u00a7b Lv."+s.reqLevel+"+|"+(owned?"\u00a7a\u2714":"\u00a77\u2716")),uuid);}
        player.sendMessage(new StringTextComponent("\u00a7e/cocreate <1-6> \u5171\u521b"),uuid);
    }
}
