package com.glisco.things.mixin;

import com.glisco.things.items.ContainerKeyItem;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ContainerLock.class)
public class ContainerLockMixin {

    @Shadow @Final private String key;

    @Inject(method = "canOpen", at = @At("HEAD"), cancellable = true)
    public void checkOpen(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!(stack.getItem() instanceof ContainerKeyItem)) return;
        if (String.valueOf(stack.getTag().getInt("Lock")).equals(key)) {
            cir.setReturnValue(true);
        }
    }
}
