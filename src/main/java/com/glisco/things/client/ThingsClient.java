package com.glisco.things.client;

import com.glisco.things.Things;
import com.glisco.things.blocks.ThingsBlocks;
import com.glisco.things.items.ThingsItems;
import com.glisco.things.items.generic.DisplacementTomeItem;
import com.glisco.things.items.trinkets.AppleTrinket;
import com.glisco.things.items.trinkets.SocksItem;
import com.glisco.things.network.OpenEChestC2SPacket;
import com.glisco.things.network.PlaceItemC2SPacket;
import com.glisco.things.network.UpdateDisplacementTomeS2CPacket;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import io.wispforest.owo.particles.ClientParticles;
import io.wispforest.owo.particles.ServerParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class ThingsClient implements ClientModInitializer {

    public static KeyBinding PLACE_ITEM;
    public static KeyBinding OPEN_ENDER_CHEST;

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(ThingsBlocks.PLACED_ITEM_BLOCK_ENTITY, PlacedItemBlockEntityRenderer::new);

        ScreenRegistry.register(Things.DISPLACEMENT_TOME_SCREEN_HANDLER, DisplacementTomeScreen::new);
        ClientPlayNetworking.registerGlobalReceiver(UpdateDisplacementTomeS2CPacket.ID, UpdateDisplacementTomeS2CPacket::onPacket);

        FabricModelPredicateProviderRegistry.register(ThingsItems.DISPLACEMENT_TOME, new Identifier("pages"), new DisplacementTomeItem.PredicateProvider());
        FabricModelPredicateProviderRegistry.register(ThingsItems.SOCKS, new Identifier("jumpy"), (stack, world, entity, seed) -> stack.getOrCreateNbt().getBoolean(SocksItem.JUMPY_KEY) ? 1 : 0);

        TrinketRendererRegistry.registerRenderer(Items.APPLE, new AppleTrinket.Renderer());

        registerRenderedTrinket(ThingsItems.ENCHANTED_WAX_GLAND);
        registerRenderedTrinket(ThingsItems.ENDER_POUCH);
        registerRenderedTrinket(ThingsItems.HADES_CRYSTAL);
        registerRenderedTrinket(ThingsItems.LUCK_OF_THE_IRISH);
        registerRenderedTrinket(ThingsItems.MONOCLE);
        registerRenderedTrinket(ThingsItems.MOSS_NECKLACE);

        PLACE_ITEM = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.things.place_item",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_J,
                "category.things.things"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (PLACE_ITEM.wasPressed()) {
                HitResult target = client.crosshairTarget;
                if (target.getType().equals(HitResult.Type.BLOCK)) {
                    client.getNetworkHandler().sendPacket(PlaceItemC2SPacket.create((BlockHitResult) target));
                }
            }
        });

        OPEN_ENDER_CHEST = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.things.openenderchest",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "category.things.things"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_ENDER_CHEST.wasPressed()) {
                if (TrinketsApi.getTrinketComponent(client.player).get().isEquipped(ThingsItems.ENDER_POUCH)) {
                    client.getNetworkHandler().sendPacket(OpenEChestC2SPacket.create());
                }
            }
        });

        ServerParticles.registerClientSideHandler(Things.id("toggle_jump_boost"), (client, pos, data) -> {
            client.execute(() -> {
                ClientParticles.setParticleCount(25);
                ClientParticles.spawnPrecise(ParticleTypes.WAX_OFF, client.world, pos.add(0, 1, 0), 1, 2, 1);
            });
        });
    }

    private void registerRenderedTrinket(Item trinket) {
        TrinketRendererRegistry.registerRenderer(trinket, (TrinketRenderer) trinket);
    }
}
