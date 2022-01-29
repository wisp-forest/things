package com.glisco.things.network;

import com.glisco.things.Things;
import com.glisco.things.blocks.PlacedItemBlockEntity;
import com.glisco.things.blocks.ThingsBlocks;
import com.glisco.things.items.ThingsItems;
import dev.emi.trinkets.api.TrinketsApi;
import io.wispforest.owo.network.OwoNetChannel;
import io.wispforest.owo.ops.ItemOps;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.state.property.Properties;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThingsNetwork {

    public static final Logger LOGGER = LogManager.getLogger("things-network");
    public static final OwoNetChannel CHANNEL = OwoNetChannel.create(Things.id("main"));

    private static final NamedScreenHandlerFactory ENDER_POUCH_FACTORY = new SimpleNamedScreenHandlerFactory((syncId, inv, player) ->
            GenericContainerScreenHandler.createGeneric9x3(syncId, inv, player.getEnderChestInventory()),
            new TranslatableText("container.enderpouch"));

    public static void init() {
        CHANNEL.registerServerbound(OpenEnderChestPacket.class, (message, access) -> {
            final var player = access.player();

            if (!TrinketsApi.getTrinketComponent(player).get().isEquipped(ThingsItems.ENDER_POUCH)) {
                LOGGER.warn("Received illegal openEChest packet");
                return;
            }

            player.openHandledScreen(ENDER_POUCH_FACTORY);
        });

        CHANNEL.registerServerbound(PlaceItemPacket.class, (message, access) -> {
            final var target = message.target();
            final var pos = target.getBlockPos().offset(target.getSide());

            final var world = access.player().world;
            if (!world.getBlockState(pos).isAir()) return;
            if (!world.canSetBlock(pos)) {
                LOGGER.warn("Received illegal place item packet");
                return;
            }

            final var stack = access.player().getStackInHand(Hand.MAIN_HAND);
            if (stack.isEmpty()) return;

            if (!world.getBlockState(target.getBlockPos()).isSideSolidFullSquare(world, target.getBlockPos(), target.getSide())) return;

            world.setBlockState(pos, ThingsBlocks.PLACED_ITEM.getDefaultState().with(Properties.FACING, target.getSide().getOpposite()));
            ((PlacedItemBlockEntity) world.getBlockEntity(pos)).setItem(ItemOps.singleCopy(stack));
            stack.decrement(1);
        });
    }


    public static final record OpenEnderChestPacket() {}

    public static final record PlaceItemPacket(BlockHitResult target) {}
}
