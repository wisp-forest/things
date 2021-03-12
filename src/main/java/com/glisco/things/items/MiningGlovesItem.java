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

public class MiningGlovesItem extends ItemWithOptionalTooltip {

    public MiningGlovesItem() {
        super(new Settings().maxCount(1).group(ThingsCommon.THINGS_ITEMS));
    }

    @Override
    List<Text> getTooltipText() {
        return Collections.singletonList(new LiteralText("§7Grants permanent Haste II"));
    }

    public static class Curio implements ICurio {
        @Override
        public void curioTick(String identifier, int index, LivingEntity livingEntity) {
            if (!(livingEntity instanceof ServerPlayerEntity)) return;
            ServerPlayerEntity player = (ServerPlayerEntity) livingEntity;

            player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 5, 1, true, false, true));
        }
    }
}