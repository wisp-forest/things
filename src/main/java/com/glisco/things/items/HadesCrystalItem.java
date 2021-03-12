package com.glisco.things.items;

import com.glisco.things.ThingsCommon;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import top.theillusivec4.curios.api.type.component.ICurio;

import java.util.ArrayList;
import java.util.List;

public class HadesCrystalItem extends ItemWithOptionalTooltip {

    private static final List<Text> TOOLTIP;

    static {
        TOOLTIP = new ArrayList<>();
        TOOLTIP.add(new LiteralText("§7Grants permanent Fire Resistance"));
        TOOLTIP.add(new LiteralText("§7Wear together with a §6Wax Gland §7for extra awesomeness"));
    }

    public HadesCrystalItem() {
        super(new Settings().group(ThingsCommon.THINGS_ITEMS).maxCount(1));
    }

    @Override
    List<Text> getTooltipText() {
        return TOOLTIP;
    }

    public static class Curio implements ICurio {
        @Override
        public void curioTick(String identifier, int index, LivingEntity livingEntity) {
            if (!(livingEntity instanceof ServerPlayerEntity)) return;
            ServerPlayerEntity player = (ServerPlayerEntity) livingEntity;

            player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 5, 0, true, false, true));
            if (player.isOnFire()) player.setFireTicks(0);
        }
    }
}
