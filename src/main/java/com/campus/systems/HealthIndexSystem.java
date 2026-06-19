package com.campus.systems;

import com.campus.economy.MoneyCapability;
import com.campus.economy.SkillCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import java.util.UUID;

public class HealthIndexSystem {
    public static void show(ServerPlayerEntity player){
        UUID uuid=player.getUUID();
        int money=player.getCapability(MoneyCapability.MONEY_CAP).map(MoneyCapability.IMoney::getMoney).orElse(0);
        int level=player.getCapability(SkillCapability.SKILL_CAP).map(SkillCapability.ISkill::getLevel).orElse(1);
        int rep=ReputationSystem.getReputation(uuid);
        int esg=ESGSystem.getRating(uuid);
        int risk=RiskWarningSystem.getRisk(uuid);
        int emp=EmployeeSystem.calculateTotalEmployees(uuid);
        int tech=ResearchSystem.getUnlockedTechs(uuid).size();
        int ipo=IPOSystem.isListed(uuid)?1:0;
        int vc=VCSystem.getRound(uuid);
        int carbon=CarbonTraceSystem.getReductionScore(uuid);
        int audit=AuditSystem.getScore(uuid);
        int quality=QualityControlSystem.getBoost(uuid);
        int digital=DigitalTransformSystem.getEfficiency(uuid);

        int financialScore=Math.min(100,money/1000);
        int growthScore=Math.min(100,level*2+tech*5+vc*10);
        int operationalScore=Math.min(100,emp*5+quality+digital);
        int governanceScore=Math.min(100,rep/2+esg/2+audit);
        int riskScore=Math.max(0,100-risk);
        int sustainabilityScore=Math.min(100,carbon+esg);
        int overall=(financialScore+growthScore+operationalScore+governanceScore+riskScore+sustainabilityScore)/6;

        player.sendMessage(new StringTextComponent("\u00a76\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2551         \u00a7a\u2764 \u4f01\u4e1a\u5065\u5eb7\u6307\u6570         \u00a76\u2551"),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d"),uuid);
        String bar="";
        for(int i=0;i<20;i++) bar+=(i<overall/5?"\u00a7a\u2588":"\u00a77\u2588");
        player.sendMessage(new StringTextComponent("\u00a7e\u7efc\u5408: "+bar+" \u00a7f"+overall+"/100 "+getGrade(overall)),uuid);
        player.sendMessage(new StringTextComponent(""),uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u8d22\u52a1\u5065\u5eb7: "+scoreBar(financialScore)),uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u6210\u957f\u6f5c\u529b: "+scoreBar(growthScore)),uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u8fd0\u8425\u6548\u7387: "+scoreBar(operationalScore)),uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u6cbb\u7406\u6c34\u5e73: "+scoreBar(governanceScore)),uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u98ce\u9669\u62b5\u5fa1: "+scoreBar(riskScore)),uuid);
        player.sendMessage(new StringTextComponent("\u00a7e\u53ef\u6301\u7eed\u6027: "+scoreBar(sustainabilityScore)),uuid);
        player.sendMessage(new StringTextComponent(""),uuid);
        String advice;
        if(overall>=80) advice="\u00a7a\u4f01\u4e1a\u72b6\u6001\u5386\u53f2\u6700\u4f73! \u53ef\u8003\u8651IPO\u6216\u5168\u7403\u6269\u5f20";
        else if(overall>=60) advice="\u00a7a\u4f01\u4e1a\u8fd0\u8425\u826f\u597d! \u5efa\u8bae\u52a0\u5927\u7814\u53d1\u6295\u5165";
        else if(overall>=40) advice="\u00a7e\u4f01\u4e1a\u57fa\u672c\u5065\u5eb7! \u5efa\u8bae\u4f18\u5316\u8584\u5f31\u73af\u8282";
        else advice="\u00a7c\u4f01\u4e1a\u9700\u8981\u6539\u5584! \u91cd\u70b9\u5173\u6ce8\u8d22\u52a1\u548c\u98ce\u9669\u7ba1\u7406";
        player.sendMessage(new StringTextComponent(advice),uuid);
        player.sendMessage(new StringTextComponent("\u00a76\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500"),uuid);
    }
    private static String scoreBar(int score){
        String color=score>=80?"\u00a7a":score>=60?"\u00a7e":score>=40?"\u00a76":"\u00a7c";
        String bar=""; for(int i=0;i<10;i++) bar+=(i<score/10?color+"\u2588":"\u00a77\u2588");
        return bar+" \u00a7f"+score+"/100";
    }
    private static String getGrade(int score){
        if(score>=90) return "\u00a7dS\u7ea7"; if(score>=80) return "\u00a7aA\u7ea7";
        if(score>=70) return "\u00a7bB\u7ea7"; if(score>=60) return "\u00a7eC\u7ea7";
        if(score>=40) return "\u00a76D\u7ea7"; return "\u00a7cF\u7ea7";
    }
}
