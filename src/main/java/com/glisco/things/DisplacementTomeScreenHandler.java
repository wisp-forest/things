package com.glisco.things;

import com.glisco.things.items.DisplacementTomeItem;
import com.glisco.things.items.ThingsItems;
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
        super(ThingsCommon.DISPLACEMENT_TOME_SCREEN_HANDLER, syncId);
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
        int currentFuel = book.getOrCreateTag().contains("Fuel") ? book.getOrCreateTag().getInt("Fuel") : 0;

        if (currentFuel <= 0) {
            player.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1, 0);
            return;
        }

        NbtCompound bookTargetsTag = book.getOrCreateSubTag("Targets");
        if (!bookTargetsTag.contains(location)) return;

        currentFuel--;
        book.getOrCreateTag().putInt("Fuel", currentFuel);

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
        DisplacementTomeItem.storeTeleportTargetInBook(book, new DisplacementTomeItem.TargetLocation(player.getBlockPos(), player.world.getRegistryKey(), player.getHeadYaw(), player.getPitch()), name, false);
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

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (!(player instanceof ServerPlayerEntity)) {
            player.playSound(SoundEvents.UI_BUTTON_CLICK, 1, 1);
        }
        return true;
    }

    public ItemStack getBook() {
        return book;
    }
}
