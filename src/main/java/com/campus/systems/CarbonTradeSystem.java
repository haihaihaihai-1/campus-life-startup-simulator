package com.campus.systems;

import com.campus.CampusLife;
import com.campus.economy.MoneyCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import java.util.*;

@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CarbonTradeSystem {
    private static final Map<UUID,Integer> credits=new HashMap<>();
    private static int tickCounter=0; private static int marketPrice=50; private static final Random RAND=new Random();
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){
        if(event.phase!=TickEvent.Phase.END) return; tickCounter++;
        if(tickCounter%2400==0) marketPrice=Math.max(20,Math.min(200,marketPrice+RAND.nextInt(30)-15));
    }
    public static boolean buy(ServerPlayerEntity player,int amount){
        UUID uuid=player.getUUID(); final int cost=amount*marketPrice; final int amt=amount;
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(cost)){credits.merge(uuid,amt,Integer::sum);player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u8d2d\u4e70\u78b3\u914d\u989d:"+amt+"|\u4ef7:"+marketPrice+"\u91d1\u5e01/\u5355\u4f4d|\u6210\u672c:"+cost),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    public static boolean sell(ServerPlayerEntity player,int amount){
        UUID uuid=player.getUUID(); int owned=credits.getOrDefault(uuid,0);
        if(owned<amount){player.sendMessage(new StringTextComponent("\u00a7c\u914d\u989d\u4e0d\u8db3! \u6301\u6709:"+owned),uuid);return false;}
        final int revenue=amount*marketPrice; final int amt=amount;
        credits.put(uuid,owned-amt);
        player.getCapability(MoneyCapability.MONEY_CAP).ifPresent(m->m.addMoney(revenue));
        player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u51fa\u552e\u78b3\u914d\u989d:"+amt+"|\u4ef7:"+marketPrice+"\u91d1\u5e01/\u5355\u4f4d|\u6536\u5165:"+revenue),uuid);
        return true;
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); int owned=credits.getOrDefault(uuid,0); int value=owned*marketPrice;
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7a\ud83c\udf31 \u78b3\u4ea4\u6613  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u5e02\u573a\u4ef7: \u00a76"+marketPrice+"\u91d1\u5e01/\u5355\u4f4d  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7e\u6301\u6709: \u00a7a"+owned+"\u5355\u4f4d (\u00a76"+value+"\u91d1\u5e01)  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        player.sendMessage(new StringTextComponent("\u00a7e/ctrade buy <\u6570\u91cf> | /ctrade sell <\u6570\u91cf>"),uuid);
    }
}
