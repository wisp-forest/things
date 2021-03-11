package com.glisco.things.blocks;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class PlacedItemBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

    private ItemStack item;
    private int rotation = 0;

    public PlacedItemBlockEntity() {
        super(ThingsBlocks.PLACED_ITEM_BLOCK_ENTITY);
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (item != null) {
            CompoundTag itemTag = new CompoundTag();
            item.toTag(itemTag);
            tag.put("Item", itemTag);
        }
        tag.putInt("Rotation", rotation);
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        if (tag.contains("Item")) {
            item = ItemStack.fromTag(tag.getCompound("Item"));
        }
        this.rotation = tag.getInt("Rotation");
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        this.fromTag(null, tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return this.toTag(tag);
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
