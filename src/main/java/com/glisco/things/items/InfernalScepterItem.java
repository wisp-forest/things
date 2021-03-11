package com.glisco.things.items;

import com.glisco.things.ThingsCommon;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InfernalScepterItem extends ItemWithOptionalTooltip {

    private static final List<Text> TOOLTIP;

    static {
        TOOLTIP = new ArrayList<>();
        TOOLTIP.add(new LiteralText("§7Shoots Fireballs"));
        TOOLTIP.add(new LiteralText("§7Uses Fire Charges as ammunition"));
    }

    public InfernalScepterItem() {
        super(new Settings().group(ThingsCommon.THINGS_ITEMS).maxCount(1).maxDamage(64));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!user.inventory.containsAny(Collections.singleton(Items.FIRE_CHARGE))) return TypedActionResult.fail(user.getStackInHand(hand));
        user.setCurrentHand(hand);
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity)) return;
        if (72000 - remainingUseTicks < 20) return;

        PlayerEntity player = (PlayerEntity) user;

        if (!player.inventory.containsAny(Collections.singleton(Items.FIRE_CHARGE))) return;

        if (!world.isClient) {
            Vec3d vec3d = player.getRotationVec(0.0F);
            double vX = (player.getX() + vec3d.x * 4.0D) - player.getX();
            double vY = (player.getY() + vec3d.y * 4.0D) - player.getY();
            double vZ = (player.getZ() + vec3d.z * 4.0D) - player.getZ();

            FireballEntity fireball = new FireballEntity(world, user, vX, vY, vZ);
            fireball.updatePosition(player.getX() + vec3d.x * 2.0D, player.getEyeY() - 1, player.getZ() + vec3d.z * 2.0D);
            fireball.explosionPower = 3;
            world.spawnEntity(fireball);
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 1, 1);

            stack.damage(1, user, (p) -> {
                user.sendToolBreakStatus(user.getActiveHand());
            });
        }

        player.inventory.getStack(player.inventory.method_7371(new ItemStack(Items.FIRE_CHARGE))).decrement(1);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    List<Text> getTooltipText() {
        return TOOLTIP;
    }
}
