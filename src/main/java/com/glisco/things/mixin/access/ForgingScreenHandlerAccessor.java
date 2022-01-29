package com.glisco.things.mixin.access;

import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ForgingScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ForgingScreenHandler.class)
public interface ForgingScreenHandlerAccessor {

    @Accessor("input")
    Inventory things$getInput();

    @Accessor("output")
    CraftingResultInventory things$getOutput();

}
