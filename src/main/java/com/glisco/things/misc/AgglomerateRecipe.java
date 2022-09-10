package com.glisco.things.misc;

import com.glisco.things.Things;
import com.glisco.things.items.ThingsItems;
import com.glisco.things.items.trinkets.AgglomerationItem;
import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.function.Predicate;

public class AgglomerateRecipe extends SpecialCraftingRecipe {
    public AgglomerateRecipe(Identifier id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        if (!matchOnce(inventory, stack -> stack.isOf(ThingsItems.EMPTY_AGGLOMERATION))) return false;

        ItemStack firstStack = matchOne(inventory, AgglomerateRecipe::isValidItem);
        if (firstStack == null) return false;

        var firstValidSlots = new ArrayList<SlotType>();

        TrinketsApi.getPlayerSlots().forEach((groupName, slotGroup) -> {
            slotGroup.getSlots().forEach((slotName, slotType) -> {
                if (firstStack.isIn(TagKey.of(Registry.ITEM_KEY, new Identifier("trinkets", groupName + "/" + slotName)))) {
                    firstValidSlots.add(slotType);
                }
            });
        });

        return matchOnce(inventory, stack -> {
            boolean anyCompatibleSlot = false;
            for (var slotType : firstValidSlots) {
                if (stack.isIn(TagKey.of(Registry.ITEM_KEY, new Identifier("trinkets", slotType.getGroup() + "/" + slotType.getName())))) {
                    anyCompatibleSlot = true;
                }
            }

            return anyCompatibleSlot && !ItemStack.areItemsEqual(stack, firstStack) && isValidItem(stack);
        });
    }

    private static boolean isValidItem(ItemStack stack) {
        return !stack.isEmpty() && !stack.isOf(ThingsItems.EMPTY_AGGLOMERATION) && !stack.isOf(ThingsItems.AGGLOMERATION)
                && TrinketsApi.getTrinket(stack.getItem()) != TrinketsApi.getDefaultTrinket()
                && !stack.isIn(Things.AGGLOMERATION_BLACKLIST);
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        ItemStack firstTrinket = matchOne(inventory, stack -> !stack.isEmpty() && !stack.isOf(ThingsItems.EMPTY_AGGLOMERATION));
        ItemStack secondTrinket = matchOne(inventory, stack -> !stack.isEmpty() && stack != firstTrinket && !stack.isOf(ThingsItems.EMPTY_AGGLOMERATION));

        return AgglomerationItem.createStack(firstTrinket, secondTrinket);
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    private static boolean matchOnce(CraftingInventory inventory, Predicate<ItemStack> condition) {
        boolean found = false;

        for (int i = 0; i < inventory.size(); i++) {
            if (!condition.test(inventory.getStack(i))) continue;
            if (found) return false;

            found = true;
        }

        return found;
    }

    private static ItemStack matchOne(CraftingInventory inventory, Predicate<ItemStack> condition) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            if (!condition.test(stack)) continue;

            return stack;
        }

        return null;
    }

    public static class Serializer extends SpecialRecipeSerializer<AgglomerateRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        private Serializer() {
            super(AgglomerateRecipe::new);
        }
    }
}
