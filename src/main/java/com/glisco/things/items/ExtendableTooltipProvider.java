package com.glisco.things.items;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.List;

public interface ExtendableTooltipProvider {

    Text TOOLTIP_HINT = new LiteralText("§7Hold §6SHIFT §7for info");

    List<Text> getExtendedTooltip();

    default void append(List<Text> tooltip) {
        if (Screen.hasShiftDown()) tooltip.addAll(getExtendedTooltip());
        else tooltip.add(TOOLTIP_HINT);
    }

}
