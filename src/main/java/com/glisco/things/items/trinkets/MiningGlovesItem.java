package com.glisco.things.items.trinkets;

import com.glisco.things.ThingsCommon;
import com.glisco.things.items.TrinketItemWithOptionalTooltip;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;

public class MiningGlovesItem extends TrinketItemWithOptionalTooltip {

    public MiningGlovesItem() {
        super(new Settings().maxCount(1).group(ThingsCommon.THINGS_GROUP));
    }

    @Override
    public List<Text> getExtendedTooltip() {
        return Collections.singletonList(new LiteralText("§7Grants permanent Haste II"));
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!(entity instanceof ServerPlayerEntity player)) return;

        player.addStatusEffect(new StatusEffectInstance(ThingsCommon.MOMENTUM, 5,
                ThingsCommon.CONFIG.effectLevels.miningGloveMomentum - 1, true, false, true));
    }
}