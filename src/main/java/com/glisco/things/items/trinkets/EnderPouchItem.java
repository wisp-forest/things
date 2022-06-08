package com.glisco.things.items.trinkets;

import com.glisco.things.Things;
import com.glisco.things.client.SimplePlayerTrinketRenderer;
import com.glisco.things.client.ThingsClient;
import com.glisco.things.items.TrinketItemWithOptionalTooltip;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3f;

import java.util.List;

public class EnderPouchItem extends TrinketItemWithOptionalTooltip implements SimplePlayerTrinketRenderer {

    public EnderPouchItem() {
        super(new Settings().maxCount(1).group(Things.THINGS_GROUP));
    }

    @Override
    public void append(List<Text> tooltip) {
        tooltip.add(Text.translatable(tooltipTranslationKey(), KeyBindingHelper.getBoundKeyOf(ThingsClient.OPEN_ENDER_CHEST).getLocalizedText()));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void align(AbstractClientPlayerEntity player, PlayerEntityModel<AbstractClientPlayerEntity> model, MatrixStack matrices, float headYaw, float headPitch) {
        TrinketRenderer.translateToChest(matrices, model, player);
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90));
        matrices.scale(.35f, .35f, .35f);
        matrices.translate(-.45, -.8, .725);
    }
}
