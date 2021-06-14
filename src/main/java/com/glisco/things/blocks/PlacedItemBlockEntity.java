package com.glisco.things.blocks;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class PlacedItemBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

    private ItemStack item;
    private int rotation = 0;

    public PlacedItemBlockEntity(BlockPos pos, BlockState state) {
        super(ThingsBlocks.PLACED_ITEM_BLOCK_ENTITY, pos, state);
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        if (item != null) {
            NbtCompound itemTag = new NbtCompound();
            item.writeNbt(itemTag);
            tag.put("Item", itemTag);
        }
        tag.putInt("Rotation", rotation);
        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        if (tag.contains("Item")) {
            item = ItemStack.fromNbt(tag.getCompound("Item"));
        }
        this.rotation = tag.getInt("Rotation");
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        this.readNbt(tag);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        return this.writeNbt(tag);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (!world.isClient) {
            this.sync();
        }
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
        if (this.rotation > 7) this.rotation = 0;
        if (this.rotation < 0) this.rotation = 7;
        this.markDirty();
    }

    public void changeRotation(boolean direction) {
        setRotation(direction ? rotation + 1 : rotation - 1);
    }
}
