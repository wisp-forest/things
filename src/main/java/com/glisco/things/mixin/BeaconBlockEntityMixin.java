package com.glisco.things.mixin;

import com.glisco.things.ThingsCommon;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityMixin {

    @Inject(method = "applyPlayerEffects", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void nerfHaste(World world, BlockPos pos, int beaconLevel, StatusEffect primaryEffect, StatusEffect secondaryEffect, CallbackInfo ci, double d, int i, int j, Box box, List<PlayerEntity> list) {
        if (!ThingsCommon.CONFIG.nerfBeaconsWithMomentum || secondaryEffect != StatusEffects.HASTE) return;
        list.removeIf(playerEntity -> playerEntity.hasStatusEffect(ThingsCommon.MOMENTUM));
    }

}
