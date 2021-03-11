package com.glisco.things.items;

import com.glisco.things.ThingsCommon;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;

public class BaterWucketItem extends BucketItem {

    public BaterWucketItem() {
        super(Fluids.WATER, new Settings().group(ThingsCommon.THINGS_ITEMS).maxCount(1));
    }

    @Override
    protected ItemStack getEmptiedStack(ItemStack stack, PlayerEntity player) {
        return stack;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
