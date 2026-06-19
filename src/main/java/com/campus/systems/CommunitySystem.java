package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import java.util.*;

@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommunitySystem {
    private static final Map<UUID,Integer> contribution=new HashMap<>();
    private static final Map<UUID,Integer> reputation2=new HashMap<>();
    public static boolean post(ServerPlayerEntity player,String type){
        UUID uuid=player.getUUID();
        int cost=100; final int rep=5+new Random().nextInt(10); final int contrib=10;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(cost)){contribution.merge(uuid,contrib,Integer::sum);reputation2.merge(uuid,rep,Integer::sum);NetworkSystem.addNetworkPoint(player,20);player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u53d1\u5e16\u6210\u529f! \u8d21\u732e+"+contrib+"|\u793e\u533a\u58f0\u8a89+"+rep+"|\u4eba\u8109+20"),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    public static boolean help(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); int rep=reputation2.getOrDefault(uuid,0);
        if(rep<20){player.sendMessage(new StringTextComponent("\u00a7c\u793e\u533a\u58f0\u8a89\u4e0d\u8db3! \u970020+"),uuid);return false;}
        int reward=rep*5; final int r=reward;
        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m->m.addMoney(r));
        player.getCapability(SkillCapability.SKILL_CAP).ifPresent(s->s.addExp(r/5));
        reputation2.merge(uuid,-5,Integer::sum);
        player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u52a9\u4eba\u56de\u62a5: +"+r+"\u91d1\u5e01 +"+(r/5)+"\u7ecf\u9a8c"),uuid);
        return true;
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); int contrib=contribution.getOrDefault(uuid,0); int rep=reputation2.getOrDefault(uuid,0);
        String title=rep>=100?"\u00a7d\u793e\u533a\u5927\u4f60":rep>=50?"\u00a7a\u8d44\u6df1\u6210\u5458":rep>=20?"\u00a7b\u6d3b\u8dc3\u6210\u5458":"\u00a77\u65b0\u624b\u6210\u5458";
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7a\ud83e\udd1d \u521b\u4e1a\u793e\u533a "+title+"  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u8d21\u732e\u503c: "+contrib+"|\u793e\u533a\u58f0\u8a89: "+rep+"  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        player.sendMessage(new StringTextComponent("\u00a7e/community post | /community help"),uuid);
    }
}
