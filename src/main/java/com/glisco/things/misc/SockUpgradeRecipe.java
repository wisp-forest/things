package com.glisco.things.misc;

import com.glisco.things.items.ThingsItems;
import com.glisco.things.items.trinkets.SocksItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class SockUpgradeRecipe extends SpecialCraftingRecipe {

    public SockUpgradeRecipe(Identifier id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        if (!matchOnce(inventory, stack -> stack.isOf(ThingsItems.GLEAMING_POWDER))) return false;
        if (!matchOnce(inventory, stack -> PotionUtil.getPotion(stack) == Potions.STRONG_SWIFTNESS)) return false;

        return matchOnce(inventory, stack -> stack.isOf(ThingsItems.SOCKS) && stack.getOrCreateNbt().getInt(SocksItem.SPEED_KEY) < 2);
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        ItemStack socc = null;

        for (int i = 0; i < inventory.size(); i++) {
            final var stack = inventory.getStack(i);
            if (!stack.isOf(ThingsItems.SOCKS)) continue;

            socc = stack.copy();
            break;
        }

        if (socc == null) return ItemStack.EMPTY;

        final var soccNbt = socc.getOrCreateNbt();
        soccNbt.putInt(SocksItem.SPEED_KEY, soccNbt.getInt(SocksItem.SPEED_KEY) + 1);

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

    public static class Type implements RecipeType<SockUpgradeRecipe> {
        public static final Type INSTANCE = new Type();
    }

    public static class Serializer extends SpecialRecipeSerializer<SockUpgradeRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        private Serializer() {
            super(SockUpgradeRecipe::new);
        }
    }
}
