package com.glisco.things.items;

import com.glisco.things.ThingsCommon;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class TrinketItemWithOptionalTooltip extends TrinketItem {

    public TrinketItemWithOptionalTooltip(Settings settings) {
        super(settings);
    }

    abstract List<Text> getTooltipText();

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(ThingsCommon.isPatchouliLoaded()) return;
        if (Screen.hasShiftDown()) {
            tooltip.addAll(getTooltipText());
        } else {
            tooltip.add(ItemWithOptionalTooltip.TOOLTIP);
        }
    }
}
