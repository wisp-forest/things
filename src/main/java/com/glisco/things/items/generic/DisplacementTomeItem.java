package com.glisco.things.items.generic;

import com.glisco.things.Things;
import com.glisco.things.ThingsNetwork;
import com.glisco.things.items.ItemWithExtendableTooltip;
import com.glisco.things.misc.DisplacementTomeScreenHandler;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.ops.WorldOps;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.BuiltInEndecs;
import io.wispforest.owo.serialization.endec.KeyedEndec;
import io.wispforest.owo.serialization.endec.StructEndecBuilder;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplacementTomeItem extends ItemWithExtendableTooltip {

    public static final KeyedEndec<Integer> FUEL = Endec.INT.keyed("Fuel", 0);
    public static final KeyedEndec<Map<String, Target>> TARGETS = Target.ENDEC.mapOf().keyed("Targets", HashMap::new);

    public DisplacementTomeItem() {
        super(new OwoItemSettings().group(Things.THINGS_GROUP).maxCount(1));
    }

    public static void storeTeleportTargetInBook(ItemStack stack, Target target, String name, boolean replaceIfExisting) {
        var targets = stack.get(TARGETS);

        if (targets.containsKey(name) && !replaceIfExisting) {
            throw new IllegalArgumentException("Teleport point '" + name + "' already exists and replaceIfExisting was not set");
        }

        targets.put(name, target);
        stack.put(TARGETS, targets);
    }

    public static void addFuel(ItemStack stack, int fuel) {
        stack.mutate(FUEL, f -> f + fuel);
    }

    public static boolean deletePoint(ItemStack stack, String name) {
        var targets = stack.get(TARGETS);
        if (!targets.containsKey(name)) return false;
        targets.remove(name);
        return true;
    }

    public static boolean rename(ItemStack stack, String data) {
        var name = data.split(":")[0];
        var newName = data.split(":")[1];

        var targets = stack.get(TARGETS);
        if (!targets.containsKey(name)) return false;
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

        public static final Endec<Target> ENDEC = StructEndecBuilder.of(
                Endec.LONG.xmap(BlockPos::fromLong, BlockPos::asLong).fieldOf("Pos", Target::pos),
                BuiltInEndecs.IDENTIFIER.xmap(identifier -> RegistryKey.of(RegistryKeys.WORLD, identifier), RegistryKey::getValue).fieldOf("World", Target::world),
                Endec.FLOAT.fieldOf("HeadYaw", Target::headYaw),
                Endec.FLOAT.fieldOf("HeadPitch", Target::headPitch),
                Target::new
        );

        public void teleportPlayer(ServerPlayerEntity player) {
            WorldOps.teleportToWorld(player, player.getServer().getWorld(this.world), Vec3d.ofCenter(this.pos), this.headYaw, this.headPitch);
        }

        public static Target fromPlayer(ServerPlayerEntity player) {
            return new Target(player.getBlockPos(), player.getWorld().getRegistryKey(), player.headYaw, player.getPitch());
        }
    }

    public static class PredicateProvider implements ClampedModelPredicateProvider {
        @Override
        public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
            int size = stack.get(TARGETS).size();
            if (size == 0) {
                return 0;
            } else if (size < 4) {
                return 1;
            } else {
                return 2;
            }
        }

        @Override
        public float unclampedCall(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
            throw new AssertionError("respectfully, get fucked");
        }
    }
}
