package com.glisco.things.mixin.access;

import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public interface ItemAccessor {

    @Accessor("recipeRemainder")
    @Mutable
    void things$setRecipeRemainder(Item recipeRemainder);

}
