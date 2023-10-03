package com.glisco.things.items.trinkets;

import com.glisco.things.Things;
import com.glisco.things.client.ThingsClient;
import com.glisco.things.items.ThingsItems;
import com.glisco.things.items.TrinketItemWithOptionalTooltip;
import dev.emi.trinkets.api.SlotReference;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.nbt.NbtKey;
import io.wispforest.owo.ops.TextOps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SocksItem extends TrinketItemWithOptionalTooltip {

    public static final NbtKey<Boolean> JUMPY_KEY = new NbtKey<>("Jumpy", NbtKey.Type.BOOLEAN);
    public static final NbtKey<Boolean> JUMP_BOOST_TOGGLE_KEY = new NbtKey<>("JumpBoostDisabled", NbtKey.Type.BOOLEAN);
    public static final NbtKey<Integer> SPEED_KEY = new NbtKey<>("Speed", NbtKey.Type.INT);

    public SocksItem() {
        super(new OwoItemSettings().maxCount(1).group(Things.THINGS_GROUP));
    }

    public static ItemStack create(int speed, boolean jumpy) {
        var stack = new ItemStack(ThingsItems.SOCKS);
        stack.put(SocksItem.SPEED_KEY, speed);
        stack.put(SocksItem.JUMPY_KEY, jumpy);
        return stack;
    }

    @Override
    public void tick(ItemStack stack, SlotReference slotRef, LivingEntity entity) {
        if (!(entity instanceof PlayerEntity player)) return;

        final var sockData = Things.SOCK_DATA.get(player);
        final var nbt = stack.getOrCreateNbt();

        sockData.jumpySocksEquipped = nbt.get(JUMPY_KEY);
        if (player.getWorld().isClient) return;

        sockData.updateSockSpeed(slotRef.index(), nbt.get(SPEED_KEY) + 1);

        if (!sockData.jumpySocksEquipped || nbt.get(JUMP_BOOST_TOGGLE_KEY)) return;
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 5, 1, true, false, true));
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        Things.SOCK_DATA.get(entity).jumpySocksEquipped = false;

        if (!(entity instanceof ServerPlayerEntity player)) return;
        int speed = stack.getOr(SPEED_KEY, 0);

        Things.SOCK_DATA.get(player).modifySpeed(-Things.CONFIG.sockPerLevelSpeedAmplifier() * (speed + 1));
        Things.SOCK_DATA.get(player).clearSockSpeed(slot.index());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void append(List<Text> tooltip) {
        this.appendWrapped(tooltip, Text.translatable(this.tooltipTranslationKey(), KeyBindingHelper.getBoundKeyOf(ThingsClient.TOGGLE_SOCKS_JUMP_BOOST).getLocalizedText()));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.getOr(JUMPY_KEY, false)) {
            tooltip.add(TextOps.withColor("↑ ", stack.getOr(JUMP_BOOST_TOGGLE_KEY, false) ? TextOps.color(Formatting.GRAY) : 0x34d49c)
                    .append(TextOps.translateWithColor("item.things.socks.jumpy", TextOps.color(Formatting.GRAY))));
        }

        int speed = stack.getOr(SocksItem.SPEED_KEY, 0);
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
