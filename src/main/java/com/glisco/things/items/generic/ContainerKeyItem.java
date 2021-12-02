package com.glisco.things.items.generic;

import com.glisco.things.ThingsCommon;
import com.glisco.things.items.ItemWithExtendableTooltip;
import com.glisco.things.mixin.ContainerLockAccessor;
import com.glisco.things.mixin.LockableContainerBlockEntityAccessor;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ContainerKeyItem extends ItemWithExtendableTooltip {

    private static final List<Text> TOOLTIP;

    static {
        TOOLTIP = new ArrayList<>();
        TOOLTIP.add(new LiteralText("§7Used to lock containers"));
        TOOLTIP.add(new LiteralText("§7 - Sneak-click to lock/unlock"));
        TOOLTIP.add(new LiteralText("§7 - Required to open a locked container"));
    }

    public ContainerKeyItem() {
        super(new Settings().group(ThingsCommon.THINGS_GROUP).maxCount(1));
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getPlayer().isSneaking()) return ActionResult.PASS;

        createKey(context.getStack(), context.getWorld().random);

        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        ItemStack stack = context.getStack();

        if (!(world.getBlockEntity(pos) instanceof LockableContainerBlockEntity)) return ActionResult.PASS;

        String existingLock = getExistingLock(world, pos);

        if (existingLock.isEmpty()) {
            setLock((LockableContainerBlockEntity) world.getBlockEntity(pos), String.valueOf(stack.getNbt().getInt("Lock")));

            if (world.isClient) {
                sendLockedState(context, true);
            }

            return ActionResult.SUCCESS;
        } else if (existingLock.equals(String.valueOf(stack.getNbt().getInt("Lock")))) {
            setLock((LockableContainerBlockEntity) world.getBlockEntity(pos), "");

            if (world.isClient) {
                sendLockedState(context, false);
            }

            return ActionResult.SUCCESS;
        } else {

            if (world.isClient) {
                context.getPlayer().playSound(SoundEvents.BLOCK_CHEST_LOCKED, 1, 1);

                MutableText containerName =
                        (MutableText) ((LockableContainerBlockEntity) context.getWorld().getBlockEntity(context.getBlockPos())).getDisplayName();
                context.getPlayer().sendMessage(containerName.append(new LiteralText(" is locked with another key!")), true);
            }

            return ActionResult.SUCCESS;
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        createKey(stack, world.random);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        var nbt = stack.getOrCreateNbt();
        if (nbt.contains("Lock", NbtElement.INT_TYPE)) {
            tooltip.add(new LiteralText("§9Key: §7#" + Integer.toHexString(nbt.getInt("Lock"))));
        }

        super.appendTooltip(stack, world, tooltip, context);
    }

    private static void createKey(ItemStack stack, Random random) {
        if (stack.getOrCreateNbt().contains("Lock")) return;
        stack.getOrCreateNbt().putInt("Lock", random.nextInt(200000));
    }

    private static String getExistingLock(World world, BlockPos pos) {
        final var blockEntity = world.getBlockEntity(pos);
        String existingLock = getKey(blockEntity);

        var chestNeighbor = maybeGetOtherChest(blockEntity);
        if (existingLock.isEmpty() && chestNeighbor != null) {
            if (getKey(chestNeighbor).isEmpty()) return existingLock;

            return getKey(chestNeighbor);
        }

        return existingLock;
    }

    private static void sendLockedState(ItemUsageContext ctx, boolean locked) {
        ctx.getPlayer().playSound(SoundEvents.BLOCK_CHEST_LOCKED, 1, 1);

        MutableText containerName = (MutableText) ((LockableContainerBlockEntity) ctx.getWorld().getBlockEntity(ctx.getBlockPos())).getDisplayName();
        ctx.getPlayer().sendMessage(containerName.append(new LiteralText(locked ? " locked!" : " unlocked!")), true);
    }

    private static void setLock(LockableContainerBlockEntity entity, String lock) {
        NbtCompound lockNbt = new NbtCompound();
        lockNbt.putString("Lock", lock);

        ContainerLock containerLock = lock.isEmpty() ? ContainerLock.EMPTY : ContainerLock.fromNbt(lockNbt);

        ((LockableContainerBlockEntityAccessor) entity).things$setLock(containerLock);
        final var doubleChestNeighbor = maybeGetOtherChest(entity);
        if (doubleChestNeighbor == null) return;

        ((LockableContainerBlockEntityAccessor) doubleChestNeighbor).things$setLock(containerLock);
    }

    private static String getKey(BlockEntity be) {
        return ((ContainerLockAccessor) ((LockableContainerBlockEntityAccessor) be).things$getLock()).things$getKey();
    }

    @SuppressWarnings("ConstantConditions")
    private static @Nullable ChestBlockEntity maybeGetOtherChest(BlockEntity potentialChest) {
        if (!(potentialChest instanceof ChestBlockEntity)) return null;
        if (potentialChest.getCachedState().get(Properties.CHEST_TYPE) == ChestType.SINGLE) return null;
        return (ChestBlockEntity) potentialChest.getWorld().getBlockEntity(potentialChest.getPos().offset(ChestBlock.getFacing(potentialChest.getCachedState())));
    }

    @Override
    public List<Text> getExtendedTooltip() {
        return TOOLTIP;
    }
}
