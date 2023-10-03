package com.glisco.things.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Optional;

public interface ExtendableTooltipProvider {

    Text TOOLTIP_HINT = Text.translatable("text.things.tooltip_hint");

    String tooltipTranslationKey();

    @Environment(EnvType.CLIENT)
    default boolean hasExtendedTooltip() {
        return true;
    }

    @Environment(EnvType.CLIENT)
    default void tryAppend(List<Text> tooltip) {
        if (!this.hasExtendedTooltip()) return;

        if (Screen.hasShiftDown()) this.append(tooltip);
        else tooltip.add(TOOLTIP_HINT);
    }

    @Environment(EnvType.CLIENT)
    default void append(List<Text> tooltip) {
        this.appendWrapped(tooltip, Text.translatable(this.tooltipTranslationKey()));
    }

    @Environment(EnvType.CLIENT)
    default void appendWrapped(List<Text> tooltip, Text toAppend) {
        MinecraftClient.getInstance().textRenderer.getTextHandler().wrapLines(toAppend, 220, Style.EMPTY.withFormatting(Formatting.GRAY))
                .stream()
                .map(VisitableTextContent::new)
                .map(MutableText::of)
                .forEach(tooltip::add);
    }

    record VisitableTextContent(StringVisitable content) implements TextContent {
        @Override
        public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> visitor, Style style) {
            return this.content.visit(visitor, style);
        }

        @Override
        public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
            return this.content.visit(visitor);
        }
    }
}
