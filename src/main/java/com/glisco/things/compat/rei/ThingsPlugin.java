package com.glisco.things.compat.rei;

import com.glisco.things.client.DisplacementTomeScreen;
import com.glisco.things.items.ThingsItems;
import com.glisco.things.items.trinkets.SocksItem;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.displays.brewing.DefaultBrewingDisplay;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.ShapelessRecipe;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ThingsPlugin implements REIClientPlugin {

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void registerDisplays(DisplayRegistry registry) {
        final var awkwardPotion = new ItemStack(Items.POTION);
        final var swiftnessPotion = new ItemStack(Items.POTION);

        PotionUtil.setPotion(awkwardPotion, Potions.AWKWARD);
        PotionUtil.setPotion(swiftnessPotion, Potions.STRONG_SWIFTNESS);

        registry.add(new DefaultBrewingDisplay(EntryIngredients.of(awkwardPotion),
                EntryIngredients.of(Items.ENDER_PEARL), EntryStacks.of(ThingsItems.RECALL_POTION)));

        final var averageSocks = SocksItem.create(0, false);

        registry.add(new SockDisplay(averageSocks, new ItemStack(ThingsItems.RABBIT_FOOT_CHARM), SocksItem.create(0, true), true));
        registry.add(new SockDisplay(averageSocks, swiftnessPotion, SocksItem.create(1, false), false));
        registry.add(new SockDisplay(SocksItem.create(1, false), swiftnessPotion, SocksItem.create(2, false), false));
    }

    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        zones.register(DisplacementTomeScreen.class, screen -> {
            if (!screen.isNameFieldVisible()) return Collections.emptyList();

            int x = screen.getRootX();
            int y = screen.getRootY();

            return Collections.singletonList(new Rectangle(x + 160, y + 50, 130, 60));
        });
    }

    @Override
    public void registerEntries(EntryRegistry registry) {
        registry.removeEntry(EntryStacks.of(ThingsItems.AGGLOMERATION));
    }

    private static class SockDisplay extends DefaultCraftingDisplay<ShapelessRecipe> {

        public SockDisplay(ItemStack socc, ItemStack addition, ItemStack result, boolean compound) {
            super(List.of(EntryIngredients.of(socc),
                            EntryIngredients.of(compound ? ThingsItems.GLEAMING_COMPOUND : ThingsItems.GLEAMING_POWDER),
                            EntryIngredients.of(addition)),
                    List.of(EntryIngredients.of(result)),
                    Optional.empty());
        }

        @Override
        public int getWidth() {
            return 2;
        }

        @Override
        public int getHeight() {
            return 2;
        }
    }
}
