package com.campus.network;

import com.campus.CampusLife;
import com.campus.gui.IncubatorContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Supplier;

/**
 * 网络包通信
 */
public class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel CHANNEL;

    public static void register() {
        CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CampusLife.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
        );

        int idx = 0;
        CHANNEL.registerMessage(idx++, IncubatorJoinPacket.class,
            IncubatorJoinPacket::encode, IncubatorJoinPacket::decode, IncubatorJoinPacket::handle);
        CHANNEL.registerMessage(idx++, AuctionBidPacket.class,
            AuctionBidPacket::encode, AuctionBidPacket::decode, AuctionBidPacket::handle);
        CHANNEL.registerMessage(idx++, AuctionBuyoutPacket.class,
            AuctionBuyoutPacket::encode, AuctionBuyoutPacket::decode, AuctionBuyoutPacket::handle);
        CHANNEL.registerMessage(idx++, CryptoBuyPacket.class,
            CryptoBuyPacket::encode, CryptoBuyPacket::decode, CryptoBuyPacket::handle);
        CHANNEL.registerMessage(idx++, CryptoSellPacket.class,
            CryptoSellPacket::encode, CryptoSellPacket::decode, CryptoSellPacket::handle);
    }

    /** 孵化器入驻包 */
    public static class IncubatorJoinPacket {
        private int incubatorIdx;
        public IncubatorJoinPacket() {}
        public IncubatorJoinPacket(int idx) { this.incubatorIdx = idx; }
        public static void encode(IncubatorJoinPacket pkt, PacketBuffer buf) { buf.writeByte(pkt.incubatorIdx); }
        public static IncubatorJoinPacket decode(PacketBuffer buf) { return new IncubatorJoinPacket(buf.readByte() & 0xFF); }
        public static void handle(IncubatorJoinPacket pkt, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player != null) {
                    com.campus.systems.IncubatorSystem.join(player, pkt.incubatorIdx + 1);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }

    /** 拍卖出价包 */
    public static class AuctionBidPacket {
        private int auctionIdx;
        private int bidAmount;
        public AuctionBidPacket() {}
        public AuctionBidPacket(int idx, int amount) { this.auctionIdx = idx; this.bidAmount = amount; }
        public static void encode(AuctionBidPacket pkt, PacketBuffer buf) {
            buf.writeByte(pkt.auctionIdx); buf.writeInt(pkt.bidAmount);
        }
        public static AuctionBidPacket decode(PacketBuffer buf) {
            return new AuctionBidPacket(buf.readByte() & 0xFF, buf.readInt());
        }
        public static void handle(AuctionBidPacket pkt, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player != null) {
                    com.campus.systems.AuctionSystem.bid(player, pkt.auctionIdx, pkt.bidAmount);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }

    /** 拍卖一口价包 */
    public static class AuctionBuyoutPacket {
        private int auctionIdx;
        public AuctionBuyoutPacket() {}
        public AuctionBuyoutPacket(int idx) { this.auctionIdx = idx; }
        public static void encode(AuctionBuyoutPacket pkt, PacketBuffer buf) { buf.writeByte(pkt.auctionIdx); }
        public static AuctionBuyoutPacket decode(PacketBuffer buf) { return new AuctionBuyoutPacket(buf.readByte() & 0xFF); }
        public static void handle(AuctionBuyoutPacket pkt, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player != null) {
                    com.campus.systems.AuctionSystem.buyout(player, pkt.auctionIdx);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }

    /** 加密货币买入包 */
    public static class CryptoBuyPacket {
        private String coin;
        private int amount;
        public CryptoBuyPacket() {}
        public CryptoBuyPacket(String coin, int amount) { this.coin = coin; this.amount = amount; }
        public static void encode(CryptoBuyPacket pkt, PacketBuffer buf) {
            buf.writeUtf(pkt.coin); buf.writeInt(pkt.amount);
        }
        public static CryptoBuyPacket decode(PacketBuffer buf) {
            return new CryptoBuyPacket(buf.readUtf(8), buf.readInt());
        }
        public static void handle(CryptoBuyPacket pkt, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player != null) {
                    com.campus.systems.CryptoSystem.buy(player, pkt.coin, pkt.amount);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }

    /** 加密货币卖出包 */
    public static class CryptoSellPacket {
        private String coin;
        private int amount;
        public CryptoSellPacket() {}
        public CryptoSellPacket(String coin, int amount) { this.coin = coin; this.amount = amount; }
        public static void encode(CryptoSellPacket pkt, PacketBuffer buf) {
            buf.writeUtf(pkt.coin); buf.writeInt(pkt.amount);
        }
        public static CryptoSellPacket decode(PacketBuffer buf) {
            return new CryptoSellPacket(buf.readUtf(8), buf.readInt());
        }
        public static void handle(CryptoSellPacket pkt, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player != null) {
                    com.campus.systems.CryptoSystem.sell(player, pkt.coin, pkt.amount);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
