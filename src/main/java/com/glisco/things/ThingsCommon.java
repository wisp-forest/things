package com.glisco.things;

import com.glisco.things.blocks.ThingsBlocks;
import com.glisco.things.enchantments.RetributionEnchantment;
import com.glisco.things.items.ThingsItems;
import com.glisco.things.network.OpenEChestC2SPacket;
import com.glisco.things.network.PlaceItemC2SPacket;
import com.glisco.things.network.RequestTomeActionC2SPacket;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.BiomePlacementModifier;
import net.minecraft.world.gen.decorator.CountPlacementModifier;
import net.minecraft.world.gen.decorator.HeightRangePlacementModifier;
import net.minecraft.world.gen.decorator.SquarePlacementModifier;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreConfiguredFeatures;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;

import java.util.List;

public class ThingsCommon implements ModInitializer {

    public static final String MOD_ID = "things";

    public static ThingsConfig CONFIG;

    public static final ItemGroup THINGS_GROUP = FabricItemGroupBuilder.build(new Identifier("things", "things"), () -> new ItemStack(ThingsItems.BATER_WUCKET));
    public static final Enchantment RETRIBUTION = new RetributionEnchantment();
    public static final StatusEffect MOMENTUM = new MomentumStatusEffect();

    public static final ScreenHandlerType<DisplacementTomeScreenHandler> DISPLACEMENT_TOME_SCREEN_HANDLER;

    private static final PlacedFeature GLEAMING_ORE = new PlacedFeature(() ->
            Feature.ORE.configure(new OreFeatureConfig(List.of(
                    OreFeatureConfig.createTarget(OreConfiguredFeatures.STONE_ORE_REPLACEABLES, ThingsBlocks.GLEAMING_ORE.getDefaultState()),
                    OreFeatureConfig.createTarget(OreConfiguredFeatures.DEEPSLATE_ORE_REPLACEABLES, ThingsBlocks.DEEPSLATE_GLEAMING_ORE.getDefaultState())),
                    3)),
            List.of(CountPlacementModifier.of(3),
                    SquarePlacementModifier.of(),
                    HeightRangePlacementModifier.uniform(YOffset.fixed(-15), YOffset.fixed(15)),
                    BiomePlacementModifier.of()));

    static {
        DISPLACEMENT_TOME_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(id("displacement_tome"), DisplacementTomeScreenHandler::new);
    }

    @Override
    public void onInitialize() {

        AutoConfig.register(ThingsConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(ThingsConfig.class).getConfig();

        FieldRegistrationHandler.register(ThingsItems.class, MOD_ID, false);
        FieldRegistrationHandler.register(ThingsBlocks.class, MOD_ID, false);

        Registry.register(Registry.ENCHANTMENT, id("retribution"), RETRIBUTION);

        RegistryKey<PlacedFeature> gleamingOre = RegistryKey.of(Registry.PLACED_FEATURE_KEY, id("ore_gleaming"));
        Registry.register(BuiltinRegistries.PLACED_FEATURE, gleamingOre, GLEAMING_ORE);

        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, gleamingOre);

        ServerPlayNetworking.registerGlobalReceiver(PlaceItemC2SPacket.ID, PlaceItemC2SPacket::onPacket);
        ServerPlayNetworking.registerGlobalReceiver(OpenEChestC2SPacket.ID, OpenEChestC2SPacket::onPacket);
        ServerPlayNetworking.registerGlobalReceiver(RequestTomeActionC2SPacket.ID, RequestTomeActionC2SPacket::onPacket);

        Registry.register(Registry.STATUS_EFFECT, id("momentum"), MOMENTUM);
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

}
