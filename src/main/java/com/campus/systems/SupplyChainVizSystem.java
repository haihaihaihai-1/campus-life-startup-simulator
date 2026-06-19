package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.*;

@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SupplyChainVizSystem {
    public static class Module { public String name; public int cost; public int visibility; public int reqLevel;
        public Module(String n,int c,int v,int r){name=n;cost=c;visibility=v;reqLevel=r;} }
    public static final Module[] MODULES={
        new Module("\u5b9e\u65f6\u8ffd\u8e2a",1000,10,2),
        new Module("\u6570\u636e\u770b\u677f",3000,15,4),
        new Module("\u5f02\u5e38\u9884\u8b66",8000,25,6),
        new Module("\u5168\u94fe\u8def\u5206\u6790",20000,35,10),
        new Module("AI\u9884\u6d4b",60000,50,15)
    };
    private static final Map<UUID,Set<Integer>> deployed=new HashMap<>();
    public static boolean deploy(ServerPlayerEntity player,int idx){
        UUID uuid=player.getUUID();
        if(idx<1||idx>MODULES.length){player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548!"),uuid);return false;}
        Set<Integer> has=deployed.getOrDefault(uuid,new HashSet<>());
        if(has.contains(idx-1)){player.sendMessage(new StringTextComponent("\u00a7c\u5df2\u90e8\u7f72!"),uuid);return false;}
        Module m=MODULES[idx-1];
        final int cost=m.cost; final int vis=m.visibility; final String mname=m.name;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(money->{if(money.spendMoney(cost)){has.add(idx-1);deployed.put(uuid,has);player.sendMessage(new StringTextComponent("\u00a7b\u2714 \u90e8\u7f72:"+mname+"|\u53ef\u89c6\u5316+"+vis+"%"),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); Set<Integer> has=deployed.getOrDefault(uuid,new HashSet<>());
        int total=0; for(int i:has) total+=MODULES[i].visibility;
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\ud83d\udcca \u4f9b\u5e94\u94fe\u53ef\u89c6\u5316 (\u53ef\u89c6\u5316+"+total+"%)  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        for(int i=0;i<MODULES.length;i++){Module m=MODULES[i];boolean owned=has.contains(i);player.sendMessage(new StringTextComponent("\u00a7e["+(i+1)+"] "+m.name+"|\u00a76 "+m.cost+"\u91d1\u5e01|\u00a7a +"+m.visibility+"%|\u00a7b Lv."+m.reqLevel+"+|"+(owned?"\u00a7a\u2714":"\u00a77\u2716")),uuid);}
        player.sendMessage(new StringTextComponent("\u00a7e/scviz <1-5> \u90e8\u7f72"),uuid);
    }
    public static int getVisibility(UUID uuid){Set<Integer> has=deployed.getOrDefault(uuid,new HashSet<>());int t=0;for(int i:has)t+=MODULES[i].visibility;return t;}
}
