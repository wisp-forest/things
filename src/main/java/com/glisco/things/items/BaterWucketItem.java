package com.glisco.things.items;

import com.glisco.things.ThingsCommon;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BaterWucketItem extends BucketItem {

    public BaterWucketItem() {
        super(Fluids.WATER, new Settings().group(ThingsCommon.THINGS_ITEMS).maxCount(1));
    }

    @Override
    public void onEmptied(@Nullable PlayerEntity player, World world, ItemStack stack, BlockPos pos) {
        super.onEmptied(player, world, stack, pos);
    }


    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
