package com.glisco.things.items;

import com.glisco.things.ThingsCommon;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import top.theillusivec4.curios.api.type.component.ICurio;

import java.util.Collections;
import java.util.List;

public class MonocleItem extends ItemWithOptionalTooltip {

    public MonocleItem() {
        super(new Settings().maxCount(1).group(ThingsCommon.THINGS_ITEMS));
    }

    @Override
    List<Text> getTooltipText() {
        return Collections.singletonList(new LiteralText("§7Grants permanent Night Vision"));
    }

    public static class Curio implements ICurio {
        @Override
        public void curioTick(String identifier, int index, LivingEntity livingEntity) {
            if (!(livingEntity instanceof ServerPlayerEntity)) return;
            ServerPlayerEntity player = (ServerPlayerEntity) livingEntity;

            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 610, 0, true, false, true));
        }

        @Override
        public void onUnequip(String identifier, int index, LivingEntity livingEntity) {
            if (!(livingEntity instanceof ServerPlayerEntity)) return;
            ServerPlayerEntity player = (ServerPlayerEntity) livingEntity;

            player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
    }
}
