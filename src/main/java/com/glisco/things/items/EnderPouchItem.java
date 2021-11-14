package com.glisco.things.items;

import com.glisco.things.ThingsCommon;
import com.glisco.things.client.SimplePlayerTrinketRenderer;
import com.glisco.things.client.ThingsClient;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import net.minecraft.util.math.Vec3f;

import java.util.Collections;
import java.util.List;

public class EnderPouchItem extends TrinketItemWithOptionalTooltip implements SimplePlayerTrinketRenderer {

    public EnderPouchItem() {
        super(new Settings().maxCount(1).group(ThingsCommon.THINGS_ITEMS));
    }

    @Override
    public List<Text> getExtendedTooltip() {
        return Collections.singletonList(new LiteralText(String.format("§7Press §6%s §7to open Ender Chest Inventory",
                Language.getInstance().get(KeyBindingHelper.getBoundKeyOf(ThingsClient.OPEN_ENDER_CHEST).getLocalizedText().getString()))));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void align(ClientPlayerEntity player, PlayerEntityModel<AbstractClientPlayerEntity> model, MatrixStack matrices, float headYaw, float headPitch) {
        TrinketRenderer.translateToChest(matrices, model, player);
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90));
        matrices.scale(.35f, .35f, .35f);
        matrices.translate(-.45, -.8, .725);
    }
}
