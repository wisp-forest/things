package com.glisco.things.items;

import com.glisco.things.ThingsCommon;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.component.ICurio;

import java.util.ArrayList;
import java.util.List;

public class EnchantedWaxGlandItem extends ItemWithOptionalTooltip {

    private static final List<Text> TOOLTIP;

    static {
        TOOLTIP = new ArrayList<>();
        TOOLTIP.add(new LiteralText("§7Makes you float in water."));
        TOOLTIP.add(new LiteralText("§7And apparently also really fast"));
    }

    public EnchantedWaxGlandItem() {
        super(new Settings().group(ThingsCommon.THINGS_ITEMS).maxCount(1));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    List<Text> getTooltipText() {
        return TOOLTIP;
    }

    public static class Curio implements ICurio {

        @Override
        public void curioTick(String identifier, int index, LivingEntity livingEntity) {

            if (!(livingEntity instanceof PlayerEntity)) return;
            PlayerEntity player = (PlayerEntity) livingEntity;

            if (player.isTouchingWater()) {
                player.addVelocity(0, 0.005, 0);
            } else if (player.isInLava() && CuriosApi.getCuriosHelper().findEquippedCurio(ThingsItems.HADES_CRYSTAL, player).isPresent()) {
                player.addVelocity(0, 0.02, 0);
            }
        }
    }
}
