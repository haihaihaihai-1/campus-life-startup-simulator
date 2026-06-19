package com.campus.systems;

import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import java.util.UUID;

public class CockpitSystem {
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID();
        int money=player.getCapability(MoneyCapability.MONEY_CAP).map(MoneyCapability.IMoney::getMoney).orElse(0);
        int level=player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        int exp=player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getExp).orElse(0);
        String rank=player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getRank).orElse("");
        int emp=EmployeeSystem.calculateTotalEmployees(uuid);
        int empIncome=EmployeeSystem.calculateTotalIncome(uuid);
        int tech=ResearchSystem.getUnlockedTechs(uuid).size();
        int rep=ReputationSystem.getReputation(uuid);
        int esg=ESGSystem.getRating(uuid);
        int franchises=FranchiseSystem.getShopCount(uuid);
        int regions=ExpansionSystem.getRegionCount(uuid);
        int ipValuation=VCSystem.getValuation(uuid);
        int ipoMarket=IPOSystem.getMarketCap(uuid);
        int brandVal=BrandValueSystem.getValue(uuid);
        int quality=QualityControlSystem.getBoost(uuid);
        int digital=DigitalTransformSystem.getEfficiency(uuid);
        int scSync=SupplyChainSyncSystem.getEfficiency(uuid);
        int scViz=SupplyChainVizSystem.getVisibility(uuid);
        int mentorNet=MentorNetworkSystem.getTotalBoost(uuid);
        int corpUni=CorporateUniversitySystem.getBoost(uuid);
        int talentPipe=TalentPipelineSystem.getScore(uuid);
        int carbon=CarbonTraceSystem.getReductionScore(uuid);
        int risk=RiskWarningSystem.getRisk(uuid);
        int totalBoost=quality+digital+scSync+scViz+mentorNet+corpUni;
        int health=50+level*2+Math.min(20,emp)+Math.min(15,tech*2)+Math.min(15,rep/5)-risk/5;
        health=Math.max(0,Math.min(100,health));
        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551       \u00a7e\ud83d\udda5\u00a7f \u4f01\u4e1a\u9a7e\u9a76\u8231       \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u3010\u6838\u5fc3\u6307\u6807\u3011"),uuid);
        player.sendMessage(new StringTextComponent("  \u00a7f\u8d44\u91d1: \u00a76"+money+"| \u00a7f\u7b49\u7ea7: \u00a7bLv."+level+" "+rank+"| \u00a7f\u7ecf\u9a8c: \u00a7a"+exp+"/"+(level*100)),uuid);
        player.sendMessage(new StringTextComponent("  \u00a7f\u5065\u5eb7\u5ea6: "+(health>=80?"\u00a7a":health>=50?"\u00a7e":"\u00a7c")+health+"/100| \u00a7f\u98ce\u9669: "+(risk>=60?"\u00a7c":risk>=30?"\u00a7e":"\u00a7a")+risk+"/100"),uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u3010\u8d44\u4ea7\u6982\u89c8\u3011"),uuid);
        player.sendMessage(new StringTextComponent("  \u00a7f\u4f30\u503c: \u00a76"+ipValuation+"| IPO\u5e02\u503c: \u00a76"+ipoMarket+"| \u54c1\u724c: \u00a76"+brandVal),uuid);
        player.sendMessage(new StringTextComponent("  \u00a7f\u5458\u5de5: \u00a7b"+emp+"| \u52a0\u76df\u5e97: \u00a7b"+franchises+"| \u533a\u57df: \u00a7b"+regions),uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u3010\u52a0\u6210\u6c47\u603b\u3011"),uuid);
        player.sendMessage(new StringTextComponent("  \u00a7f\u8d28\u91cf: \u00a7a+"+quality+"%| \u00a7f\u6570\u5b57\u5316: \u00a7a+"+digital+"%| \u00a7f\u4f9b\u5e94\u94fe: \u00a7a+"+(scSync+scViz)+"%"),uuid);
        player.sendMessage(new StringTextComponent("  \u00a7f\u5bfc\u5e08: \u00a7a+"+mentorNet+"| \u00a7f\u5927\u5b66: \u00a7a+"+corpUni+"%| \u00a7f\u4eba\u624d: \u00a7a+"+talentPipe),uuid);
        player.sendMessage(new StringTextComponent("  \u00a7f\u603b\u52a0\u6210: \u00a76+"+totalBoost+"%"),uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u3010\u793e\u4f1a\u8d23\u4efb\u3011"),uuid);
        player.sendMessage(new StringTextComponent("  \u00a7f\u58f0\u8a89: "+rep+"| ESG: "+esg+"| \u51cf\u78b3: "+carbon+"| \u6280\u672f: "+tech+"/8"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500"),uuid);
    }
}
