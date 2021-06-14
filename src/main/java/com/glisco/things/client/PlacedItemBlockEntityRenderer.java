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
import net.minecraft.util.math.Vec3f;

public class PlacedItemBlockEntityRenderer implements BlockEntityRenderer<PlacedItemBlockEntity> {

    public PlacedItemBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        
    }
    
    @Override
    public void render(PlacedItemBlockEntity entity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.getItem() != null) {
            ItemStack item = entity.getItem();
            BakedModel itemModel = MinecraftClient.getInstance().getItemRenderer().getHeldItemModel(item, null, null, 0);

            float scaleFactor = item.getItem() instanceof BlockItem ? 0.5f : 0.4f;

            if (!entity.getWorld().getBlockState(entity.getPos()).isOf(ThingsBlocks.PLACED_ITEM)) return;

            matrixStack.push();
            switch (entity.getWorld().getBlockState(entity.getPos()).get(Properties.FACING)) {
                case UP:
                    matrixStack.translate(0.5, 0.97, 0.5);
                    matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180));
                    matrixStack.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(entity.getRotation() * 45));
                    break;
                case DOWN:
                    matrixStack.translate(0.5, 0.03, 0.5);
                    matrixStack.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(entity.getRotation() * 45));
                    break;
                case EAST:
                    matrixStack.translate(0.97, 0.5, 0.5);
                    matrixStack.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(90 - entity.getRotation() * 45));
                    matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90));
                    break;
                case WEST:
                    matrixStack.translate(0.03, 0.5, 0.5);
                    matrixStack.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(90 + entity.getRotation() * 45));
                    matrixStack.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(90));
                    break;
                case NORTH:
                    matrixStack.translate(0.5, 0.5, 0.03);
                    matrixStack.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(90));
                    matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180));
                    matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-entity.getRotation() * 45));
                    break;
                case SOUTH:
                    matrixStack.translate(0.5, 0.5, 0.97);
                    matrixStack.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(90));
                    matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-entity.getRotation() * 45));
                    break;
            }
            matrixStack.scale(scaleFactor, scaleFactor, scaleFactor);
            matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90f));
            MinecraftClient.getInstance().getItemRenderer().renderItem(item, ModelTransformation.Mode.FIXED, false, matrixStack, vertexConsumers, light, OverlayTexture.DEFAULT_UV, itemModel);
            matrixStack.pop();
        }
    }
}
