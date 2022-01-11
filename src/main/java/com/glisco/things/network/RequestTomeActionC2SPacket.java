package com.glisco.things.network;

import com.glisco.things.misc.DisplacementTomeScreenHandler;
import com.glisco.things.Things;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RequestTomeActionC2SPacket {

    public static final Identifier ID = new Identifier(Things.MOD_ID, "request-tome-action");
    private static final Logger LOGGER = LogManager.getLogger();

    public static void onPacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buffer, PacketSender sender) {

        Action action = Action.valueOf(buffer.readString(100));
        String data = buffer.readString(100);
        server.execute(() -> {
            if (player.currentScreenHandler instanceof DisplacementTomeScreenHandler) {
                if (action == Action.TELEPORT) {
                    ((DisplacementTomeScreenHandler) player.currentScreenHandler).requestTeleport(data);
                } else if (action == Action.DELETE_POINT) {
                    if (!((DisplacementTomeScreenHandler) player.currentScreenHandler).deletePoint(data)) {
                        LOGGER.warn("Received invalid DELETE_POINT request");
                    }
                } else if (action == Action.CREATE_POINT) {
                    ((DisplacementTomeScreenHandler) player.currentScreenHandler).addPoint(data);
                } else if (action == Action.RENAME_POINT) {
                    if (!((DisplacementTomeScreenHandler) player.currentScreenHandler).renamePoint(data)) {
                        LOGGER.warn("Received invalid RENAME_POINT request");
                    }
                }
            }
        });
    }

    public static Packet<?> create(Action action, String data) {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        buffer.writeString(action.toString());
        buffer.writeString(data);
        return ClientPlayNetworking.createC2SPacket(ID, buffer);
    }

    public enum Action {
        TELEPORT, DELETE_POINT, RENAME_POINT, CREATE_POINT
    }

}
