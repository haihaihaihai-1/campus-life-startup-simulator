package com.campus.economy;

import com.campus.CampusLife;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Capability 附加拼图 - 将经济/技能 Capability 附加到玩家实体
 * 参考: Forge AttachCapabilitiesEvent 模式
 */
@Mod.EventBusSubscriber(modid = CampusLife.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityHandler {

    public static final ResourceLocation MONEY_ID = new ResourceLocation(CampusLife.MOD_ID, "money");
    public static final ResourceLocation SKILL_ID = new ResourceLocation(CampusLife.MOD_ID, "skill");

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            if (!event.getObject().getCapability(MoneyCapability.MONEY_CAP).isPresent()) {
                event.addCapability(MONEY_ID, new MoneyCapability());
            }
            if (!event.getObject().getCapability(SkillCapability.SKILL_CAP).isPresent()) {
                event.addCapability(SKILL_ID, new SkillCapability());
            }
        }
    }
}
