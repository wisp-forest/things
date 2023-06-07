package com.glisco.things.misc;

import com.glisco.things.Things;
import com.glisco.things.items.ThingsItems;
import com.glisco.things.items.trinkets.AgglomerationItem;
import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.function.Predicate;

public class AgglomerateRecipe extends SpecialCraftingRecipe {
    public AgglomerateRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        int totalItems = 0;
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.getStack(i).isEmpty()) continue;
            totalItems++;
        }
        if (totalItems != 3) return false;

        if (!matchOnce(inventory, stack -> stack.isOf(ThingsItems.EMPTY_AGGLOMERATION))) return false;

        ItemStack firstStack = matchOne(inventory, AgglomerateRecipe::isValidItem);
        if (firstStack == null) return false;

        var firstValidSlots = new ArrayList<SlotType>();

        TrinketsApi.getPlayerSlots().forEach((groupName, slotGroup) -> {
            slotGroup.getSlots().forEach((slotName, slotType) -> {
                if (firstStack.isIn(TagKey.of(RegistryKeys.ITEM, new Identifier("trinkets", groupName + "/" + slotName)))) {
                    firstValidSlots.add(slotType);
                }
            });
        });

        return matchOnce(inventory, stack -> {
            boolean anyCompatibleSlot = false;
            for (var slotType : firstValidSlots) {
                if (stack.isIn(TagKey.of(RegistryKeys.ITEM, new Identifier("trinkets", slotType.getGroup() + "/" + slotType.getName())))) {
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
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager drm) {
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

    private static boolean matchOnce(RecipeInputInventory inventory, Predicate<ItemStack> condition) {
        boolean found = false;

        for (int i = 0; i < inventory.size(); i++) {
            if (!condition.test(inventory.getStack(i))) continue;
            if (found) return false;

            found = true;
        }

        return found;
    }

    private static ItemStack matchOne(RecipeInputInventory inventory, Predicate<ItemStack> condition) {
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
