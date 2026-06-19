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
public class CorporateUniversitySystem {
    public enum Course{ FOUNDATION("\u65b0\u5458\u8ba4\u8bc1",500,10,1,5),LEADERSHIP("\u9886\u5bfc\u529b\u8bfe\u7a0b",2000,20,3,10),INNOVATION("\u521b\u65b0\u601d\u7ef4",5000,30,5,15),EXECUTIVE("\u9ad8\u7ba1EMBA",15000,50,10,25),GLOBAL("\u5168\u7403\u89c6\u91ce",50000,80,20,40);
        public String name; public int cost,expBoost,reqLevel,permBoost; Course(String n,int c,int e,int r,int p){name=n;cost=c;expBoost=e;reqLevel=r;permBoost=p;} }
    private static final Map<UUID,Set<Course>> completed=new HashMap<>();
    private static final Map<UUID,Integer> totalBoost=new HashMap<>();
    public static boolean enroll(ServerPlayerEntity player,int idx){
        UUID uuid=player.getUUID(); Course[] courses=Course.values();
        if(idx<1||idx>courses.length) return false;
        Course c=courses[idx-1]; Set<Course> has=completed.getOrDefault(uuid,new HashSet<>());
        if(has.contains(c)){player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u5b8c\u6210!"),uuid);return false;}
        int level=player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        if(level<c.reqLevel){player.sendMessage(new StringTextComponent("\u00a7c\u9700Lv."+c.reqLevel),uuid);return false;}
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(c.cost)){has.add(c);completed.put(uuid,has);totalBoost.merge(uuid,c.permBoost,Integer::sum);player.getCapability(SkillCapability.SKILL_CAP).ifPresent(s->s.addExp(c.expBoost*10));player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5b8c\u6210:"+c.name+"|\u7ecf\u9a8c+"+c.expBoost+"|\u6c38\u4e45\u52a0\u6210+"+c.permBoost+"%"),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); Set<Course> has=completed.getOrDefault(uuid,new HashSet<>());
        int boost=totalBoost.getOrDefault(uuid,0);
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7d\ud83c\udf93 \u4f01\u4e1a\u5927\u5b66 ("+has.size()+"/"+Course.values().length+"|\u52a0\u6210+"+boost+"%)  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        Course[] courses=Course.values();
        for(int i=0;i<courses.length;i++){Course c=courses[i];boolean owned=has.contains(c);player.sendMessage(new StringTextComponent("\u00a7e["+(i+1)+"] "+c.name+"|\u00a76 "+c.cost+"\u91d1\u5e01|\u00a7b \u7ecf\u9a8c+"+c.expBoost+"|\u00a7a \u6c38\u4e45+"+c.permBoost+"%|\u00a7b Lv."+c.reqLevel+"+|"+(owned?"\u00a7a\u2714":"\u00a77\u2716")),uuid);}
        player.sendMessage(new StringTextComponent("\u00a7e/uni <1-5> \u62a5\u540d"),uuid);
    }
    public static int getBoost(UUID uuid){return totalBoost.getOrDefault(uuid,0);}
}
