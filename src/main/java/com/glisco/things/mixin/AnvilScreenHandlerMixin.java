package com.glisco.things.mixin;

import com.glisco.things.items.ThingsItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.text.LiteralText;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {

    @Inject(method = "canTakeOutput", at = @At("HEAD"), cancellable = true)
    public void outputCheckOverride(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> cir) {

        ForgingScreenHandlerAccessor handler = (ForgingScreenHandlerAccessor) (Object) this;

        if (!handler.getInput().getStack(1).getItem().equals(ThingsItems.HARDENING_CRYSTAL)) return;

        cir.setReturnValue(((AnvilScreenHandlerAccessor) (Object) this).getLevelCost().get() <= player.experienceLevel);
        cir.cancel();
    }

    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    public void setOutput(CallbackInfo ci) {

        ForgingScreenHandlerAccessor forgingHandler = (ForgingScreenHandlerAccessor) (Object) this;
        AnvilScreenHandlerAccessor anvilHandler = (AnvilScreenHandlerAccessor) (Object) this;

        if (!forgingHandler.getInput().getStack(1).getItem().equals(ThingsItems.HARDENING_CRYSTAL)) return;
        if (forgingHandler.getInput().getStack(0).getItem().getMaxDamage() == 0) return;
        if (forgingHandler.getInput().getStack(0).getOrCreateTag().getByte("Unbreakable") == (byte) 1) return;

        ItemStack newOutput = forgingHandler.getInput().getStack(0).copy();
        newOutput.getOrCreateTag().putByte("Unbreakable", (byte) 1);

        if (!StringUtils.isBlank(anvilHandler.getNewItemName())) {
            newOutput.setCustomName(new LiteralText(anvilHandler.getNewItemName()));
        } else {
            newOutput.removeCustomName();
        }

        forgingHandler.getOutput().setStack(0, newOutput);
        anvilHandler.getLevelCost().set(30);

        ci.cancel();
    }
}
