package com.glisco.things.misc;

import com.glisco.things.items.ThingsItems;
import com.glisco.things.items.trinkets.SocksItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class JumpySocksRecipe extends SpecialCraftingRecipe {

    public JumpySocksRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        if (!matchOnce(inventory, stack -> stack.isOf(ThingsItems.GLEAMING_COMPOUND))) return false;
        if (!matchOnce(inventory, stack -> stack.isOf(ThingsItems.RABBIT_FOOT_CHARM))) return false;

        return matchOnce(inventory, stack -> stack.isOf(ThingsItems.SOCKS) && !stack.getOr(SocksItem.JUMPY_KEY, false));
    }

    @Override
    public ItemStack craft(CraftingInventory inventory, DynamicRegistryManager drm) {
        ItemStack socc = null;

        for (int i = 0; i < inventory.size(); i++) {
            final var stack = inventory.getStack(i);
            if (!stack.isOf(ThingsItems.SOCKS)) continue;

            socc = stack.copy();
            break;
        }

        if (socc == null) return ItemStack.EMPTY;

        final var soccNbt = socc.getOrCreateNbt();
        soccNbt.put(SocksItem.JUMPY_KEY, true);

        return socc;
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

    @Override
    public boolean fits(int width, int height) {
        return width > 1 && height > 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Type implements RecipeType<JumpySocksRecipe> {
        public static final Type INSTANCE = new Type();
    }

    public static class Serializer extends SpecialRecipeSerializer<JumpySocksRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        private Serializer() {
            super(JumpySocksRecipe::new);
        }
    }
}
