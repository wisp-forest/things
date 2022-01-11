package com.glisco.things.items.generic;

import com.glisco.things.Things;
import com.glisco.things.items.ThingsItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

import java.util.Random;

public class ItemMagnetItem extends Item {

    public ItemMagnetItem() {
        super(new Settings().group(Things.THINGS_GROUP).maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        boolean blue = true;
        for (double i = 2; i < 10; i += 0.15) {
            HitResult result = user.raycast(i, 0, false);

            if (world.isClient) {
                ParticleEffect particle = new DustParticleEffect(new Vec3f(blue ? 0.5f : 1, 0, blue ? 1 : 0.5f), 1);
                blue = !blue;

                world.addParticle(particle, result.getPos().x, result.getPos().y, result.getPos().z, 0, 0, 0);

                if (i > 9.5) {
                    displayTerminator(world, result.getPos(), 0.65);
                }
            }

            if (!result.getType().equals(HitResult.Type.MISS)) {

                if (world.isClient) {
                    HitResult terminatorPosition = user.raycast(i - 0.75, 0, false);
                    displayTerminator(world, terminatorPosition.getPos(), 0.25);
                }

                break;
            }

            Vec3d box1 = result.getPos().add(-1.5, -1.5, -1.5);
            Vec3d box2 = result.getPos().add(1.5, 1.5, 1.5);
            for (Entity e : world.getNonSpectatingEntities(ItemEntity.class, new Box(box1, box2))) {
                ItemEntity item = (ItemEntity) e;
                if (world.isClient) {
                    world.addParticle(ParticleTypes.POOF, item.getX(), item.getY() + 0.25, item.getZ(), 0, 0, 0);
                } else {
                    item.teleport(user.getX(), user.getY(), user.getZ());
                    item.setNoGravity(true);
                    item.setVelocity(Vec3d.ZERO);
                    item.setPickupDelay(0);
                }
            }
        }

        user.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 0.125f, 2);
        user.getItemCooldownManager().set(ThingsItems.ITEM_MAGNET, 20);

        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Environment(EnvType.CLIENT)
    private static void displayTerminator(World worldIn, Vec3d at, double spread) {
        for (int j = 0; j < 5; j++) {
            Random rand = worldIn.getRandom();

            double x = at.x + (rand.nextDouble() - 0.5) * spread;
            double y = at.y + (rand.nextDouble() - 0.5) * spread;
            double z = at.z + (rand.nextDouble() - 0.5) * spread;

            worldIn.addParticle(ParticleTypes.WITCH, x, y, z, 0, 0, 0);
        }
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return false;
    }
}
