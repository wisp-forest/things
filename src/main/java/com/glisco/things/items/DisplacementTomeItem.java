package com.glisco.things.items;

import com.glisco.things.DisplacementTomeScreenHandler;
import com.glisco.things.ThingsCommon;
import com.glisco.things.network.UpdateDisplacementTomeS2CPacket;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class DisplacementTomeItem extends ItemWithOptionalTooltip {

    public DisplacementTomeItem() {
        super(new Item.Settings().group(ThingsCommon.THINGS_ITEMS).maxCount(1));
    }

    public static void storeTeleportTargetInBook(ItemStack stack, TargetLocation target, String name, boolean replaceIfExisting) {
        CompoundTag targets = stack.getOrCreateSubTag("Targets");

        if (targets.contains(name) && !replaceIfExisting) {
            throw new IllegalArgumentException("This teleport point already exists and replaceIfExisting was not set");
        }

        targets.put(name, target.toTag());
        stack.putSubTag("Targets", targets);
    }

    public static void addFuel(ItemStack stack, int fuel) {
        CompoundTag stackTag = stack.getOrCreateTag();
        int currentFuel = stackTag.contains("Fuel") ? stackTag.getInt("Fuel") : 0;
        currentFuel += fuel;
        stackTag.putInt("Fuel", currentFuel);
    }

    public static boolean deletePoint(ItemStack stack, String name) {
        CompoundTag targets = stack.getOrCreateSubTag("Targets");
        if (!targets.contains(name)) return false;
        targets.remove(name);
        stack.putSubTag(name, targets);
        return true;
    }

    public static boolean rename(ItemStack stack, String data) {
        String name = data.split(":")[0];
        String newName = data.split(":")[1];

        CompoundTag targets = stack.getOrCreateSubTag("Targets");
        if (!targets.contains(name)) return false;
        CompoundTag toRename = targets.getCompound(name).copy();
        targets.remove(name);
        targets.put(newName, toRename);
        stack.putSubTag(name, targets);
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        if (user.isSneaking()) {
            int enderPearlSlot = user.inventory.method_7371(new ItemStack(Items.ENDER_PEARL));
            if (enderPearlSlot == -1) return TypedActionResult.pass(user.getStackInHand(hand));

            ItemStack pearls = user.inventory.getStack(enderPearlSlot);
            addFuel(user.getStackInHand(hand), pearls.getCount());
            user.inventory.setStack(enderPearlSlot, ItemStack.EMPTY);
            user.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 1, 2);
        } else {
            user.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) -> {
                return new DisplacementTomeScreenHandler(i, playerInventory, user.getStackInHand(hand));
            }, new LiteralText("help")));
            if (user instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) user).networkHandler.connection.send(UpdateDisplacementTomeS2CPacket.create(user.getStackInHand(hand)));
            } else {
                user.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 1, 1);
            }
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    List<Text> getTooltipText() {
        return Collections.singletonList(new LiteralText("§7A fancy tool used for teleportation"));
    }

    public static class TargetLocation {

        private final BlockPos pos;
        private final RegistryKey<World> world;
        private final float headYaw;
        private final float headPitch;

        public TargetLocation(BlockPos pos, RegistryKey<World> world, float headYaw, float headPitch) {
            this.pos = pos;
            this.world = world;
            this.headYaw = headYaw;
            this.headPitch = headPitch;
        }

        public void teleportPlayer(ServerPlayerEntity player) {
            player.teleport(player.getServer().getWorld(world), pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, headYaw, headPitch);
        }

        public CompoundTag toTag() {
            CompoundTag tag = new CompoundTag();
            tag.putLong("Pos", pos.asLong());
            tag.putString("World", world.getValue().toString());
            tag.putFloat("HeadYaw", headYaw);
            tag.putFloat("HeadPitch", headPitch);
            return tag;
        }

        public static TargetLocation fromPlayer(ServerPlayerEntity player) {
            return new TargetLocation(player.getBlockPos(), player.world.getRegistryKey(), player.headYaw, player.pitch);
        }

        @Nullable
        public static TargetLocation fromTag(CompoundTag tag) {
            if (!tag.contains("Pos")) return null;
            if (!tag.contains("World")) return null;
            if (!tag.contains("HeadYaw")) return null;
            if (!tag.contains("HeadPitch")) return null;

            BlockPos blockPos = BlockPos.fromLong(tag.getLong("Pos"));
            RegistryKey<World> worldRegistryKey = RegistryKey.of(Registry.DIMENSION, new Identifier(tag.getString("World")));
            float yaw = tag.getFloat("HeadYaw");
            float pitch = tag.getFloat("HeadPitch");

            return new TargetLocation(blockPos, worldRegistryKey, yaw, pitch);
        }

    }

    public static class PredicateProvider implements ModelPredicateProvider {
        @Override
        public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
            int size = stack.getOrCreateSubTag("Targets").getSize();
            if (size == 0) {
                return 0;
            } else if (size < 4) {
                return 1;
            } else {
                return 2;
            }
        }
    }
}
