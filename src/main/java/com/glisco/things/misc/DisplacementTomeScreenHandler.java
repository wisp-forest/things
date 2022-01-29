package com.glisco.things.misc;

import com.glisco.things.Things;
import com.glisco.things.items.ThingsItems;
import com.glisco.things.items.generic.DisplacementTomeItem;
import com.glisco.things.network.ThingsNetwork;
import com.glisco.things.network.UpdateDisplacementTomeS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;

public class DisplacementTomeScreenHandler extends ScreenHandler {

    private ItemStack book;
    private PlayerEntity player;

    public DisplacementTomeScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ItemStack.EMPTY);
    }

    public DisplacementTomeScreenHandler(int syncId, PlayerInventory playerInventory, ItemStack book) {
        super(Things.DISPLACEMENT_TOME_SCREEN_HANDLER, syncId);
        this.book = book;
        this.player = playerInventory.player;
    }

    @Override
    public void addListener(ScreenHandlerListener listener) {
        super.addListener(listener);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof DisplacementTomeItem || player.getStackInHand(Hand.OFF_HAND).getItem() instanceof DisplacementTomeItem;
    }

    public void setBook(ItemStack book) {
        this.book = book;
    }

    public void requestTeleport(String location) {
        int currentFuel = book.getOrCreateNbt().contains("Fuel") ? book.getOrCreateNbt().getInt("Fuel") : 0;

        if (currentFuel < Things.CONFIG.displacementTomeFuelConsumption) {
            player.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1, 0);
            return;
        }

        NbtCompound bookTargetsTag = book.getOrCreateSubNbt("Targets");
        if (!bookTargetsTag.contains(location)) return;

        currentFuel -= Things.CONFIG.displacementTomeFuelConsumption;
        book.getOrCreateNbt().putInt("Fuel", currentFuel);

        DisplacementTomeItem.TargetLocation target = DisplacementTomeItem.TargetLocation.fromTag(bookTargetsTag.getCompound(location));
        target.teleportPlayer((ServerPlayerEntity) player);
        player.world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.MASTER, 1, 1);
        ((ServerPlayerEntity) player).closeHandledScreen();
        updateClient();
    }

    public boolean deletePoint(String name) {
        boolean result = DisplacementTomeItem.deletePoint(book, name);
        updateClient();
        return result;
    }

    public void addPoint(String name) {
        player.getInventory().getStack(player.getInventory().getSlotWithStack(new ItemStack(ThingsItems.DISPLACEMENT_PAGE))).decrement(1);
        sendContentUpdates();
        DisplacementTomeItem.storeTeleportTargetInBook(book,
                DisplacementTomeItem.TargetLocation.fromPlayer((ServerPlayerEntity) player), name, false);
        updateClient();
    }

    public boolean renamePoint(String data) {
        boolean result = DisplacementTomeItem.rename(book, data);
        updateClient();
        return result;
    }

    private void updateClient() {
        ((ServerPlayerEntity) player).networkHandler.connection.send(UpdateDisplacementTomeS2CPacket.create(book));
    }

    public ItemStack getBook() {
        return book;
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (!(player instanceof ServerPlayerEntity)) {
            player.playSound(SoundEvents.UI_BUTTON_CLICK, 1, 1);
        }
        return true;
    }

    static {
        ThingsNetwork.CHANNEL.registerServerbound(Packet.class, (message, access) -> {
            if (!(access.player().currentScreenHandler instanceof DisplacementTomeScreenHandler handler)) return;
            final var action = message.action();

            switch (action) {
                case TELEPORT -> handler.requestTeleport(message.data());
                case CREATE_POINT -> handler.addPoint(message.data());
                case DELETE_POINT -> {
                    if (!handler.deletePoint(message.data())) ThingsNetwork.LOGGER.warn("Received invalid DELETE_POINT request");
                }
                case RENAME_POINT -> {
                    if (!handler.renamePoint(message.data())) ThingsNetwork.LOGGER.warn("Received invalid RENAME_POINT request");
                }
            }
        });
    }

    public static final record Packet(Action action, String data) {
        public enum Action {TELEPORT, DELETE_POINT, RENAME_POINT, CREATE_POINT}

        public static Packet teleport(String where) {
            return new Packet(Action.TELEPORT, where);
        }

        public static Packet create(String what) {
            return new Packet(Action.CREATE_POINT, what);
        }

        public static Packet rename(String which) {
            return new Packet(Action.RENAME_POINT, which);
        }

        public static Packet delete(String which) {
            return new Packet(Action.DELETE_POINT, which);
        }
    }
}
