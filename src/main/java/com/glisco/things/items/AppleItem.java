package com.glisco.things.items;

import dev.emi.trinkets.api.SlotGroups;
import dev.emi.trinkets.api.Slots;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class AppleItem extends TrinketItem {

    public AppleItem() {
        super(new Item.Settings().group(ItemGroup.FOOD).food(FoodComponents.APPLE));
    }

    @Override
    public boolean canWearInSlot(String group, String slot) {
        return slot.equals(Slots.MASK);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (user.canConsume(this.getFoodComponent().isAlwaysEdible())) {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(itemStack);
        } else {
            return TypedActionResult.fail(itemStack);
        }
    }

    @Override
    public void tick(PlayerEntity player, ItemStack stack) {
        if (player.getHungerManager().getFoodLevel() > 16) return;

        player.getHungerManager().eat(this, stack);
        TrinketsApi.getTrinketComponent(player).getStack(SlotGroups.HEAD, Slots.MASK).decrement(1);
        player.playSound(SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 1, 1);
    }
}
