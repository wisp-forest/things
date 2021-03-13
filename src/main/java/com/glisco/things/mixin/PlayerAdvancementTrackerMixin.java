package com.glisco.things.mixin;

import com.glisco.things.ThingsCommon;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {

    @Shadow
    private ServerPlayerEntity owner;

    @Inject(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/AdvancementRewards;apply(Lnet/minecraft/server/network/ServerPlayerEntity;)V"))
    public void givePatchouli(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if (!ThingsCommon.isPatchouliLoaded()) return;
        if (!advancement.getId().equals(new Identifier("things", "root"))) return;

        ItemStack book = new ItemStack(Registry.ITEM.get(new Identifier("patchouli", "guide_book")));
        book.getOrCreateTag().putString("patchouli:book", "things:things_guide");

        owner.inventory.offerOrDrop(owner.world, book);

    }

}
