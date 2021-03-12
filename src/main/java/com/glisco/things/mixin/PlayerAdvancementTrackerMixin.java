package com.glisco.things.mixin;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {

    @Inject(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/AdvancementRewards;apply(Lnet/minecraft/server/network/ServerPlayerEntity;)V"))
    public void givePatchouli(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir){

    }

}
