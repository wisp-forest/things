package com.glisco.things.network;

import com.glisco.things.ThingsCommon;
import com.glisco.things.items.ThingsItems;
import dev.emi.trinkets.api.TrinketsApi;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OpenEChestC2SPacket {

    public static final Identifier ID = new Identifier(ThingsCommon.MOD_ID, "openechest");
    private static Logger LOGGER = LogManager.getLogger();

    public static void onPacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buffer, PacketSender sender) {
        server.execute(() -> {
            if (TrinketsApi.getTrinketComponent(player).get().isEquipped(ThingsItems.ENDER_POUCH)) {
                player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) -> {
                    return GenericContainerScreenHandler.createGeneric9x3(i, playerInventory, player.getEnderChestInventory());
                }, new TranslatableText("container.enderpouch")));
            } else {
                LOGGER.warn("Received illegal openEChest packet");
            }
        });
    }

    public static Packet<?> create() {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        return ClientPlayNetworking.createC2SPacket(ID, buffer);
    }
}
