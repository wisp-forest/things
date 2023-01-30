package com.glisco.things.items.generic;

import com.glisco.things.Things;
import com.glisco.things.items.ThingsItems;
import com.glisco.things.misc.BaterWucketStorage;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.world.event.GameEvent;

public class BaterWucketItem extends BucketItem {

    public BaterWucketItem() {
        super(Fluids.WATER, new OwoItemSettings().group(Things.THINGS_GROUP).maxCount(1));

        //noinspection UnstableApiUsage
        FluidStorage.ITEM.registerForItems((stack, ctx) -> new BaterWucketStorage(), this);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    public static void registerCauldronBehavior() {
        CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.put(ThingsItems.BATER_WUCKET, (state, world, pos, player, hand, stack) -> {
            if (world.isClient) return ActionResult.SUCCESS;

            world.setBlockState(pos, Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 3));
            player.incrementStat(Stats.FILL_CAULDRON);
            player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1f, 1f);
            world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);

            return ActionResult.CONSUME;
        });
    }
}
