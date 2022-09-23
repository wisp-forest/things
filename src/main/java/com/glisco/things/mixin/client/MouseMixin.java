package com.glisco.things.mixin.client;

import com.glisco.things.Things;
import com.glisco.things.ThingsNetwork;
import com.glisco.things.items.trinkets.AgglomerationItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Mouse.class)
public class MouseMixin {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(D)V"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void beforePlayerScrollHotbar(long window, double horizontal, double vertical, CallbackInfo ci, double verticalAmount, int i) {
        ClientPlayerEntity player = this.client.player;

        if(!player.shouldCancelInteraction()) return;

        boolean scrollMainHandStack;

        if(player.getMainHandStack().getItem() instanceof AgglomerationItem && player.getMainHandStack().getOrCreateNbt().contains("Items")){
            scrollMainHandStack = true;
        } else if(player.getOffHandStack().getItem() instanceof AgglomerationItem && player.getOffHandStack().getOrCreateNbt().contains("Items")){
            scrollMainHandStack = false;
        } else {
            return;
        }

        ThingsNetwork.CHANNEL.clientHandle().send(new AgglomerationItem.ScrollHandStackTrinket(scrollMainHandStack));

        ci.cancel();
    }

}
