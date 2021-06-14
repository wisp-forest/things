package com.glisco.things.items;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class AppleTrinket implements Trinket {

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!(entity instanceof ServerPlayerEntity player)) return;

        if (player.getHungerManager().getFoodLevel() > 16) return;

        if (!TrinketsApi.getTrinketComponent(player).get().isEquipped(Items.APPLE)) return;

        player.getHungerManager().eat(Items.APPLE, stack);
        stack.decrement(1);

        player.playSound(SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 1, 1);
    }

}
