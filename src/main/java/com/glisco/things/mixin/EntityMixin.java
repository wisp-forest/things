package com.glisco.things.mixin;

import com.glisco.things.Things;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public class EntityMixin {

    @Redirect(at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;stepHeight:F", opcode = Opcodes.GETFIELD), method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;")
    public float getStepHeight(Entity e) {
        if (!(e instanceof PlayerEntity player)) return e.stepHeight;
        if (!Things.SOCK_DATA.get(player).jumpySocksEquipped) return e.stepHeight;
        return e.stepHeight + .5f;
    }

}
