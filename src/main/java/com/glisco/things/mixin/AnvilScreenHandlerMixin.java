package com.glisco.things.mixin;

import com.glisco.things.items.ThingsItems;
import com.glisco.things.mixin.access.ForgingScreenHandlerAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.text.LiteralText;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {

    @Shadow
    @Final
    private Property levelCost;

    @Shadow
    private String newItemName;

    @Inject(method = "canTakeOutput", at = @At("HEAD"), cancellable = true)
    public void outputCheckOverride(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> cir) {
        ForgingScreenHandlerAccessor handler = (ForgingScreenHandlerAccessor) this;

        if (!handler.things$getInput().getStack(1).getItem().equals(ThingsItems.HARDENING_CATALYST)) return;

        cir.setReturnValue(levelCost.get() <= player.experienceLevel);
        cir.cancel();
    }

    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    public void setOutput(CallbackInfo ci) {
        ForgingScreenHandlerAccessor forgingHandler = (ForgingScreenHandlerAccessor) this;

        if (!forgingHandler.things$getInput().getStack(1).getItem().equals(ThingsItems.HARDENING_CATALYST)) return;
        if (forgingHandler.things$getInput().getStack(0).getItem().getMaxDamage() == 0) return;
        if (forgingHandler.things$getInput().getStack(0).getOrCreateNbt().getByte("Unbreakable") == (byte) 1) return;

        ItemStack newOutput = forgingHandler.things$getInput().getStack(0).copy();
        newOutput.getOrCreateNbt().putByte("Unbreakable", (byte) 1);

        if (!StringUtils.isBlank(newItemName)) {
            newOutput.setCustomName(new LiteralText(newItemName));
        } else {
            newOutput.removeCustomName();
        }

        forgingHandler.things$getOutput().setStack(0, newOutput);
        levelCost.set(30);

        ci.cancel();
    }
}
