package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.*;

@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CarbonTraceSystem {
    private static final Map<UUID,Integer> carbonTracked=new HashMap<>();
    private static final Map<UUID,Integer> reductionScore=new HashMap<>();
    public static class Action{ public String name; public int cost; public int reduction; public int reqLevel;
        public Action(String n,int c,int r,int rl){name=n;cost=c;reduction=r;reqLevel=rl;} }
    public static final Action[] ACTIONS={
        new Action("\u7eff\u8272\u4f9b\u5e94\u5546",1000,20,2),
        new Action("\u8282\u80fd\u6539\u9020",3000,40,4),
        new Action("\u53ef\u518d\u751f\u5305\u88c5",5000,50,6),
        new Action("\u6e05\u6d01\u80fd\u6e90",15000,100,10),
        new Action("\u78b3\u4e2d\u548c\u6280\u672f",50000,250,15)
    };
    public static boolean implement(ServerPlayerEntity player,int idx){
        UUID uuid=player.getUUID();
        if(idx<1||idx>ACTIONS.length){player.sendMessage(new StringTextComponent("\u00a7c\u65e0\u6548!"),uuid);return false;}
        Action a=ACTIONS[idx-1]; final int cost=a.cost; final int reduction=a.reduction; final String aname=a.name;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(cost)){carbonTracked.merge(uuid,reduction,Integer::sum);reductionScore.merge(uuid,reduction/5,Integer::sum);ReputationSystem.addReputation(uuid,reduction/10);ESGSystem.invest(player,1,reduction/20);player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5b9e\u65bd: "+aname+"|\u78b3\u51cf\u5c11"+reduction+"|\u51cf\u78b3\u8bc4\u5206+"+(reduction/5)),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); int tracked=carbonTracked.getOrDefault(uuid,0); int score=reductionScore.getOrDefault(uuid,0);
        String grade=score>=200?"\u00a7a\u78b3\u4e2d\u548c":score>=100?"\u00a7b\u4f4e\u78b3":score>=50?"\u00a7e\u51cf\u78b3":"\u00a77\u666e\u901a";
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7a\ud83c\udf31 \u78b3\u8db3\u8ff9\u8ffd\u8e2a "+grade+"  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u5df2\u51cf\u78b3: "+tracked+"kg|\u51cf\u78b3\u8bc4\u5206: "+score+"  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        for(int i=0;i<ACTIONS.length;i++){Action a=ACTIONS[i];player.sendMessage(new StringTextComponent("\u00a7e["+(i+1)+"] "+a.name+"|\u00a76 "+a.cost+"\u91d1\u5e01|\u00a7a \u51cf\u78b3"+a.reduction+"kg|\u00a7b Lv."+a.reqLevel+"+"),uuid);}
        player.sendMessage(new StringTextComponent("\u00a7e/carbon <1-5> \u5b9e\u65bd"),uuid);
    }

    public static int getReductionScore(UUID uuid) { return reductionScore.getOrDefault(uuid, 0); }
}
