package com.glisco.things.mixin;

import com.glisco.things.items.ThingsItems;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandBlockEntityMixin {

    @Inject(method = "canCraft", at = @At("HEAD"), cancellable = true)
    public void checkCraft(CallbackInfoReturnable<Boolean> cir) {
        BrewingStandBlockEntityAccessor stand = (BrewingStandBlockEntityAccessor) (Object) this;

        if (!stand.getInventory().get(3).getItem().equals(Items.ENDER_PEARL)) return;
        for (int i = 0; i < 3; i++) {
            if (!(stand.getInventory().get(i).getItem() instanceof PotionItem)) continue;
            if (!PotionUtil.getPotion(stand.getInventory().get(i)).equals(Potions.AWKWARD)) continue;
            cir.setReturnValue(true);
            return;
        }
    }

    @Inject(method = "craft", at = @At("HEAD"), cancellable = true)
    public void doCraft(CallbackInfo ci) {
        BrewingStandBlockEntityAccessor stand = (BrewingStandBlockEntityAccessor) (Object) this;
        ItemStack ingredient = stand.getInventory().get(3);

        if (!ingredient.getItem().equals(Items.ENDER_PEARL)) return;
        ingredient.decrement(1);

        for (int i = 0; i < 3; i++) {
            if (!(stand.getInventory().get(i).getItem() instanceof PotionItem)) continue;
            if (!PotionUtil.getPotion(stand.getInventory().get(i)).equals(Potions.AWKWARD)) continue;
            stand.getInventory().set(i, new ItemStack(ThingsItems.RECALL_POTION));
        }

        ((BlockEntity) (Object) this).getWorld().syncWorldEvent(1035, ((BlockEntity) (Object) this).getPos(), 0);
        ci.cancel();
    }

}
