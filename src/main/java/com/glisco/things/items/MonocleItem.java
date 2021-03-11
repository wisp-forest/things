package com.glisco.things.items;

import com.glisco.things.ThingsCommon;
import dev.emi.trinkets.api.Slots;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;

public class MonocleItem extends TrinketItemWithOptionalTooltip {

    public MonocleItem() {
        super(new Settings().maxCount(1).group(ThingsCommon.THINGS_ITEMS));
    }

    @Override
    public boolean canWearInSlot(String group, String slot) {
        return slot.equals(Slots.MASK);
    }

    @Override
    public void tick(PlayerEntity player, ItemStack stack) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 610, 0, true, false, true));
    }

    @Override
    public void onUnequip(PlayerEntity player, ItemStack stack) {
        player.removeStatusEffect(StatusEffects.NIGHT_VISION);
    }

    @Override
    List<Text> getTooltipText() {
        return Collections.singletonList(new LiteralText("§7Grants permanent Night Vision"));
    }
}
