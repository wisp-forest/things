package com.glisco.things.items.trinkets;

import com.glisco.things.Things;
import com.glisco.things.items.TrinketItemWithOptionalTooltip;
import com.google.common.collect.Multimap;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class ArmExtenderItem extends TrinketItemWithOptionalTooltip {
    private static final UUID REACH_MODIFIER_ID = UUID.fromString("0919CA5B-3771-48E9-86B7-E062AE0D709B");
    private static final UUID ATTACK_DISTANCE_MODIFIER_ID = UUID.fromString("DABEB32B-FAC1-48BA-A48D-A31B3B449FA2");

    public ArmExtenderItem() {
        super(new Settings().maxCount(1).group(Things.THINGS_GROUP));
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
        var map = super.getModifiers(stack, slot, entity, uuid);

        map.put(ReachEntityAttributes.REACH,
            new EntityAttributeModifier(REACH_MODIFIER_ID, "Trinket modifier", 2.0, EntityAttributeModifier.Operation.ADDITION));
        map.put(ReachEntityAttributes.ATTACK_RANGE,
            new EntityAttributeModifier(ATTACK_DISTANCE_MODIFIER_ID, "Trinket modifier", 2.0, EntityAttributeModifier.Operation.ADDITION));

        return map;
    }
}
