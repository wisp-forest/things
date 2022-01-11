package com.glisco.things.items;

import io.wispforest.owo.ops.TextOps;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

public interface ExtendableTooltipProvider {

    Text TOOLTIP_HINT = new TranslatableText("text.things.tooltip_hint");

    String tooltipTranslationKey();

    default boolean hasExtendedTooltip() {
        return true;
    }

    default void tryAppend(List<Text> tooltip) {
        if (!hasExtendedTooltip()) return;

        if (Screen.hasShiftDown()) append(tooltip);
        else tooltip.add(TOOLTIP_HINT);
    }

    default void append(List<Text> tooltip) {
        var lines = WordUtils.wrap(I18n.translate(tooltipTranslationKey()), 35).split(System.lineSeparator());
        var texts = new ArrayList<Text>();

        for (var line : lines) {
            texts.add(TextOps.withColor(line, TextOps.color(Formatting.GRAY)));
        }

        tooltip.addAll(texts);
    }

}
