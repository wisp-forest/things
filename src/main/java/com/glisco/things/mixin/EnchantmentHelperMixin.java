package com.glisco.things.mixin;

import com.glisco.things.enchantments.RetributionEnchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(method = "getPossibleEntries", at = @At("RETURN"))
    private static void removeInvalidEnchantments(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        cir.getReturnValue().removeIf(enchantmentLevelEntry ->
                enchantmentLevelEntry.enchantment instanceof RetributionEnchantment && !enchantmentLevelEntry.enchantment.isAcceptableItem(stack));
    }

}
