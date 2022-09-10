package com.glisco.things.items.trinkets;

import com.glisco.things.Things;
import com.glisco.things.items.ThingsItems;
import com.glisco.things.items.TrinketItemWithOptionalTooltip;
import dev.emi.trinkets.api.SlotReference;
import io.wispforest.owo.ops.TextOps;
import io.wispforest.owo.ops.WorldOps;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SocksItem extends TrinketItemWithOptionalTooltip {

    public static final String JUMPY_KEY = "Jumpy";
    public static final String JUMP_BOOST_TOGGLE_KEY = "JumpBoostDisabled";
    public static final String SPEED_KEY = "Speed";

    public SocksItem() {
        super(new Settings().maxCount(1).group(Things.THINGS_GROUP));
    }

    public static ItemStack create(int speed, boolean jumpy) {
        var stack = new ItemStack(ThingsItems.SOCKS);
        stack.getOrCreateNbt().putInt(SocksItem.SPEED_KEY, speed);
        stack.getOrCreateNbt().putBoolean(SocksItem.JUMPY_KEY, jumpy);
        return stack;
    }

    @Override
    public void tick(ItemStack stack, SlotReference slotRef, LivingEntity entity) {
        if (!(entity instanceof PlayerEntity player)) return;

        final var sockData = Things.SOCK_DATA.get(player);
        final var nbt = stack.getOrCreateNbt();

        sockData.jumpySocksEquipped = nbt.getBoolean(JUMPY_KEY);
        if (player.world.isClient) return;

        if (player.isSneaking() && player.isSprinting()) {
            sockData.sneakTicks++;
            if (sockData.sneakTicks >= 20) {
                nbt.putBoolean(JUMP_BOOST_TOGGLE_KEY, !nbt.getBoolean(JUMP_BOOST_TOGGLE_KEY));
                sockData.sneakTicks = 0;

                WorldOps.playSound(player.world, player.getPos(), SoundEvents.UI_TOAST_IN, SoundCategory.PLAYERS, 1, 2);
                Things.TOGGLE_JUMP_BOOST_PARTICLES.spawn(player.world, player.getPos());
            }
        } else {
            sockData.sneakTicks = 0;
        }

        sockData.updateSockSpeed(slotRef.index(), nbt.getInt(SPEED_KEY) + 1);

        if (!sockData.jumpySocksEquipped || nbt.getBoolean(JUMP_BOOST_TOGGLE_KEY)) return;
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 5, 1, true, false, true));
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        Things.SOCK_DATA.get(entity).jumpySocksEquipped = false;

        if (!(entity instanceof ServerPlayerEntity player)) return;
        int speed = stack.getOrCreateNbt().getInt(SocksItem.SPEED_KEY);

        Things.SOCK_DATA.get(player).modifySpeed(-Things.CONFIG.sockPerLevelSpeedAmplifier() * (speed + 1));
        Things.SOCK_DATA.get(player).clearSockSpeed(slot.index());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        var soccNbt = stack.getOrCreateNbt();

        if (soccNbt.getBoolean(JUMPY_KEY)) {
            tooltip.add(TextOps.withColor("↑ ", soccNbt.getBoolean(JUMP_BOOST_TOGGLE_KEY) ? TextOps.color(Formatting.GRAY) : 0x34d49c)
                    .append(TextOps.translateWithColor("item.things.socks.jumpy", TextOps.color(Formatting.GRAY))));
        }

        int speed = soccNbt.getInt(SocksItem.SPEED_KEY);
        if (speed < 3) {
            tooltip.add(TextOps.withColor("☄ ", 0x34b1d4)
                    .append(TextOps.translateWithColor("item.things.socks.speed_" + speed, TextOps.color(Formatting.GRAY))));
        } else {
            tooltip.add(TextOps.withColor("☄ ", 0x34b1d4)
                    .append(TextOps.translateWithColor("item.things.socks.speed_illegal", TextOps.color(Formatting.RED)))
                    .append(TextOps.withColor(" (" + speed + ")", TextOps.color(Formatting.RED))));
        }

        tooltip.add(Text.literal(" "));

        super.appendTooltip(stack, world, tooltip, context);
    }
}
