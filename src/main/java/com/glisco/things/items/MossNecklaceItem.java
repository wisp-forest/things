package com.glisco.things.items;

import com.glisco.things.ThingsCommon;
import dev.emi.trinkets.api.Slots;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.world.LightType;

import java.util.Collections;
import java.util.List;

public class MossNecklaceItem extends TrinketItemWithOptionalTooltip {

    public MossNecklaceItem() {
        super(new Settings().maxCount(1).group(ThingsCommon.THINGS_ITEMS));
    }

    @Override
    public boolean canWearInSlot(String group, String slot) {
        return slot.equals(Slots.NECKLACE);
    }

    @Override
    public void tick(PlayerEntity player, ItemStack stack) {
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
    public void onUnequip(PlayerEntity player, ItemStack stack) {
        if (player.hasStatusEffect(StatusEffects.REGENERATION)) ;
        player.removeStatusEffect(StatusEffects.REGENERATION);
    }

    @Override
    List<Text> getTooltipText() {
        return Collections.singletonList(new LiteralText("§7Grants Regeneration II when in light"));
    }
}
