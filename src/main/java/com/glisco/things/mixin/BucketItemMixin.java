package com.glisco.things.mixin;

import com.glisco.things.items.ThingsItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public class BucketItemMixin {

    @Inject(method = "getEmptiedStack", at = @At("HEAD"), cancellable = true)
    private static void preserverBaterWucket(ItemStack stack, PlayerEntity player, CallbackInfoReturnable<ItemStack> cir){
        if(!stack.isOf(ThingsItems.BATER_WUCKET)) return;
        cir.setReturnValue(stack);
    }

}
