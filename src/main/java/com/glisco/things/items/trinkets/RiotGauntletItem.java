package com.glisco.things.items.trinkets;

import com.glisco.things.Things;
import com.glisco.things.items.TrinketItemWithOptionalTooltip;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class RiotGauntletItem extends TrinketItemWithOptionalTooltip {

    public RiotGauntletItem() {
        super(new Settings().maxCount(1).group(Things.THINGS_GROUP));
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!(entity instanceof ServerPlayerEntity player)) return;

        player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 5,
                Things.CONFIG.effectLevels.riotGauntletStrength - 1, true, false, true));
    }

}