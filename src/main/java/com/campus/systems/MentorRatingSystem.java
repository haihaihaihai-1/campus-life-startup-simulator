package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.*;

@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MentorRatingSystem {
    private static final Map<UUID,Integer> ratingScore=new HashMap<>();
    private static final Map<UUID,Integer> totalMentees=new HashMap<>();
    public static boolean rate(ServerPlayerEntity player,int score){
        UUID uuid=player.getUUID();
        if(score<1||score>5){player.sendMessage(new StringTextComponent("\u00a7c\u8bc4\u52061-5!"),uuid);return false;}
        ratingScore.merge(uuid,score,Integer::sum); totalMentees.merge(uuid,1,Integer::sum);
        final int s=score; final int mentees=totalMentees.get(uuid); final int total=ratingScore.get(uuid);
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{int reward=s*100; m.addMoney(reward);player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u83b7\u5f97\u8bc4\u5206:"+s+"\u661f|\u5956\u52b1:"+reward+"\u91d1\u5e01|\u5b66\u5458:"+mentees+"|\u5e73\u5747:"+String.format("%.1f",(double)total/mentees)),uuid);return true;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); int total=ratingScore.getOrDefault(uuid,0); int mentees=totalMentees.getOrDefault(uuid,0);
        double avg=mentees>0?(double)total/mentees:0;
        String grade=avg>=4.5?"\u00a7d\u4f20\u5947\u5bfc\u5e08":avg>=4.0?"\u00a7a\u4f18\u79c0\u5bfc\u5e08":avg>=3.0?"\u00a7b\u826f\u597d\u5bfc\u5e08":avg>=2.0?"\u00a7e\u4e00\u822c\u5bfc\u5e08":"\u00a77\u65b0\u624b\u5bfc\u5e08";
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7d\u2b50 \u5bfc\u5e08\u8bc4\u5206 "+grade+"  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u5b66\u5458: "+mentees+"|\u5e73\u5747\u8bc4\u5206: "+String.format("%.1f",avg)+"  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        player.sendMessage(new StringTextComponent("\u00a7e/mrate <1-5> \u63a5\u53d7\u8bc4\u5206"),uuid);
    }
    public static double getAverage(UUID uuid){int total=ratingScore.getOrDefault(uuid,0);int m=totalMentees.getOrDefault(uuid,0);return m>0?(double)total/m:0;}
}
