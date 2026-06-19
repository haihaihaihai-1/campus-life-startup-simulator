package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LogisticsInsuranceSystem {
    public enum Coverage { BASIC("\u57fa\u7840\u7269\u6d41\u4fdd\u9669",300,0.3,36000), PREMIUM("\u9ad8\u7ea7\u7269\u6d41\u4fdd\u9669",800,0.6,36000), FULL("\u5168\u7a0b\u7269\u6d41\u4fdd\u9669",2000,1.0,72000);
        public String name; public int premium; public double coverage; public int duration;
        Coverage(String n,int p,double c,int d){name=n;premium=p;coverage=c;duration=d;} }
    private static final Map<UUID,ActiveCoverage> active=new HashMap<>();
    public static class ActiveCoverage{ public int idx; public int remainingTicks; public ActiveCoverage(int i,int t){idx=i;remainingTicks=t;} }
    public static boolean buy(ServerPlayerEntity player,int idx){
        UUID uuid=player.getUUID(); Coverage[] types=Coverage.values();
        if(idx<1||idx>types.length) return false;
        if(active.containsKey(uuid)){player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u6709\u4fdd\u9669!"),uuid);return false;}
        Coverage c=types[idx-1];
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(c.premium)){active.put(uuid,new ActiveCoverage(idx-1,c.duration));player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u8d2d\u4e70"+c.name+"|\u8d54\u7387:"+(int)(c.coverage*100)+"%|"+(c.duration/1200)+"\u5206\u949f"),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); ActiveCoverage ac=active.get(uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2500\u2500\u2500 \u7269\u6d41\u4fdd\u9669 \u2500\u2500\u2500"),uuid);
        Coverage[] types=Coverage.values();
        for(int i=0;i<types.length;i++) player.sendMessage(new StringTextComponent("\u00a7e["+(i+1)+"] "+types[i].name+"|\u00a76 "+types[i].premium+"\u91d1\u5e01|\u00a7a \u8d54\u7387"+(int)(types[i].coverage*100)+"%|\u00a7b "+(types[i].duration/1200)+"\u5206\u949f"),uuid);
        if(ac!=null) player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5f53\u524d:"+types[ac.idx].name+"|\u5269\u4f59:"+(ac.remainingTicks/1200)+"\u5206\u949f"),uuid);
        player.sendMessage(new StringTextComponent("\u00a7e/logins <1-3> \u8d2d\u4e70"),uuid);
    }
    public static int getCoverage(UUID uuid){ ActiveCoverage ac=active.get(uuid); return ac!=null?(int)(Coverage.values()[ac.idx].coverage*100):0; }
}
