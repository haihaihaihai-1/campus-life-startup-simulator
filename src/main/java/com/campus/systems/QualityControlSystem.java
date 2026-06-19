package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class QualityControlSystem {
    public enum Level { ISO9001("ISO9001",1000,5,1), SIX_SIGMA("6\u897f\u683c\u739b",5000,15,5), LEAN("\u7cbe\u76ca\u751f\u4ea7",10000,25,8), TQM("\u5168\u9762\u8d28\u91cf\u7ba1\u7406",25000,40,12), WORLD_CLASS("\u4e16\u754c\u7ea7\u8d28\u91cf",100000,60,20);
        public String name; public int cost,boost,reqLevel; Level(String n,int c,int b,int r){name=n;cost=c;boost=b;reqLevel=r;} }
    private static final Map<UUID,Set<Level>> certified=new HashMap<>();
    public static boolean certify(ServerPlayerEntity player,int idx){
        UUID uuid=player.getUUID(); Level[] levels=Level.values();
        if(idx<1||idx>levels.length) return false;
        Level l=levels[idx-1]; Set<Level> has=certified.getOrDefault(uuid,new HashSet<>());
        if(has.contains(l)){player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u8ba4\u8bc1!"),uuid);return false;}
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(l.cost)){has.add(l);certified.put(uuid,has);ReputationSystem.addReputation(uuid,3);player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u8ba4\u8bc1:"+l.name+"|\u8d28\u91cf\u52a0\u6210+"+l.boost+"%"),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); Set<Level> has=certified.getOrDefault(uuid,new HashSet<>());
        int total=0; for(Level l:has) total+=l.boost;
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7a\u2705 \u8d28\u91cf\u63a7\u5236 (\u52a0\u6210+"+total+"%)  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        Level[] levels=Level.values();
        for(int i=0;i<levels.length;i++){Level l=levels[i];boolean owned=has.contains(l);player.sendMessage(new StringTextComponent("\u00a7e["+(i+1)+"] "+l.name+"|\u00a76 "+l.cost+"\u91d1\u5e01|\u00a7a +"+l.boost+"%|\u00a7b Lv."+l.reqLevel+"+|"+(owned?"\u00a7a\u2714":"\u00a77\u2716")),uuid);}
        player.sendMessage(new StringTextComponent("\u00a7e/quality <1-5> \u8ba4\u8bc1"),uuid);
    }
    public static int getBoost(UUID uuid){Set<Level> has=certified.getOrDefault(uuid,new HashSet<>());int total=0;for(Level l:has)total+=l.boost;return total;}
}
