package com.glisco.things.compat.rei;

import com.glisco.things.items.ThingsItems;
import io.wispforest.owo.compat.rei.ReiUIAdapter;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;

import java.util.List;

public class BrokenWatchCategory implements DisplayCategory<BrokenWatchDisplay> {

    @Override
    public List<Widget> setupDisplay(BrokenWatchDisplay display, Rectangle bounds) {
        var adapter = new ReiUIAdapter<>(bounds, Containers::stack);

        var root = adapter.rootComponent();
        root.horizontalAlignment(HorizontalAlignment.CENTER).verticalAlignment(VerticalAlignment.CENTER);

        root.child(adapter.wrap(Widgets.createRecipeBase(bounds)));
        root.child(Containers.horizontalFlow(Sizing.content(), Sizing.content())
                .child(Containers.verticalFlow(Sizing.content(), Sizing.content())
                        .child(adapter.wrap(Widgets::createSlot, slot -> slot.entries(display.getInputEntries().get(0))))
                        .child(adapter.wrap(Widgets::createSlot, slot -> slot.entries(display.getInputEntries().get(1))))
                        .child(adapter.wrap(Widgets::createSlot, slot -> slot.entries(display.getInputEntries().get(2))))
                        .gap(5))
                .child(adapter.wrap(Widgets.createArrow(ReiUIAdapter.LAYOUT)))
                .child(Containers.verticalFlow(Sizing.content(), Sizing.content())
                        .child(Components.block(Blocks.PISTON.getDefaultState().with(PistonBlock.FACING, Direction.DOWN)).sizing(Sizing.fixed(32)))
                        .child(Components.block(Blocks.SMOOTH_STONE.getDefaultState()).sizing(Sizing.fixed(32)))
                        .gap(8))
                .child(adapter.wrap(Widgets.createArrow(ReiUIAdapter.LAYOUT)))
                .child(adapter.wrap(Widgets::createSlot, slot -> slot.entries(display.getOutputEntries().get(0))))
                .gap(5)
                .verticalAlignment(VerticalAlignment.CENTER));

        adapter.prepare();
        return List.of(adapter);
    }

    @Override
    public int getDisplayHeight() {
        return 80;
    }

    @Override
    public CategoryIdentifier<? extends BrokenWatchDisplay> getCategoryIdentifier() {
        return ThingsPlugin.BROKEN_WATCH_CATEGORY;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("category.things.broken_watch");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ThingsItems.BROKEN_WATCH);
    }
}
