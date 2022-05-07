package com.glisco.things.mixin.trinkets;

import com.glisco.things.items.ThingsItems;
import com.glisco.things.items.trinkets.AgglomerationItem;
import dev.emi.trinkets.api.LivingEntityTrinketComponent;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Predicate;

@Mixin(LivingEntityTrinketComponent.class)
public class LivingEntityTrinketComponentMixin {
    @ModifyVariable(method = "isEquipped", at = @At("HEAD"), argsOnly = true, remap = false)
    private Predicate<ItemStack> checkAgglomerationsAsWell(Predicate<ItemStack> oldPredicate) {
        return oldPredicate.or(stack -> stack.isOf(ThingsItems.AGGLOMERATION) && AgglomerationItem.hasStack(stack, oldPredicate));
    }
}
