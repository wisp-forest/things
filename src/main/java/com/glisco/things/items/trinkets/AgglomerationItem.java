package com.glisco.things.items.trinkets;

import com.glisco.things.Things;
import com.glisco.things.items.ThingsItems;
import com.glisco.things.mixin.ItemUsageContextAccessor;
import com.glisco.things.text.AgglomerationTooltipData;
import com.glisco.things.text.TooltipComponentText;
import io.wispforest.accessories.Accessories;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.AccessoryItem;
import io.wispforest.accessories.api.AccessoryNest;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.components.AccessoriesDataComponents;
import io.wispforest.accessories.api.components.AccessoryNestContainerContents;
import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.accessories.data.SlotTypeLoader;
import io.wispforest.accessories.impl.AccessoryNestUtils;
import io.wispforest.endec.Endec;
import io.wispforest.endec.SerializationAttributes;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.network.ServerAccess;
import io.wispforest.owo.serialization.CodecUtils;
import it.unimi.dsi.fastutil.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class AgglomerationItem extends AccessoryItem implements AccessoryNest, AccessoryRenderer {

    public AgglomerationItem() {
        super(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));
    }

    //--------

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        return getSlottedStackAndRun(stack, player, (innerSlot, innerStack) -> {
            return innerStack.onClicked(otherStack, innerSlot, clickType, player, cursorStackReference);
        }, () -> false);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return getStackAndRun(context.getStack(), context.getPlayer(), innerStack -> {
            return innerStack.useOnBlock(new ItemUsageContext(context.getWorld(), context.getPlayer(), context.getHand(), innerStack, ((ItemUsageContextAccessor)context).things$getHitResult()));
        }, () -> ActionResult.FAIL);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        return getStackAndRun(stack, user instanceof PlayerEntity player ? player : null, innerStack -> {
            return innerStack.finishUsing(world, user);
        }, () -> stack);
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        return getSlottedStackAndRun(stack, player, (innerSlot, innerStack) -> innerStack.onStackClicked(innerSlot, clickType, player), () -> false);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return getStackAndRun(stack, attacker instanceof PlayerEntity player ? player : null, innerStack -> {
            innerStack.postHit(target, ((PlayerEntity) attacker));

            return true;
        }, () -> false);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        return getStackAndRun(stack, miner instanceof PlayerEntity player ? player : null, innerStack -> {
            innerStack.postMine(world, state, pos, ((PlayerEntity) miner));

            return true;
        }, () -> false);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        return getStackAndRun(stack, user, innerStack -> innerStack.useOnEntity(user, entity, hand), () -> ActionResult.FAIL);
    }

    @Override
    public boolean isUsedOnRelease(ItemStack stack) {
        return getStackAndRun(stack, null, ItemStack::isUsedOnRelease, () -> false);
    }

    public <T> T getStackAndRun(ItemStack stack, PlayerEntity player, Function<ItemStack, T> methodPassthru, Supplier<T> error){
        return getSlottedStackAndRun(stack, player, (integer, stack1) -> methodPassthru.apply(stack1), error);
    }

    public <T> T getSlottedStackAndRun(ItemStack stack, PlayerEntity player, BiFunction<Slot, ItemStack, T> methodPassthru, Supplier<T> error){
        var value = AccessoryNest.attemptFunction(stack, player, map -> {
            var data = AccessoryNestUtils.getData(stack);

            int index = 0;

            var selectedTrinket = getSelectedIndex(stack);

            if(selectedTrinket >= map.size()) return error.get();

            for (var entry : map.entrySet()) {
                if(index == selectedTrinket) return methodPassthru.apply(new AgglomerationItemSlot(stack, data, index, 0, 0), entry.getKey());

                index++;
            }

            return null;
        }, null);

        return value != null ? value : error.get();
    }

    //--------

    public static void scrollSelectedStack(ItemStack stack){
        stack.apply(SelectedStackComponent.COMPONENT_TYPE, SelectedStackComponent.DEFAULT, component -> {
            return new SelectedStackComponent(component.index() == 0 ? 1 : 0);
        });
    }

    public static int getSelectedIndex(ItemStack stack) {
        return stack.getOrDefault(SelectedStackComponent.COMPONENT_TYPE, SelectedStackComponent.DEFAULT).index();
    }

    public static ItemStack createStack(ItemStack... items) {
        var stack = new ItemStack(ThingsItems.AGGLOMERATION, 1);

        stack.set(AccessoriesDataComponents.NESTED_ACCESSORIES, new AccessoryNestContainerContents(Arrays.stream(items).map(ItemStack::copy).toList()));
        stack.set(SelectedStackComponent.COMPONENT_TYPE, new SelectedStackComponent((byte) 0));

        return stack;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var data = AccessoryNestUtils.getData(user.getStackInHand(hand));

        if(data != null) {
            for (var stack : data.accessories()) {
                if (stack.isEmpty()) {
                    var cake = new ItemStack(Items.CAKE);
                    cake.set(DataComponentTypes.ITEM_NAME, Text.translatable("item.things.consolation_cake"));

                    user.getInventory().offerOrDrop(cake);
                    return TypedActionResult.success(ItemStack.EMPTY);
                }
            }
        }

        return super.use(world, user, hand);
    }

    //--

    @Override
    public boolean canEquip(ItemStack stack, SlotReference slot) {
        var isInnerStacksValid = AccessoryNest.attemptFunction(stack, slot, map -> {
            for (var entryRef : map.keySet()) {
                if(!AccessoriesAPI.canInsertIntoSlot(entryRef.stack(), entryRef.reference())) return false;
            }

            return true;
        }, false);

        var slotType = SlotTypeLoader.getSlotType(slot.entity(), slot.slotName());

        var validators = slotType.validators();

        if(!isInnerStacksValid && validators.contains(Accessories.of("component"))) {
            var state = AccessoriesAPI.getPredicate(Accessories.of("component"))
                    .isValid(slot.entity().getWorld(), slotType, slot.slot(), stack);

            if(state == TriState.TRUE) return true;
        }

        return isInnerStacksValid;
    }

    @Override
    public boolean canUnequip(ItemStack stack, SlotReference reference) {
        if(AccessoryNestUtils.getData(stack) == null) return true;

        return AccessoryNest.super.canUnequip(stack, reference);
    }

    @Override
    public void getExtraTooltip(ItemStack stack, List<Text> tooltips, TooltipContext tooltipContext, TooltipType tooltipType) {
        var data = AccessoryNestUtils.getData(stack);

        if(data == null) return;

        var subStacks = data.accessories();

        var innerTooltipData = new ArrayList<Text>();

        for (int i = 0; i < subStacks.size(); i++) {
            var subStack = subStacks.get(i);

            var subTooltip = subStacks.get(i).getTooltip(tooltipContext,null, tooltipType);

            for (int j = 0; j < subTooltip.size(); j++) {
                if (j == 0) {
                    var text = new TooltipComponentText(new AgglomerationTooltipData(Text.literal(getSelectedIndex(stack) == i ? "> " : "• "), subStack, subTooltip.get(j)));

                    innerTooltipData.add(text);
                } else {
                    innerTooltipData.add(Text.literal("  ").append(subTooltip.get(j)));
                }
            }
        }

        for (var subStack : subStacks) {
            if (!subStack.isEmpty()) continue;
            innerTooltipData.add(Text.empty());
            innerTooltipData.add(Text.translatable("item.things.consolation_cake.hint"));
        }

        tooltips.addAll(0, innerTooltipData);
    }

    @Override
    public void onStackChanges(ItemStack holderStack, AccessoryNestContainerContents data, @Nullable LivingEntity livingEntity) {
        for (var accessory : data.accessories()) {
            if (accessory.isOf(Items.AIR) && livingEntity instanceof ServerPlayerEntity player) {
                Things.AN_AMAZINGLY_EXPENSIVE_MISTAKE_CRITERION.trigger(player);
            }
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, MatrixStack matrices, EntityModel<M> model, VertexConsumerProvider multiBufferSource, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!Things.CONFIG.renderAgglomerationTrinket()) return;

        AccessoryNest.attemptConsumer(stack, reference, map -> {
            map.forEach((slotEntryReference, accessory) -> {
                var subStack = slotEntryReference.stack();
                var renderer = AccessoriesRendererRegistry.getRender(subStack);

                if (renderer != null) {
                    matrices.push();
                    renderer.render(subStack, reference, matrices, model, multiBufferSource, light, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
                    matrices.pop();
                }
            });
        });
    }

    //--

    public record ScrollHandStackTrinket(boolean mainHandStack){
        public static void scrollItemStack(ScrollHandStackTrinket message, ServerAccess access){
            var stack = message.mainHandStack ? access.player().getMainHandStack() : access.player().getOffHandStack();

            AgglomerationItem.scrollSelectedStack(stack);

            var data = AccessoryNestUtils.getData(stack);

            access.player().sendMessageToClient(Text.literal("> ")
                    .append(Text.translatable(data.accessories().get(getSelectedIndex(stack)).getTranslationKey())), true);
        }
    }

    public record ScrollStackFromSlotTrinket(boolean fromPlayerInv, int slotId){
        public static void scrollItemStack(ScrollStackFromSlotTrinket message, ServerAccess access){
            var stack = message.fromPlayerInv
                    ? access.player().getInventory().getStack(message.slotId)
                    : access.player().currentScreenHandler.getSlot(message.slotId).getStack();

            if(stack == null) return;

            AgglomerationItem.scrollSelectedStack(stack);
        }
    }

    public record SelectedStackComponent(int index) {
        public static final SelectedStackComponent DEFAULT = new SelectedStackComponent(0);

        public static final Endec<SelectedStackComponent> ENDEC = StructEndecBuilder.of(
                Endec.ifAttr(SerializationAttributes.HUMAN_READABLE, Endec.INT).orElse(Endec.VAR_INT).fieldOf("index", SelectedStackComponent::index),
                SelectedStackComponent::new
        );

        public static final ComponentType<SelectedStackComponent> COMPONENT_TYPE = ComponentType.<SelectedStackComponent>builder()
                .codec(CodecUtils.toCodec(ENDEC))
                .packetCodec(CodecUtils.toPacketCodec(ENDEC))
                .cache()
                .build();
    }

    private static class AgglomerationItemSlot extends Slot {
        public AgglomerationItemSlot(ItemStack nestStack, AccessoryNestContainerContents containerContents, int index, int x, int y) {
            super(new AccessoryNestInventory(nestStack, containerContents), index, x, y);
        }
    }

    public static class AccessoryNestInventory implements Inventory {

        private final ItemStack nestStack;
        private AccessoryNestContainerContents containerContents;

        private AccessoryNestInventory(ItemStack nestStack, AccessoryNestContainerContents containerContents) {
            this.nestStack = nestStack;
            this.containerContents = containerContents;
        }

        @Override
        public int size() {
            return this.containerContents.accessories().size();
        }

        @Override
        public boolean isEmpty() {
            return this.containerContents.accessories().isEmpty();
        }

        @Override
        public ItemStack getStack(int slot) {
            return this.containerContents.accessories().get(slot);
        }

        @Override
        public ItemStack removeStack(int slot, int amount) {
            var stacks = splitStack(this.containerContents.accessories(), slot, amount);

            if (stacks == null || stacks.right().isEmpty()) return ItemStack.EMPTY;

            // Force Change to be updated
            this.setStack(slot, stacks.left());

            return stacks.right();
        }

        // Return is the Slot based stack on the LEFT and
        // the split off stack with the given amount on the RIGHT
        @Nullable
        public static Pair<ItemStack, ItemStack> splitStack(List<ItemStack> stacks, int slot, int amount) {
            if (slot >= 0 && slot < stacks.size() && !stacks.get(slot).isEmpty() && amount > 0) {
                var copiedStack = stacks.get(slot).copy();

                return Pair.of(copiedStack, copiedStack.split(amount));
            }

            return null;
        }

        @Override
        public ItemStack removeStack(int slot) {
            var itemStack = this.containerContents.accessories().get(slot);

            if (itemStack.isEmpty()) return ItemStack.EMPTY;

            // Force Change to be updated
            this.setStack(slot, ItemStack.EMPTY);

            return itemStack;
        }

        @Override
        public void setStack(int slot, ItemStack stack) {
            // Force Change to be updated
            this.containerContents = this.containerContents.setStack(slot, stack);

            stack.capCount(this.getMaxCount(stack));

            this.markDirty();
        }

        @Override
        public void markDirty() {
            this.nestStack.set(AccessoriesDataComponents.NESTED_ACCESSORIES, this.containerContents);
        }

        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return false;
        }

        @Override
        public void clear() {
            this.containerContents.accessories().clear();
            this.markDirty();
        }
    }
}
