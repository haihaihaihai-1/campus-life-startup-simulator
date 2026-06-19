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
 * 经济系统拼图 - 玩家金钱 Capability
 * 参考: Forge Capability 系统 + Tavern-Tokens 钱包概念
 */
public class MoneyCapability implements ICapabilitySerializable<IntNBT> {

    @CapabilityInject(IMoney.class)
    public static Capability<IMoney> MONEY_CAP = null;

    private IMoney instance = new MoneyImpl();
    private LazyOptional<IMoney> lazy = LazyOptional.of(() -> instance);

    public static void register() {
        CapabilityManager.INSTANCE.register(IMoney.class, new Storage(), MoneyImpl::new);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return MONEY_CAP.orEmpty(cap, lazy);
    }

    @Override
    public IntNBT serializeNBT() {
        return IntNBT.valueOf(instance.getMoney());
    }

    @Override
    public void deserializeNBT(IntNBT nbt) {
        instance.setMoney(nbt.getAsInt());
    }

    public interface IMoney {
        int getMoney();
        void setMoney(int amount);
        void addMoney(int amount);
        boolean spendMoney(int amount);
    }

    public static class MoneyImpl implements IMoney {
        private int money = 1000; // 初始资金1000

        @Override public int getMoney() { return money; }
        @Override public void setMoney(int amount) { this.money = Math.max(0, amount); }
        @Override public void addMoney(int amount) { this.money += amount; }
        @Override public boolean spendMoney(int amount) {
            if (money >= amount) { money -= amount; return true; }
            return false;
        }
    }

    public static class Storage implements Capability.IStorage<IMoney> {
        @Override
        public INBT writeNBT(Capability<IMoney> capability, IMoney instance, Direction side) {
            return IntNBT.valueOf(instance.getMoney());
        }
        @Override
        public void readNBT(Capability<IMoney> capability, IMoney instance, Direction side, INBT nbt) {
            instance.setMoney(((IntNBT) nbt).getAsInt());
        }
    }
}
