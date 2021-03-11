package com.glisco.things.network;

import com.glisco.things.ThingsCommon;
import com.glisco.things.blocks.PlacedItemBlockEntity;
import com.glisco.things.blocks.ThingsBlocks;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlaceItemC2SPacket {

    public static final Identifier ID = new Identifier(ThingsCommon.MOD_ID, "place_item");
    private static Logger LOGGER = LogManager.getLogger();

    public static void onPacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buffer, PacketSender sender) {
        BlockHitResult target = buffer.readBlockHitResult();
        BlockPos pos = target.getBlockPos().offset(target.getSide());

        server.execute(() -> {
            World w = player.world;
            ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);

            if (w.canSetBlock(pos) && !stack.isEmpty() && w.getBlockState(pos).isOf(Blocks.AIR) && w.getBlockState(target.getBlockPos()).isSideSolidFullSquare(w, target.getBlockPos(), target.getSide())) {
                w.setBlockState(pos, ThingsBlocks.PLACED_ITEM.getDefaultState().with(Properties.FACING, target.getSide().getOpposite()));
                ((PlacedItemBlockEntity) w.getBlockEntity(pos)).setItem(stack.copy());
                ((PlacedItemBlockEntity) w.getBlockEntity(pos)).getItem().setCount(1);
                stack.decrement(1);
            } else {
                LOGGER.warn("Received illegal place item packet");
            }
        });
    }

    public static Packet<?> create(BlockHitResult target) {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        buffer.writeBlockHitResult(target);
        return ClientPlayNetworking.createC2SPacket(ID, buffer);
    }
}
