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
public class SmartPricingSystem {
    private static final Map<UUID,Integer> pricingLevel=new HashMap<>();
    private static final Map<UUID,Integer> optimalPrice=new HashMap<>();
    private static int tickCounter=0;
    private static final Random RAND=new Random();
    public static class Tier{ public String name; public int cost; public int boost; public int reqLevel;
        public Tier(String n,int c,int b,int r){name=n;cost=c;boost=b;reqLevel=r;} }
    public static final Tier[] TIERS={
        new Tier("\u57fa\u7840\u5b9a\u4ef7",500,5,1),
        new Tier("\u52a8\u6001\u5b9a\u4ef7",2000,12,3),
        new Tier("\u7ade\u4e89\u5b9a\u4ef7",5000,20,6),
        new Tier("AI\u667a\u80fd\u5b9a\u4ef7",15000,35,10),
        new Tier("\u9884\u6d4b\u5b9a\u4ef7",50000,50,15)
    };
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){
        if(event.phase!=TickEvent.Phase.END) return; tickCounter++;
        if(tickCounter%6000!=0||ServerLifecycleHooks.getCurrentServer()==null) return;
        for(ServerPlayerEntity player:ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()){
            UUID uuid=player.getUUID(); int level=pricingLevel.getOrDefault(uuid,0); if(level==0) continue;
            int boost=TIERS[Math.min(level,TIERS.length)-1].boost;
            int bonus=boost*15; final int b=bonus;
            player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m->m.addMoney(b));
            optimalPrice.put(uuid,100+RAND.nextInt(200));
            player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u667a\u80fd\u5b9a\u4ef7\u6536\u76ca: +"+b+"\u91d1\u5e01 (\u6700\u4f18\u4ef7:"+optimalPrice.get(uuid)+")"),uuid);
        }
    }
    public static boolean upgrade(ServerPlayerEntity player,int idx){
        UUID uuid=player.getUUID();
        if(idx<1||idx>TIERS.length){player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548!"),uuid);return false;}
        if(pricingLevel.getOrDefault(uuid,0)>=idx){player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u5347\u7ea7!"),uuid);return false;}
        Tier t=TIERS[idx-1];
        int level=player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if(level<t.reqLevel){player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv."+t.reqLevel),uuid);return false;}
        final int cost=t.cost; final int boost=t.boost; final String tname=t.name;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(cost)){pricingLevel.put(uuid,idx);player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5b9a\u4ef7\u5347\u7ea7: "+tname+"|\u4ef7\u683c\u4f18\u52bf+"+boost+"%"),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); int level=pricingLevel.getOrDefault(uuid,0); int opt=optimalPrice.getOrDefault(uuid,100);
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\ud83e\udd16 \u667a\u80fd\u5b9a\u4ef7 Lv."+level+"  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        for(int i=0;i<TIERS.length;i++){Tier t=TIERS[i];boolean owned=(i+1)<=level;player.sendMessage(new StringTextComponent("\u00a7e["+(i+1)+"] "+t.name+"|\u00a76 "+t.cost+"\u91d1\u5e01|\u00a7a +"+t.boost+"%|\u00a7b Lv."+t.reqLevel+"+|"+(owned?"\u00a7a\u2714":"\u00a77\u2716")),uuid);}
        player.sendMessage(new StringTextComponent("\u00a7e/pricing <1-5> \u5347\u7ea7|\u00a7b\u6700\u4f18\u4ef7:"+opt),uuid);
    }
}
