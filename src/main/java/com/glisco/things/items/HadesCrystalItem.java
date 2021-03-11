package com.glisco.things.items;

import com.glisco.things.ThingsCommon;
import dev.emi.trinkets.api.Slots;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class HadesCrystalItem extends TrinketItemWithOptionalTooltip {

    private static final List<Text> TOOLTIP;

    static {
        TOOLTIP = new ArrayList<>();
        TOOLTIP.add(new LiteralText("§7Grants permanent Fire Resistance"));
        TOOLTIP.add(new LiteralText("§7Wear together with a §6Wax Gland §7for extra awesomeness"));
    }

    public HadesCrystalItem() {
        super(new Settings().group(ThingsCommon.THINGS_ITEMS).maxCount(1));
    }

    @Override
    public boolean canWearInSlot(String group, String slot) {
        return slot.equals(Slots.NECKLACE);
    }

    @Override
    public void tick(PlayerEntity player, ItemStack stack) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 5, 0, true, false, true));
        if (player.isOnFire()) player.setFireTicks(0);
    }

    @Override
    List<Text> getTooltipText() {
        return TOOLTIP;
    }
}
