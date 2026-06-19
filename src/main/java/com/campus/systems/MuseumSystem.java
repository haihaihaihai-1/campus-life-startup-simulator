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
public class MuseumSystem {
    private static final Map<UUID,Integer> exhibits=new HashMap<>();
    private static final Map<UUID,Integer> visitors=new HashMap<>();
    private static int tickCounter=0;
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){
        if(event.phase!=TickEvent.Phase.END) return; tickCounter++;
        if(tickCounter%6000!=0||ServerLifecycleHooks.getCurrentServer()==null) return;
        for(ServerPlayerEntity player:ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()){
            UUID uuid=player.getUUID(); int ex=exhibits.getOrDefault(uuid,0);
            if(ex==0) continue;
            int visitor=new Random().nextInt(ex*10)+ex*5;
            visitors.merge(uuid,visitor,Integer::sum);
            int revenue=visitor*5;
            final int rev=revenue; final int vis=visitor;
            player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m->m.addMoney(rev));
            player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u535a\u7269\u9986\u6536\u5165: \u00a76"+rev+"\u91d1\u5e01 (\u6e38\u5ba2:"+vis+")"),uuid);
        }
    }
    public static boolean addExhibit(ServerPlayerEntity player){
        UUID uuid=player.getUUID();
        int level=player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        int count=exhibits.getOrDefault(uuid,0);
        if(count>=level){player.sendMessage(new StringTextComponent("\u00a7c\u5c55\u54c1\u4e0a\u9650:"+level+" (\u6bcf\u7ea7+1)"),uuid);return false;}
        int cost=1000*(count+1);
        final int newCount=count+1;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(cost)){exhibits.put(uuid,newCount);player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u65b0\u589e\u5c55\u54c1! \u603b\u5c55\u54c1:"+newCount+"|\u6210\u672c:"+cost),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); int ex=exhibits.getOrDefault(uuid,0); int vis=visitors.getOrDefault(uuid,0);
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7d\ud83c\udfe8 \u4f01\u4e1a\u535a\u7269\u9986  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u5c55\u54c1\u6570: "+ex+"  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u7d2f\u8ba1\u6e38\u5ba2: "+vis+"  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        player.sendMessage(new StringTextComponent("\u00a7e/museum add \u65b0\u589e\u5c55\u54c1 (\u8d39\u7528:"+1000*(ex+1)+")"),uuid);
    }
}
