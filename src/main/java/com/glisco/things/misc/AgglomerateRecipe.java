package com.glisco.things.misc;

import com.glisco.things.items.ThingsItems;
import com.glisco.things.items.trinkets.AgglomerationItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class AgglomerateRecipe extends SpecialCraftingRecipe {
    public AgglomerateRecipe(Identifier id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        if (!matchOnce(inventory, stack -> stack.isOf(ThingsItems.EMPTY_AGGLOMERATION))) return false;

        ItemStack firstTrinket = matchOne(inventory, AgglomerateRecipe::isValidItem);

        if (firstTrinket == null) return false;

        return matchOnce(inventory, stack -> !ItemStack.areItemsEqual(stack, firstTrinket) && isValidItem(stack));

    }

    private static boolean isValidItem(ItemStack stack) {
        return !stack.isEmpty() && !stack.isOf(ThingsItems.EMPTY_AGGLOMERATION) && !stack.isOf(ThingsItems.AGGLOMERATION);
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
