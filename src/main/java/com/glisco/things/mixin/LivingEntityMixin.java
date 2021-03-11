package com.glisco.things.mixin;

import com.glisco.things.ThingsCommon;
import com.glisco.things.items.ThingsItems;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "takeShieldHit", at = @At("HEAD"))
    public void onShieldHit(LivingEntity attacker, CallbackInfo ci) {

        LivingEntity user = (LivingEntity) (Object) this;

        if (user.getActiveItem().getItem() != Items.SHIELD) return;
        if (!EnchantmentHelper.fromTag(user.getActiveItem().getEnchantments()).containsKey(ThingsCommon.RETRIBUTION)) return;
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 40, 0));
    }

    @Inject(method = "blockedByShield", at = @At("RETURN"))
    public void onShieldBlock(DamageSource source, CallbackInfoReturnable<Boolean> cir) {

        if (!cir.getReturnValue()) return;

        LivingEntity user = (LivingEntity) (Object) this;

        if (user.getActiveItem().getItem() != Items.SHIELD) return;
        if (!EnchantmentHelper.fromTag(user.getActiveItem().getEnchantments()).containsKey(ThingsCommon.RETRIBUTION)) return;
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 40, 0));
    }

    @ModifyVariable(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z", ordinal = 1), ordinal = 1)
    public float waxGlandWater(float j) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity instanceof PlayerEntity)) return j;

        PlayerEntity player = (PlayerEntity) entity;

        if (!TrinketsApi.getTrinketsInventory(player).containsAny(Collections.singleton(ThingsItems.ENCHANTED_WAX_GLAND))) return j;

        return j * 10f;
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;updateVelocity(FLnet/minecraft/util/math/Vec3d;)V", ordinal = 1))
    public void waxGlandLava(LivingEntity livingEntity, float speed, Vec3d movementInput) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity instanceof PlayerEntity)) return;

        PlayerEntity player = (PlayerEntity) entity;

        if (TrinketsApi.getTrinketsInventory(player).containsAny(Collections.singleton(ThingsItems.ENCHANTED_WAX_GLAND)) && TrinketsApi.getTrinketsInventory(player).containsAny(Collections.singleton(ThingsItems.HADES_CRYSTAL))) {
            int depthStrider = EnchantmentHelper.getDepthStrider(player);
            player.updateVelocity(0.175f + 0.1f * depthStrider, movementInput);
        } else {
            player.updateVelocity(speed, movementInput);
        }
    }

    /*@Redirect(method = "getMovementSpeed(F)F", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;flyingSpeed:F", opcode = Opcodes.GETFIELD))
    public float airAgility(LivingEntity livingEntity) {
        return 0.05f;
    }*/

}
