package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GovRelationSystem {
    private static final Map<UUID,Integer> relation=new HashMap<>();
    public enum Action{ LOBBY("\u6e38\u8bf4\u6d3b\u52a8",2000,10,5),POLICY_FEEDBACK("\u653f\u7b56\u53cd\u9988",1000,5,3),PARTNERSHIP("\u653f\u4f01\u5408\u4f5c",5000,20,8),THINK_TANK("\u667a\u5e93\u53c2\u4e0e",10000,30,12),ADVISORY("\u653f\u5e9c\u987e\u95ee",30000,50,20);
        public String name; public int cost,boost,reqLevel; Action(String n,int c,int b,int r){name=n;cost=c;boost=b;reqLevel=r;} }
    public static boolean act(ServerPlayerEntity player,int idx){
        UUID uuid=player.getUUID(); Action[] actions=Action.values();
        if(idx<1||idx>actions.length) return false;
        Action a=actions[idx-1]; int level=player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if(level<a.reqLevel){player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv."+a.reqLevel),uuid);return false;}
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(a.cost)){relation.merge(uuid,a.boost,Integer::sum);player.sendMessage(new StringTextComponent("\u00a7a\u2714 "+a.name+"|\u653f\u5e9c\u5173\u7cfb+"+a.boost),uuid);int bonus=a.boost*10;player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(money->money.addMoney(bonus));player.sendMessage(new StringTextComponent("\u00a7e\u653f\u7b56\u7ea2\u5229:"+bonus+"\u91d1\u5e01"),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); int rel=relation.getOrDefault(uuid,0);
        String grade=rel>=100?"\u00a7d\u6218\u7565\u5408\u4f5c\u4f19\u4f34":rel>=50?"\u00a7a\u91cd\u70b9\u5408\u4f5c\u5355\u4f4d":rel>=20?"\u00a7b\u4fe1\u8d56\u5408\u4f5c":rel>=5?"\u00a7e\u57fa\u672c\u5408\u4f5c":"\u00a77\u4e00\u822c\u5173\u7cfb";
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7c\ud83c\udfe2 \u653f\u5e9c\u5173\u7cfb "+grade+"  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u5173\u7cfb\u503c: "+rel+"  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        Action[] actions=Action.values();
        for(int i=0;i<actions.length;i++){Action a=actions[i];player.sendMessage(new StringTextComponent("\u00a7e["+(i+1)+"] "+a.name+"|\u00a76 "+a.cost+"\u91d1\u5e01|\u00a7a +"+a.boost+"|\u00a7b Lv."+a.reqLevel+"+"),uuid);}
        player.sendMessage(new StringTextComponent("\u00a7e/gov <1-5> \u884c\u52a8"),uuid);
    }
    public static int getRelation(UUID uuid){return relation.getOrDefault(uuid,0);}
}
