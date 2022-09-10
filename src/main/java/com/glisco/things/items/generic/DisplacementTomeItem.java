package com.glisco.things.items.generic;

import com.glisco.things.Things;
import com.glisco.things.ThingsNetwork;
import com.glisco.things.items.ItemWithExtendableTooltip;
import com.glisco.things.misc.DisplacementTomeScreenHandler;
import io.wispforest.owo.nbt.NbtKey;
import io.wispforest.owo.ops.WorldOps;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DisplacementTomeItem extends ItemWithExtendableTooltip {

    public static final NbtKey<Integer> FUEL = new NbtKey<>("Fuel", NbtKey.Type.INT);
    public static final NbtKey<NbtCompound> TARGETS = new NbtKey<>("Targets", NbtKey.Type.COMPOUND);

    public DisplacementTomeItem() {
        super(new Settings().group(Things.THINGS_GROUP).maxCount(1));
    }

    public static void storeTeleportTargetInBook(ItemStack stack, Target target, String name, boolean replaceIfExisting) {
        var targets = stack.get(TARGETS);

        if (targets.contains(name) && !replaceIfExisting) {
            throw new IllegalArgumentException("Teleport point '" + name + "' already exists and replaceIfExisting was not set");
        }

        target.put(targets, name);
        stack.put(TARGETS, targets);
    }

    public static void addFuel(ItemStack stack, int fuel) {
        stack.mutate(FUEL, f -> f + fuel);
    }

    public static boolean deletePoint(ItemStack stack, String name) {
        var targets = stack.get(TARGETS);
        if (!targets.contains(name)) return false;
        targets.remove(name);
        return true;
    }

    public static boolean rename(ItemStack stack, String data) {
        var name = data.split(":")[0];
        var newName = data.split(":")[1];

        var targets = stack.get(TARGETS);
        if (!targets.contains(name)) return false;
        targets.put(newName, targets.get(name));
        targets.remove(name);
        return true;
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        if (clickType != ClickType.RIGHT) return false;

        var slotStack = slot.getStack();
        if (!slotStack.isIn(Things.DISPLACEMENT_TOME_FUELS)) return false;

        addFuel(stack, slotStack.getCount());
        slot.setStack(ItemStack.EMPTY);

        player.playSound(SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, .5f, 2f);

        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) ->
                new DisplacementTomeScreenHandler(i, playerInventory, user.getStackInHand(hand)), Text.literal("help")));

        if (user instanceof ServerPlayerEntity) {
            ThingsNetwork.CHANNEL.serverHandle(user).send(new DisplacementTomeScreenHandler.UpdateClientPacket(user.getStackInHand(hand)));
        } else {
            user.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 1, 1);
        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.things.displacement_tome.tooltip.charges", stack.get(FUEL)));
        super.appendTooltip(stack, world, tooltip, context);
    }

    public record Target(BlockPos pos, RegistryKey<World> world, float headYaw, float headPitch) {

        public void teleportPlayer(ServerPlayerEntity player) {
            WorldOps.teleportToWorld(player, player.getServer().getWorld(this.world), Vec3d.ofCenter(this.pos), this.headYaw, this.headPitch);
        }

        public static Target fromPlayer(ServerPlayerEntity player) {
            return new Target(player.getBlockPos(), player.world.getRegistryKey(), player.headYaw, player.getPitch());
        }

        public void put(NbtCompound compound, String key) {
            var nbt = new NbtCompound();
            nbt.putLong("Pos", this.pos.asLong());
            nbt.putString("World", this.world.getValue().toString());
            nbt.putFloat("HeadYaw", this.headYaw);
            nbt.putFloat("HeadPitch", this.headPitch);
            compound.put(key, nbt);
        }

        public static Target get(NbtCompound compound, String key) {
            var nbt = compound.getCompound(key);
            return new Target(
                    BlockPos.fromLong(nbt.getLong("Pos")),
                    RegistryKey.of(Registry.WORLD_KEY, new Identifier(nbt.getString("World"))),
                    nbt.getFloat("HeadYaw"),
                    nbt.getFloat("HeadPitch")
            );
        }
    }

    public static class PredicateProvider implements UnclampedModelPredicateProvider {
        @Override
        public float unclampedCall(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
            int size = stack.get(TARGETS).getSize();
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
