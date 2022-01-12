package com.glisco.things.mixin;

import com.glisco.things.Things;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Shadow public float stepHeight;

    @Unique private float things$prevStepHeight = -1;

    @Inject(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"))
    private void boostStepHeight(Vec3d movement, CallbackInfoReturnable<Vec3d> cir) {
        if (!((Object) this instanceof PlayerEntity player)) return;
        if (!Things.SOCK_DATA.get(player).jumpySocksEquipped) return;

        this.things$prevStepHeight = this.stepHeight;
        this.stepHeight = this.stepHeight + .45f;
    }

    @Inject(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At("RETURN"))
    private void resetStepHeight(Vec3d movement, CallbackInfoReturnable<Vec3d> cir) {
        if (this.things$prevStepHeight == -1) return;

        this.stepHeight = this.things$prevStepHeight;
        this.things$prevStepHeight = -1;
    }

}
