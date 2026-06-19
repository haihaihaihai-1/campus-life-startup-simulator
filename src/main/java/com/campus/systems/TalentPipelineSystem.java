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
public class TalentPipelineSystem {
    public static class Program { public String name; public int cost; public int boost; public int reqLevel; public String desc;
        public Program(String n,int c,int b,int r,String d){name=n;cost=c;boost=b;reqLevel=r;desc=d;} }
    public static final Program[] PROGRAMS={
        new Program("\u5b9e\u4e60\u751f\u8ba1\u5212",500,5,1,"\u57fa\u7840\u4eba\u624d\u8f93\u9001"),
        new Program("\u7ba1\u57f9\u751f\u8ba1\u5212",2000,10,3,"\u4e2d\u5c42\u7ba1\u7406\u57f9\u517b"),
        new Program("\u9886\u5bfc\u529b\u8ba1\u5212",5000,20,5,"\u9ad8\u7ba1\u9886\u5bfc\u529b"),
        new Program("\u7ee7\u4efb\u8005\u8ba1\u5212",15000,35,8,"\u6838\u5fc3\u5c97\u4f4d\u7ee7\u4efb"),
        new Program("\u5168\u7403\u82f1\u624d\u8ba1\u5212",50000,60,15,"\u9876\u7ea7\u4eba\u624d\u5e93")
    };
    private static final Map<UUID,Set<Integer>> enrolled=new HashMap<>();
    private static final Map<UUID,Integer> pipelineScore=new HashMap<>();
    public static boolean enroll(ServerPlayerEntity player,int idx){
        UUID uuid=player.getUUID();
        if(idx<1||idx>PROGRAMS.length){player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548!"),uuid);return false;}
        Set<Integer> has=enrolled.getOrDefault(uuid,new HashSet<>());
        if(has.contains(idx-1)){player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u5f00\u8bbe!"),uuid);return false;}
        Program p=PROGRAMS[idx-1];
        int level=player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if(level<p.reqLevel){player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv."+p.reqLevel),uuid);return false;}
        final int cost=p.cost; final int boost=p.boost; final String pname=p.name; final String pdesc=p.desc;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(cost)){has.add(idx-1);enrolled.put(uuid,has);pipelineScore.merge(uuid,boost,Integer::sum);player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5f00\u8bbe:"+pname+"|"+pdesc+"|\u4eba\u624d\u50a8\u5907+"+boost),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); Set<Integer> has=enrolled.getOrDefault(uuid,new HashSet<>()); int score=pipelineScore.getOrDefault(uuid,0);
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\ud83d\udc65 \u4eba\u624d\u68af\u961f (\u50a8\u5907:"+score+")  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        for(int i=0;i<PROGRAMS.length;i++){Program p=PROGRAMS[i];boolean owned=has.contains(i);player.sendMessage(new StringTextComponent("\u00a7e["+(i+1)+"] "+p.name+"|\u00a76 "+p.cost+"\u91d1\u5e01|\u00a7a +"+p.boost+"|\u00a7b Lv."+p.reqLevel+"+|\u00a77 "+p.desc+"|"+(owned?"\u00a7a\u2714":"\u00a77\u2716")),uuid);}
        player.sendMessage(new StringTextComponent("\u00a7e/pipeline <1-5> \u5f00\u8bbe"),uuid);
    }
    public static int getScore(UUID uuid){return pipelineScore.getOrDefault(uuid,0);}
}
