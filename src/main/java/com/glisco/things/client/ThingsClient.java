package com.glisco.things.client;

import com.glisco.things.ThingsCommon;
import com.glisco.things.blocks.ThingsBlocks;
import com.glisco.things.items.AppleTrinket;
import com.glisco.things.items.DisplacementTomeItem;
import com.glisco.things.items.ThingsItems;
import com.glisco.things.network.OpenEChestC2SPacket;
import com.glisco.things.network.PlaceItemC2SPacket;
import com.glisco.things.network.UpdateDisplacementTomeS2CPacket;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class ThingsClient implements ClientModInitializer {

    public static KeyBinding placeItem;
    public static KeyBinding openEChest;

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(ThingsBlocks.PLACED_ITEM_BLOCK_ENTITY, PlacedItemBlockEntityRenderer::new);

        ScreenRegistry.register(ThingsCommon.DISPLACEMENT_TOME_SCREEN_HANDLER, DisplacementTomeScreen::new);
        ClientPlayNetworking.registerGlobalReceiver(UpdateDisplacementTomeS2CPacket.ID, UpdateDisplacementTomeS2CPacket::onPacket);

        FabricModelPredicateProviderRegistry.register(ThingsItems.DISPLACEMENT_TOME, new Identifier("pages"), new DisplacementTomeItem.PredicateProvider());

        placeItem = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.things.place_item",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_J,
                "category.things.things"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (placeItem.wasPressed()) {
                HitResult target = client.crosshairTarget;
                if (target.getType().equals(HitResult.Type.BLOCK)) {
                    client.getNetworkHandler().sendPacket(PlaceItemC2SPacket.create((BlockHitResult) target));
                }
            }
        });

        TrinketRendererRegistry.registerRenderer(Items.APPLE, new AppleTrinket.Renderer());

        registerRenderedTrinket(ThingsItems.ENCHANTED_WAX_GLAND);
        registerRenderedTrinket(ThingsItems.ENDER_POUCH);
        registerRenderedTrinket(ThingsItems.HADES_CRYSTAL);
        registerRenderedTrinket(ThingsItems.LUCK_OF_THE_IRISH);
        registerRenderedTrinket(ThingsItems.MONOCLE);
        registerRenderedTrinket(ThingsItems.MOSS_NECKLACE);

        openEChest = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.things.openenderchest",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "category.things.things"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openEChest.wasPressed()) {
                if (TrinketsApi.getTrinketComponent(client.player).get().isEquipped(ThingsItems.ENDER_POUCH)) {
                    client.getNetworkHandler().sendPacket(OpenEChestC2SPacket.create());
                }
            }
        });
    }

    private void registerRenderedTrinket(Item trinket) {
        TrinketRendererRegistry.registerRenderer(trinket, (TrinketRenderer) trinket);
    }
}
