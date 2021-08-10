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
import net.minecraft.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "takeShieldHit", at = @At("HEAD"))
    public void onShieldHit(LivingEntity attacker, CallbackInfo ci) {

        LivingEntity user = (LivingEntity) (Object) this;

        if (!(user.getActiveItem().getItem() instanceof ShieldItem)) return;
        if (!EnchantmentHelper.fromNbt(user.getActiveItem().getEnchantments()).containsKey(ThingsCommon.RETRIBUTION)) return;
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 40, 0));
    }

    @Inject(method = "blockedByShield", at = @At("RETURN"))
    public void onShieldBlock(DamageSource source, CallbackInfoReturnable<Boolean> cir) {

        if (!cir.getReturnValue()) return;

        LivingEntity user = (LivingEntity) (Object) this;

        if (!(user.getActiveItem().getItem() instanceof ShieldItem)) return;
        if (!EnchantmentHelper.fromNbt(user.getActiveItem().getEnchantments()).containsKey(ThingsCommon.RETRIBUTION)) return;
        user.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 40, 0));
    }

    @ModifyVariable(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z", ordinal = 1), ordinal = 1)
    public float waxGlandWater(float j) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity instanceof PlayerEntity player)) return j;

        if (!TrinketsApi.getTrinketComponent(player).get().isEquipped(ThingsItems.ENCHANTED_WAX_GLAND)) return j;

        return j * ThingsCommon.CONFIG.waxGlandMultiplier;
    }

    @ModifyArg(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;updateVelocity(FLnet/minecraft/util/math/Vec3d;)V"), index = 0)
    public float waxGlandLava(float speed) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity instanceof PlayerEntity player)) return speed;

        if (TrinketsApi.getTrinketComponent(player).get().isEquipped(ThingsItems.ENCHANTED_WAX_GLAND) && TrinketsApi.getTrinketComponent(player).get().isEquipped(ThingsItems.HADES_CRYSTAL)) {
            int depthStrider = EnchantmentHelper.getDepthStrider(player);
            return 0.0175f * ThingsCommon.CONFIG.waxGlandMultiplier + 0.1f * depthStrider;
        }

        return speed;
    }

    /*@Redirect(method = "getMovementSpeed(F)F", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;flyingSpeed:F", opcode = Opcodes.GETFIELD))
    public float airAgility(LivingEntity livingEntity) {
        return 0.05f;
    }*/

}
