package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FeedbackSystem {
    private static final Map<UUID,int[]> feedback=new HashMap<>(); // [positive, negative, neutral]
    private static final Map<UUID,Integer> improvementLevel=new HashMap<>();
    public static boolean collect(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); Random rand=new Random();
        int[] fb=feedback.getOrDefault(uuid,new int[]{0,0,0});
        int rep=ReputationSystem.getReputation(uuid);
        int pos=2+rand.nextInt(3)+(rep>70?2:0); int neg=rand.nextInt(3)+(rep<30?2:0); int neu=5-pos-neg;
        fb[0]+=Math.max(0,pos); fb[1]+=Math.max(0,neg); fb[2]+=Math.max(0,neu);
        feedback.put(uuid,fb);
        int total=fb[0]+fb[1]+fb[2]; int satRate=total>0?fb[0]*100/total:0;
        player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u6536\u96c6\u53cd\u9988! \u597d\u8bc4:"+fb[0]+" \u5dee\u8bc4:"+fb[1]+" \u4e2d\u7acb:"+fb[2]+"|\u6ee1\u610f\u5ea6:"+satRate+"%"),uuid);
        if(satRate>80) ReputationSystem.addReputation(uuid,3);
        return true;
    }
    public static boolean improve(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); int cost=1000*(improvementLevel.getOrDefault(uuid,0)+1);
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(cost)){improvementLevel.merge(uuid,1,Integer::sum);ReputationSystem.addReputation(uuid,5);int[] fb=feedback.getOrDefault(uuid,new int[]{0,0,0});if(fb[1]>0)fb[1]--;feedback.put(uuid,fb);player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u6539\u8fdb\u6210\u529f! \u7b49\u7ea7:"+improvementLevel.get(uuid)+"|\u58f0\u8a89+5"),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3! \u9700"+cost),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); int[] fb=feedback.getOrDefault(uuid,new int[]{0,0,0});
        int total=fb[0]+fb[1]+fb[2]; int satRate=total>0?fb[0]*100/total:0;
        int impLevel=improvementLevel.getOrDefault(uuid,0);
        player.sendMessage(new StringTextComponent("\u00a76\u2500\u2500\u2500 \u5ba2\u6237\u53cd\u9988 \u2500\u2500\u2500"),uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u603b\u53cd\u9988: "+total+"|\u00a7a\u597d\u8bc4:"+fb[0]+" \u00a7c\u5dee\u8bc4:"+fb[1]+" \u00a77\u4e2d\u7acb:"+fb[2]),uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u6ee1\u610f\u5ea6: "+satRate+"%"),uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u6539\u8fdb\u7b49\u7ea7: "+impLevel),uuid);
        player.sendMessage(new StringTextComponent("\u00a7e/feedback collect | /feedback improve (\u8d39\u7528:"+1000*(impLevel+1)+")"),uuid);
    }
}
