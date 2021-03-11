package com.glisco.things.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingRecipeRegistry.class)
public class BrewingRecipeRegistryMixin {

    @Inject(method = "isValidIngredient", at = @At("HEAD"), cancellable = true)
    private static void allowEnderPearl(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem().equals(Items.ENDER_PEARL)) cir.setReturnValue(true);
    }

}
