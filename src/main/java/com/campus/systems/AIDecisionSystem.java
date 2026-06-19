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
public class AIDecisionSystem {
    public static class Model{ public String name; public int cost; public int accuracy; public int reqLevel;
        public Model(String n,int c,int a,int r){name=n;cost=c;accuracy=a;reqLevel=r;} }
    public static final Model[] MODELS={
        new Model("\u57fa\u7840\u5206\u6790",2000,60,3), new Model("\u9884\u6d4b\u6a21\u578b",8000,75,6),
        new Model("\u6df1\u5ea6\u5b66\u4e60",25000,85,10), new Model("\u5f3a\u5316\u5b66\u4e60",80000,92,15),
        new Model("\u901a\u7528AI",300000,98,25)
    };
    private static final Map<UUID,Integer> modelLevel=new HashMap<>();
    public static boolean acquire(ServerPlayerEntity player,int idx){
        UUID uuid=player.getUUID();
        if(idx<1||idx>MODELS.length){player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548!"),uuid);return false;}
        if(modelLevel.getOrDefault(uuid,0)>=idx){player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u62e5\u6709!"),uuid);return false;}
        Model m=MODELS[idx-1]; int level=player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if(level<m.reqLevel){player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv."+m.reqLevel),uuid);return false;}
        final int cost=m.cost; final int acc=m.accuracy; final String mname=m.name; final int newLevel=idx;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(money->{if(money.spendMoney(cost)){modelLevel.put(uuid,newLevel);int bonus=acc*20;player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(mm->mm.addMoney(bonus));player.getCapability(SkillCapability.SKILL_CAP).ifPresent(s->s.addExp(acc*5));player.sendMessage(new StringTextComponent("\u00a7b\u2714 \u83b7\u5f97: "+mname+"|\u51c6\u786e\u7387:"+acc+"%|\u51b3\u7b56\u5956\u52b1:"+bonus+"\u91d1\u5e01"),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); int level=modelLevel.getOrDefault(uuid,0);
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\ud83e\udd16 AI\u51b3\u7b56\u5f15\u64ce Lv."+level+"  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        for(int i=0;i<MODELS.length;i++){Model m=MODELS[i];boolean owned=(i+1)<=level;player.sendMessage(new StringTextComponent("\u00a7e["+(i+1)+"] "+m.name+"|\u00a76 "+m.cost+"\u91d1\u5e01|\u00a7a \u51c6\u786e"+m.accuracy+"%|\u00a7b Lv."+m.reqLevel+"+|"+(owned?"\u00a7a\u2714":"\u00a77\u2716")),uuid);}
        player.sendMessage(new StringTextComponent("\u00a7e/aidecision <1-5> \u83b7\u5f97"),uuid);
    }
    public static int getAccuracy(UUID uuid){int level=modelLevel.getOrDefault(uuid,0);return level>0?MODELS[level-1].accuracy:0;}
}
