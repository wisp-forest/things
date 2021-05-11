package com.glisco.things.mixin;

import com.glisco.things.ThingsCommon;
import com.glisco.things.items.ThingsItems;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;

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

        if (!CuriosApi.getCuriosHelper().findEquippedCurio(ThingsItems.ENCHANTED_WAX_GLAND, player).isPresent()) return j;

        return j * 10f;
    }

    @ModifyArg(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;updateVelocity(FLnet/minecraft/util/math/Vec3d;)V"), index = 0)
    public float waxGlandLava(float speed) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity instanceof PlayerEntity)) return speed;

        PlayerEntity player = (PlayerEntity) entity;

        if (CuriosApi.getCuriosHelper().findEquippedCurio(ThingsItems.ENCHANTED_WAX_GLAND, player).isPresent() && CuriosApi.getCuriosHelper().findEquippedCurio(ThingsItems.HADES_CRYSTAL, player).isPresent()) {
            int depthStrider = EnchantmentHelper.getDepthStrider(player);
            return 0.175f + 0.1f * depthStrider;
        }

        return speed;
    }

    /*@Redirect(method = "getMovementSpeed(F)F", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;flyingSpeed:F", opcode = Opcodes.GETFIELD))
    public float airAgility(LivingEntity livingEntity) {
        return 0.05f;
    }*/

}
