package com.glisco.things.items.trinkets;

import com.glisco.things.ThingsCommon;
import com.glisco.things.client.SimplePlayerTrinketRenderer;
import com.glisco.things.items.TrinketItemWithOptionalTooltip;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3f;

import java.util.Collections;
import java.util.List;

public class MonocleItem extends TrinketItemWithOptionalTooltip implements SimplePlayerTrinketRenderer {

    public MonocleItem() {
        super(new Settings().maxCount(1).group(ThingsCommon.THINGS_GROUP));
    }

    @Override
    public List<Text> getExtendedTooltip() {
        return Collections.singletonList(new LiteralText("§7Grants permanent Night Vision"));
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!(entity instanceof ServerPlayerEntity player)) return;

        player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 610, 0, true, false, true));
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!(entity instanceof ServerPlayerEntity player)) return;

        player.removeStatusEffect(StatusEffects.NIGHT_VISION);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void align(ClientPlayerEntity player, PlayerEntityModel<AbstractClientPlayerEntity> model, MatrixStack matrices, float headYaw, float headPitch) {
        TrinketRenderer.translateToFace(matrices, model, player, headYaw, headPitch);

        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
        matrices.scale(.5f, .5f, .5f);
        matrices.translate(.25, -.215, -.05f);
    }
}
