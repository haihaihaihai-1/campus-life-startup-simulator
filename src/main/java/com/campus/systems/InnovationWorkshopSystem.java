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
public class InnovationWorkshopSystem {
    private static final Map<UUID,List<String>> ideas=new HashMap<>();
    private static final Map<UUID,Integer> innovationScore=new HashMap<>();
    private static int tickCounter=0;
    private static final Random RAND=new Random();
    public static final String[] IDEA_TYPES={"\u4ea7\u54c1\u521b\u65b0","\u670d\u52a1\u521b\u65b0","\u6d41\u7a0b\u4f18\u5316","\u6280\u672f\u7a81\u7834","\u5546\u4e1a\u6a21\u5f0f","\u7528\u6237\u4f53\u9a8c"};
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){
        if(event.phase!=TickEvent.Phase.END) return; tickCounter++;
        if(tickCounter%3600!=0||ServerLifecycleHooks.getCurrentServer()==null) return;
        for(ServerPlayerEntity player:ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()){
            UUID uuid=player.getUUID(); List<String> list=ideas.get(uuid);
            if(list==null||list.isEmpty()) continue;
            int bonus=list.size()*20; final int b=bonus;
            player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m->m.addMoney(b));
            player.sendMessage(new StringTextComponent("\u00a7b\u2714 \u521b\u65b0\u4ea7\u51fa: +"+b+"\u91d1\u5e01 ("+list.size()+"\u4e2a\u521b\u610f)"),uuid);
        }
    }
    public static boolean brainstorm(ServerPlayerEntity player){
        UUID uuid=player.getUUID();
        int level=player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        int cost=500; final String idea=IDEA_TYPES[RAND.nextInt(IDEA_TYPES.length)]+"#"+(RAND.nextInt(9000)+1000);
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(cost)){ideas.computeIfAbsent(uuid,k->new ArrayList<>()).add(idea);innovationScore.merge(uuid,1,Integer::sum);player.getCapability(SkillCapability.SKILL_CAP).ifPresent(s->s.addExp(20));player.sendMessage(new StringTextComponent("\u00a7b\u2714 \u521b\u610f\u8bde\u751f: "+idea+"|\u603b\u521b\u610f:"+innovationScore.get(uuid)),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); List<String> list=ideas.getOrDefault(uuid,new ArrayList<>()); int score=innovationScore.getOrDefault(uuid,0);
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7d\ud83d\udca1 \u521b\u65b0\u5de5\u574a (\u521b\u610f:"+list.size()+"|\u5f97\u5206:"+score+")  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        for(int i=0;i<Math.min(5,list.size());i++) player.sendMessage(new StringTextComponent("\u00a7b  "+(i+1)+". "+list.get(i)),uuid);
        if(list.size()>5) player.sendMessage(new StringTextComponent("\u00a77  ...+"+(list.size()-5)+"\u4e2a"),uuid);
        player.sendMessage(new StringTextComponent("\u00a7e/workshop brainstorm \u8111\u529b\u6fc0\u8361 (500\u91d1\u5e01)"),uuid);
    }
}
