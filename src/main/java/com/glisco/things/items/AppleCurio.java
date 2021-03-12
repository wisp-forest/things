package com.glisco.things.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.component.ICurio;

public class AppleCurio implements ICurio {

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity) {
        if (!(livingEntity instanceof ServerPlayerEntity)) return;
        ServerPlayerEntity player = (ServerPlayerEntity) livingEntity;

        if (player.getHungerManager().getFoodLevel() > 16) return;

        if (!CuriosApi.getCuriosHelper().findEquippedCurio(Items.APPLE, player).isPresent()) return;
        ItemStack apples = CuriosApi.getCuriosHelper().findEquippedCurio(Items.APPLE, player).get().getRight();

        player.getHungerManager().eat(Items.APPLE, apples);
        apples.decrement(1);

        player.playSound(SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 1, 1);
    }
}
