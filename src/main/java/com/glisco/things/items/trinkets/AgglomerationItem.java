package com.glisco.things.items.trinkets;

import com.glisco.things.Things;
import com.glisco.things.items.ThingsItems;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

public class AgglomerationItem extends TrinketItem implements TrinketRenderer {
    private final static LoadingCache<ItemStack, StackData> CACHE = CacheBuilder.newBuilder()
        .concurrencyLevel(1)
        .maximumSize(200)
        .weakKeys()
        .build(CacheLoader.from(StackData::new));

    public AgglomerationItem() {
        super(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));
    }

    private static StackData getDataFor(ItemStack stack) {
        StackData data = CACHE.getUnchecked(stack);

        if (data.isInvalid()) {
            CACHE.refresh(stack);
            data = CACHE.getUnchecked(stack);
        }

        return data;
    }

    public static ItemStack createStack(ItemStack... items) {
        ItemStack stack = new ItemStack(ThingsItems.AGGLOMERATION, 1);

        NbtList itemsTag = new NbtList();
        stack.getOrCreateNbt().put("Items", itemsTag);

        for (ItemStack itemStack : items) {
            itemsTag.add(itemStack.writeNbt(new NbtCompound()));
        }

        return stack;
    }

    public static List<ItemStack> getStacks(ItemStack stack) {
        return getDataFor(stack).subStacks;
    }

    public static boolean hasStack(ItemStack stack, Predicate<ItemStack> predicate) {
        var data = getDataFor(stack);

        for (int i = 0; i < data.subStacks.size(); i++) {
            var subStack = data.subStacks.get(i);

            if (predicate.test(subStack)) {
                return true;
            }

            if (subStack.isOf(ThingsItems.AGGLOMERATION) && hasStack(subStack, predicate)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        var data = getDataFor(stack);

        for (int i = 0; i < data.subStacks.size(); i++) {
            ItemStack subStack = data.subStacks.get(i);

            TrinketsApi.getTrinket(subStack.getItem()).onEquip(subStack, slot, entity);

            data.updateStackIfNeeded(i);
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        var data = getDataFor(stack);

        for (int i = 0; i < data.subStacks.size(); i++) {
            ItemStack subStack = data.subStacks.get(i);

            TrinketsApi.getTrinket(subStack.getItem()).onUnequip(subStack, slot, entity);

            data.updateStackIfNeeded(i);
        }
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.tick(stack, slot, entity);

        var data = getDataFor(stack);

        for (int i = 0; i < data.subStacks.size(); i++) {
            ItemStack subStack = data.subStacks.get(i);

            TrinketsApi.getTrinket(subStack.getItem()).tick(subStack, slot, entity);

            data.updateStackIfNeeded(i);
        }
    }

    @Override
    public boolean canEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        var data = getDataFor(stack);

        for (int i = 0; i < data.subStacks.size(); i++) {
            ItemStack subStack = data.subStacks.get(i);

            if (!TrinketsApi.evaluatePredicateSet(slot.inventory().getSlotType().getValidatorPredicates(), subStack, slot, entity))  {
                return false;
            }

            if (!TrinketsApi.getTrinket(subStack.getItem()).canEquip(subStack, slot, entity)) {
                return false;
            }
        }

        return super.canEquip(stack, slot, entity);
    }

    @Override
    public boolean canUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        var data = getDataFor(stack);

        for (int i = 0; i < data.subStacks.size(); i++) {
            ItemStack subStack = data.subStacks.get(i);

            if (!TrinketsApi.getTrinket(subStack.getItem()).canUnequip(subStack, slot, entity)) {
                return false;
            }
        }

        return super.canUnequip(stack, slot, entity);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
        var modifiers = super.getModifiers(stack, slot, entity, uuid);

        var data = getDataFor(stack);

        for (int i = 0; i < data.subStacks.size(); i++) {
            ItemStack subStack = data.subStacks.get(i);

            modifiers.putAll(TrinketsApi.getTrinket(subStack.getItem()).getModifiers(subStack, slot, entity, uuid));
        }

        return modifiers;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (!Things.CONFIG.renderAgglomerationTrinket) return;

        var data = getDataFor(stack);

        for (int i = 0; i < data.subStacks.size(); i++) {
            ItemStack subStack = data.subStacks.get(i);

            var renderer = TrinketRendererRegistry.getRenderer(subStack.getItem()).orElse(null);

            if (renderer != null) {
                matrices.push();
                renderer.render(subStack, slotReference, contextModel, matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
                matrices.pop();
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        var data = getDataFor(stack);

        for (int i = 0; i < data.subStacks.size(); i++) {
            var subTooltip = data.subStacks.get(i).getTooltip(null, context);

            for (int j = 0; j < subTooltip.size(); j++) {
                if (j == 0) {
                    tooltip.add(Text.literal("• ").append(subTooltip.get(j)));
                } else {
                    tooltip.add(Text.literal("  ").append(subTooltip.get(j)));
                }
            }
        }
    }

    private static class StackData {
        private final ItemStack stack;
        private final List<ItemStack> subStacks = new ArrayList<>();
        private final List<ItemStack> defensiveCopies = new ArrayList<>();
        private NbtCompound defensiveNbtData;

        public StackData(ItemStack stack) {
            this.stack = stack;

            if (stack.hasNbt()) {
                this.defensiveNbtData = stack.getNbt().copy();

                var itemsTag = stack.getNbt().getList("Items", NbtElement.COMPOUND_TYPE);

                for (int i = 0; i < itemsTag.size(); i++) {
                    ItemStack subStack = ItemStack.fromNbt(itemsTag.getCompound(i));

                    subStacks.add(subStack);
                    defensiveCopies.add(subStack.copy());
                }
            }
        }

        public boolean isInvalid() {
            return !Objects.equals(stack.getNbt(), defensiveNbtData);
        }

        public void updateStackIfNeeded(int idx) {
            if (ItemStack.areEqual(subStacks.get(idx), defensiveCopies.get(idx))) return;

            defensiveCopies.set(idx, subStacks.get(idx).copy());

            var itemsTag = stack.getNbt().getList("Items", NbtElement.COMPOUND_TYPE);

            itemsTag.set(idx, subStacks.get(idx).writeNbt(new NbtCompound()));

            defensiveNbtData = stack.getNbt().copy();
        }
    }
}
