package com.glisco.things.mixin.trinkets;

import com.glisco.things.items.ThingsItems;
import com.glisco.things.items.trinkets.AgglomerationItem;
import dev.emi.trinkets.api.LivingEntityTrinketComponent;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

@Mixin(LivingEntityTrinketComponent.class)
public class LivingEntityTrinketComponentMixin {
    @ModifyVariable(method = "isEquipped", at = @At("HEAD"), argsOnly = true, remap = false)
    private Predicate<ItemStack> checkAgglomerationsAsWell(Predicate<ItemStack> oldPredicate) {
        return oldPredicate.or(stack -> stack.isOf(ThingsItems.AGGLOMERATION) && AgglomerationItem.hasStack(stack, oldPredicate));
    }

    @ModifyArg(method = "getEquipped", at = @At(value = "INVOKE", target = "Ldev/emi/trinkets/api/LivingEntityTrinketComponent;forEach(Ljava/util/function/BiConsumer;)V"), remap = false)
    private BiConsumer<SlotReference, ItemStack> iterateThroughAgglomerations(BiConsumer<SlotReference, ItemStack> consumer) {
        return consumer.andThen((slot, stack) -> {
            if (stack.isOf(ThingsItems.AGGLOMERATION)) {
                AgglomerationItem.getStacks(stack).forEach(subStack -> consumer.accept(slot, subStack));
            }
        });
    }
}
