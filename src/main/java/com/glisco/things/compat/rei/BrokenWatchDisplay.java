package com.glisco.things.compat.rei;

import com.glisco.things.items.ThingsItems;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.Items;

import java.util.List;

public class BrokenWatchDisplay implements Display {

    private final List<EntryIngredient> inputs = List.of(
            EntryIngredients.of(Items.LEATHER),
            EntryIngredients.of(Items.CLOCK),
            EntryIngredients.of(ThingsItems.GLEAMING_COMPOUND)
    );

    private final List<EntryIngredient> outputs = List.of(EntryIngredients.of(ThingsItems.BROKEN_WATCH));

    @Override
    public List<EntryIngredient> getInputEntries() {
        return this.inputs;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return this.outputs;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ThingsPlugin.BROKEN_WATCH_CATEGORY;
    }
}
