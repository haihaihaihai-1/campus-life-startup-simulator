package com.campus.economy;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 技能等级系统拼图 - 创业技能 Capability
 * 参考: Forge Capability 持久化模式
 */
public class SkillCapability implements ICapabilitySerializable<IntNBT> {

    @CapabilityInject(ISkill.class)
    public static Capability<ISkill> SKILL_CAP = null;

    private ISkill instance = new SkillImpl();
    private LazyOptional<ISkill> lazy = LazyOptional.of(() -> instance);

    public static void register() {
        CapabilityManager.INSTANCE.register(ISkill.class, new Storage(), SkillImpl::new);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return SKILL_CAP.orEmpty(cap, lazy);
    }

    @Override
    public IntNBT serializeNBT() { return IntNBT.valueOf(instance.getLevel()); }

    @Override
    public void deserializeNBT(IntNBT nbt) { instance.setLevel(nbt.getAsInt()); }

    public interface ISkill {
        int getLevel();
        void setLevel(int level);
        int getExp();
        void addExp(int amount);
        boolean canLevelUp();
        void levelUp();
        String getRank();
    }

    public static class SkillImpl implements ISkill {
        private int level = 1;
        private int exp = 0;

        @Override public int getLevel() { return level; }
        @Override public void setLevel(int level) { this.level = Math.max(1, level); }
        @Override public int getExp() { return exp; }

        @Override
        public void addExp(int amount) {
            exp += amount;
            while (canLevelUp()) {
                exp -= level * 100;
                level++;
            }
        }

        @Override
        public boolean canLevelUp() { return exp >= level * 100; }

        @Override
        public void levelUp() {
            if (canLevelUp()) {
                exp -= level * 100;
                level++;
            }
        }

        @Override
        public String getRank() {
            if (level >= 50) return "\u00a76\u521b\u4e1a\u5927\u5e08"; // 创业大师
            if (level >= 30) return "\u00a7b\u8d44\u6df1\u521b\u5ba2"; // 资深创客
            if (level >= 15) return "\u00a7a\u521d\u9636\u521b\u4e1a\u8005"; // 初阶创业者
            if (level >= 5) return "\u00a7e\u5b66\u751f\u521b\u5ba2"; // 学生创客
            return "\u00a77\u65b0\u624b\u5b66\u5f92"; // 新手学徒
        }
    }

    public static class Storage implements Capability.IStorage<ISkill> {
        @Override
        public INBT writeNBT(Capability<ISkill> capability, ISkill instance, Direction side) {
            return IntNBT.valueOf(instance.getLevel());
        }
        @Override
        public void readNBT(Capability<ISkill> capability, ISkill instance, Direction side, INBT nbt) {
            instance.setLevel(((IntNBT) nbt).getAsInt());
        }
    }
}
