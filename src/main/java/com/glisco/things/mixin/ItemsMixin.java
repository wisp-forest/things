package com.glisco.things.mixin;

import com.glisco.things.items.AppleItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Items.class)
public class ItemsMixin {

    //@Redirect(method = "<clinit>", at = @At(value = "NEW", target = "Lnet/minecraft/item/Item"))
    @Inject(method = "register(Lnet/minecraft/util/Identifier;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", at = @At("HEAD"), cancellable = true)
    private static void initApple(Identifier id, Item item, CallbackInfoReturnable<Item> cir) {
        if (!id.equals(new Identifier("apple"))) return;

        cir.setReturnValue(Registry.register(Registry.ITEM, id, new AppleItem()));
    }

}
