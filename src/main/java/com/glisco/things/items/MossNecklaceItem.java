package com.glisco.things.items;

import com.glisco.things.ThingsCommon;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.world.LightType;
import top.theillusivec4.curios.api.type.component.ICurio;

import java.util.Collections;
import java.util.List;

public class MossNecklaceItem extends ItemWithOptionalTooltip {

    public MossNecklaceItem() {
        super(new Settings().maxCount(1).group(ThingsCommon.THINGS_ITEMS));
    }

    @Override
    List<Text> getTooltipText() {
        return Collections.singletonList(new LiteralText("§7Grants Regeneration II when in light"));
    }

    public static class Curio implements ICurio {
        @Override
        public void curioTick(String identifier, int index, LivingEntity livingEntity) {
            if (!(livingEntity instanceof ServerPlayerEntity)) return;
            ServerPlayerEntity player = (ServerPlayerEntity) livingEntity;

            int daytime = (int) player.world.getTimeOfDay() % 24000;
            if (player.world.getLightLevel(LightType.BLOCK, player.getBlockPos()) > 7 ||
                    (player.world.getLightLevel(LightType.SKY, player.getBlockPos()) > 7 && (daytime > 23500 || daytime < 12500))) {
                if (player.getStatusEffect(StatusEffects.REGENERATION) != null) {
                    if (player.getStatusEffect(StatusEffects.REGENERATION).getDuration() < 10) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 610, 1, true, false, true));
                    }
                } else {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 610, 1, true, false, true));
                }
            }
        }

        @Override
        public void onUnequip(String identifier, int index, LivingEntity livingEntity) {
            if (!(livingEntity instanceof ServerPlayerEntity)) return;
            ServerPlayerEntity player = (ServerPlayerEntity) livingEntity;

            if (player.hasStatusEffect(StatusEffects.REGENERATION))
                player.removeStatusEffect(StatusEffects.REGENERATION);
        }
    }
}
