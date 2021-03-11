package com.glisco.things.items;

import com.glisco.things.ThingsCommon;
import dev.emi.trinkets.api.Slots;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnchantedWaxGlandItem extends TrinketItemWithOptionalTooltip {

    private static final List<Text> TOOLTIP;

    static {
        TOOLTIP = new ArrayList<>();
        TOOLTIP.add(new LiteralText("§7Makes you float in water."));
        TOOLTIP.add(new LiteralText("§7And apparently also really fast"));
    }

    public EnchantedWaxGlandItem() {
        super(new Settings().group(ThingsCommon.THINGS_ITEMS).maxCount(1));
    }

    @Override
    public boolean canWearInSlot(String group, String slot) {
        return slot.equals(Slots.AGLET);
    }

    @Override
    public void tick(PlayerEntity player, ItemStack stack) {
        if (player.isTouchingWater()) {
            player.addVelocity(0, 0.005, 0);
        } else if (player.isInLava() && TrinketsApi.getTrinketsInventory(player).containsAny(Collections.singleton(ThingsItems.HADES_CRYSTAL))) {
            player.addVelocity(0, 0.02, 0);
        }
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    List<Text> getTooltipText() {
        return TOOLTIP;
    }
}
