package com.glisco.things;

import com.glisco.things.blocks.ThingsBlocks;
import com.glisco.things.enchantments.RetributionEnchantment;
import com.glisco.things.items.ThingsItems;
import com.glisco.things.network.OpenEChestC2SPacket;
import com.glisco.things.network.PlaceItemC2SPacket;
import com.glisco.things.network.RequestTomeActionC2SPacket;
import dev.emi.trinkets.api.SlotGroups;
import dev.emi.trinkets.api.Slots;
import dev.emi.trinkets.api.TrinketSlots;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.RandomChanceWithLootingLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class ThingsCommon implements ModInitializer {

    public static final String MOD_ID = "things";

    public static final ItemGroup THINGS_ITEMS = FabricItemGroupBuilder.build(new Identifier("things", "things"), () -> new ItemStack(ThingsItems.BATER_WUCKET));

    public static final Enchantment RETRIBUTION = new RetributionEnchantment();

    public static final ScreenHandlerType<ScreenHandler> DISPLACEMENT_TOME_SCREEN_HANDLER;

    private static final ConfiguredFeature<?, ?> ORE_GLEAMING_OVERWORLD = Feature.ORE
            .configure(new OreFeatureConfig(
                    OreFeatureConfig.Rules.BASE_STONE_OVERWORLD,
                    ThingsBlocks.GLEAMING_ORE.getDefaultState(),
                    3))
            .decorate(Decorator.RANGE.configure(new RangeDecoratorConfig(
                    3,
                    0,
                    15)))
            .spreadHorizontally().repeat(5);

    static {
        DISPLACEMENT_TOME_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(MOD_ID, "displacement_tome"), DisplacementTomeScreenHandler::new);
    }

    private static boolean isPatchouliLoaded;

    @Override
    public void onInitialize() {

        TrinketSlots.addSlot(SlotGroups.LEGS, Slots.BELT, new Identifier("trinkets", "textures/item/empty_trinket_slot_belt.png"));
        TrinketSlots.addSlot(SlotGroups.HEAD, Slots.MASK, new Identifier("trinkets", "textures/item/empty_trinket_slot_mask.png"));
        TrinketSlots.addSlot(SlotGroups.CHEST, Slots.NECKLACE, new Identifier("trinkets", "textures/item/empty_trinket_slot_necklace.png"));
        TrinketSlots.addSlot(SlotGroups.FEET, Slots.CHARM, new Identifier("trinkets", "textures/item/empty_trinket_slot_charm.png"));
        TrinketSlots.addSlot(SlotGroups.HAND, Slots.GLOVES, new Identifier("trinkets", "textures/item/empty_trinket_slot_gloves.png"));
        TrinketSlots.addSlot(SlotGroups.FEET, Slots.AGLET, new Identifier("trinkets", "textures/item/empty_trinket_slot_aglet.png"));

        ThingsItems.register();
        ThingsBlocks.register();

        Registry.register(Registry.ENCHANTMENT, new Identifier(MOD_ID, "retribution"), RETRIBUTION);

        RegistryKey<ConfiguredFeature<?, ?>> oreGleamingOverworld = RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN,
                new Identifier(MOD_ID, "ore_gleaming_overworld"));
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, oreGleamingOverworld.getValue(), ORE_GLEAMING_OVERWORLD);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, oreGleamingOverworld);

        ServerPlayNetworking.registerGlobalReceiver(PlaceItemC2SPacket.ID, PlaceItemC2SPacket::onPacket);

        ServerPlayNetworking.registerGlobalReceiver(OpenEChestC2SPacket.ID, OpenEChestC2SPacket::onPacket);

        ServerPlayNetworking.registerGlobalReceiver(RequestTomeActionC2SPacket.ID, RequestTomeActionC2SPacket::onPacket);

        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, setter) -> {
            if (new Identifier("minecraft", "entities/squid").equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder().rolls(UniformLootTableRange.between(1, 3)).withEntry(ItemEntry.builder(ThingsItems.GLOWING_INK).build()).withCondition(RandomChanceWithLootingLootCondition.builder(0.05f, 0.025f).build());
                supplier.withPool(poolBuilder.build());
            }
        });

        isPatchouliLoaded = FabricLoader.getInstance().isModLoaded("patchouli");
    }

    public static boolean isPatchouliLoaded() {
        return isPatchouliLoaded;
    }
}
