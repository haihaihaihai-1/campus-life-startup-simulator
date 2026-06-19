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
public class ArchiveSystem {
    private static final Map<UUID,List<ArchiveEntry>> archives=new HashMap<>();
    public static class ArchiveEntry{ public String title; public String category; public int value; public long timestamp; public ArchiveEntry(String t,String c,int v){title=t;category=c;value=v;timestamp=System.currentTimeMillis();} }
    public static boolean archive(ServerPlayerEntity player,String category){
        UUID uuid=player.getUUID();
        int level=player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        int cost=200; final String cat=category; final String title=cat+"#"+(archives.getOrDefault(uuid,new ArrayList<>()).size()+1);
        return player.getCapability(MoneyCapability.MONEY_CAP).map(m->{if(m.spendMoney(cost)){archives.computeIfAbsent(uuid,k->new ArrayList<>()).add(new ArchiveEntry(title,cat,level*10));player.getCapability(SkillCapability.SKILL_CAP).ifPresent(s->s.addExp(30));player.sendMessage(new StringTextComponent("\u00a7a\u2714 \u5f52\u6863: "+title+"|\u7c7b\u522b:"+cat),uuid);return true;}player.sendMessage(new StringTextComponent("\u00a7c\u8d44\u91d1\u4e0d\u8db3!"),uuid);return false;}).orElse(false);
    }
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID(); List<ArchiveEntry> list=archives.getOrDefault(uuid,new ArrayList<>());
        int totalValue=0; for(ArchiveEntry e:list) totalValue+=e.value;
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551  \u00a7b\ud83d\udcc1 \u4f01\u4e1a\u6863\u6848\u9986 ("+list.size()+"\u6761|\u4ef7\u503c:"+totalValue+")  \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        for(int i=Math.max(0,list.size()-5);i<list.size();i++){ArchiveEntry e=list.get(i);player.sendMessage(new StringTextComponent("\u00a7e  "+(i+1)+". "+e.title+"|\u00a77 "+e.category+"|\u00a7a "+e.value),uuid);}
        if(list.size()>5) player.sendMessage(new StringTextComponent("\u00a77  ...+"+(list.size()-5)+"\u6761\u65e9\u671f\u6863\u6848"),uuid);
        player.sendMessage(new StringTextComponent("\u00a7e/archive <\u7c7b\u522b> \u5f52\u6863 (200\u91d1\u5e01)"),uuid);
    }
    public static int getArchivedCount(UUID uuid){return archives.getOrDefault(uuid,new ArrayList<>()).size();}
}
