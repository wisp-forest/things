package com.glisco.things.items;

import com.glisco.things.ThingsCommon;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
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
    List<Text> getTooltipText() {
        return TOOLTIP;
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!(entity instanceof ServerPlayerEntity player)) return;

        player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 5, 0, true, false, true));
        if (player.isOnFire()) player.setFireTicks(0);
    }

}
