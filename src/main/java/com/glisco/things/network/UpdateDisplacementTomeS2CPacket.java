package com.glisco.things.network;

import com.glisco.things.DisplacementTomeScreenHandler;
import com.glisco.things.client.DisplacementTomeScreen;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class UpdateDisplacementTomeS2CPacket {

    public static final Identifier ID = new Identifier("things", "update-displacement-tome");

    public static void onPacket(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buffer, PacketSender sender) {
        NbtCompound tag = buffer.readNbt();
        client.execute(() -> {
            if (client.currentScreen instanceof DisplacementTomeScreen) {
                ((DisplacementTomeScreenHandler) ((DisplacementTomeScreen) client.currentScreen).getScreenHandler()).setBook(ItemStack.fromNbt(tag));
                ((DisplacementTomeScreen) client.currentScreen).update();
            }
        });
    }

    public static Packet<?> create(ItemStack warpBook) {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        NbtCompound item = new NbtCompound();
        warpBook.writeNbt(item);
        buffer.writeNbt(item);
        return ServerPlayNetworking.createS2CPacket(ID, buffer);
    }

}
