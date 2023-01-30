package com.glisco.things.client;

import com.glisco.things.blocks.PlacedItemBlockEntity;
import com.glisco.things.blocks.ThingsBlocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.RotationAxis;

public class PlacedItemBlockEntityRenderer implements BlockEntityRenderer<PlacedItemBlockEntity> {

    public PlacedItemBlockEntityRenderer(BlockEntityRendererFactory.Context context) {}

    @Override
    public void render(PlacedItemBlockEntity entity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemStack item = entity.getItem();
        BakedModel itemModel = MinecraftClient.getInstance().getItemRenderer().getModel(item, null, null, 0);

        float scaleFactor = item.getItem() instanceof BlockItem ? 0.5f : 0.4f;

        if (!entity.getWorld().getBlockState(entity.getPos()).isOf(ThingsBlocks.PLACED_ITEM)) return;

        matrixStack.push();
        switch (entity.getWorld().getBlockState(entity.getPos()).get(Properties.FACING)) {
            case UP -> {
                matrixStack.translate(0.5, 0.97, 0.5);
                matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
                matrixStack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(entity.getRotation() * 45));
            }
            case DOWN -> {
                matrixStack.translate(0.5, 0.03, 0.5);
                matrixStack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(entity.getRotation() * 45));
            }
            case EAST -> {
                matrixStack.translate(0.97, 0.5, 0.5);
                matrixStack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90 - entity.getRotation() * 45));
                matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90));
            }
            case WEST -> {
                matrixStack.translate(0.03, 0.5, 0.5);
                matrixStack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90 + entity.getRotation() * 45));
                matrixStack.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(90));
            }
            case NORTH -> {
                matrixStack.translate(0.5, 0.5, 0.03);
                matrixStack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));
                matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-entity.getRotation() * 45));
            }
            case SOUTH -> {
                matrixStack.translate(0.5, 0.5, 0.97);
                matrixStack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));
                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-entity.getRotation() * 45));
            }
        }
        matrixStack.scale(scaleFactor, scaleFactor, scaleFactor);
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f));
        MinecraftClient.getInstance().getItemRenderer().renderItem(item, ModelTransformation.Mode.FIXED, false, matrixStack, vertexConsumers, light, OverlayTexture.DEFAULT_UV, itemModel);
        matrixStack.pop();
    }
}
