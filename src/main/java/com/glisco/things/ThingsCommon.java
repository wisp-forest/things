package com.glisco.things;

import com.glisco.things.blocks.ThingsBlocks;
import com.glisco.things.enchantments.RetributionEnchantment;
import com.glisco.things.items.ThingsItems;
import com.glisco.things.network.OpenEChestC2SPacket;
import com.glisco.things.network.PlaceItemC2SPacket;
import com.glisco.things.network.RequestTomeActionC2SPacket;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

public class ThingsCommon implements ModInitializer {

    public static ThingsConfig CONFIG;

    public static final String MOD_ID = "things";

    public static final ItemGroup THINGS_ITEMS = FabricItemGroupBuilder.build(new Identifier("things", "things"), () -> new ItemStack(ThingsItems.BATER_WUCKET));

    public static final Enchantment RETRIBUTION = new RetributionEnchantment();

    public static final StatusEffect MOMENTUM = new MomentumStatusEffect();

    public static final ScreenHandlerType<ScreenHandler> DISPLACEMENT_TOME_SCREEN_HANDLER;

    private static final ConfiguredFeature<?, ?> ORE_GLEAMING_OVERWORLD = Feature.ORE
            .configure(new OreFeatureConfig(
                    OreFeatureConfig.Rules.BASE_STONE_OVERWORLD,
                    ThingsBlocks.GLEAMING_ORE.getDefaultState(),
                    3))
            .decorate(Decorator.RANGE.configure(new RangeDecoratorConfig(UniformHeightProvider.create(
                    YOffset.fixed(0),
                    YOffset.fixed(15)))))
            .spreadHorizontally().repeat(5);

    static {
        DISPLACEMENT_TOME_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(MOD_ID, "displacement_tome"), DisplacementTomeScreenHandler::new);
    }

    private static boolean isPatchouliLoaded;

    @Override
    public void onInitialize() {

        AutoConfig.register(ThingsConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(ThingsConfig.class).getConfig();

        ThingsItems.register();
        ThingsBlocks.register();

        Registry.register(Registry.ENCHANTMENT, new Identifier(MOD_ID, "retribution"), RETRIBUTION);

        RegistryKey<ConfiguredFeature<?, ?>> oreGleamingOverworld = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY,
                new Identifier(MOD_ID, "ore_gleaming_overworld"));
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, oreGleamingOverworld.getValue(), ORE_GLEAMING_OVERWORLD);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, oreGleamingOverworld);

        ServerPlayNetworking.registerGlobalReceiver(PlaceItemC2SPacket.ID, PlaceItemC2SPacket::onPacket);
        ServerPlayNetworking.registerGlobalReceiver(OpenEChestC2SPacket.ID, OpenEChestC2SPacket::onPacket);
        ServerPlayNetworking.registerGlobalReceiver(RequestTomeActionC2SPacket.ID, RequestTomeActionC2SPacket::onPacket);

        Registry.register(Registry.STATUS_EFFECT, new Identifier(MOD_ID, "momentum"), MOMENTUM);

        isPatchouliLoaded = FabricLoader.getInstance().isModLoaded("patchouli");

        UseBlockCallback.EVENT.register((playerEntity, world, hand, blockHitResult) -> {
            if (!isPatchouliLoaded()) return ActionResult.PASS;

            if (playerEntity.getMainHandStack().getItem() == Items.BOOK && world.getBlockState(blockHitResult.getBlockPos()).isOf(ThingsBlocks.GLEAMING_ORE)) {
                if (!world.isClient) {
                    playerEntity.getMainHandStack().decrement(1);

                    ItemStack book = new ItemStack(Registry.ITEM.get(new Identifier("patchouli", "guide_book")));
                    book.getOrCreateNbt().putString("patchouli:book", "things:things_guide");
                    playerEntity.getInventory().offerOrDrop(book);
                }
                return ActionResult.SUCCESS;
            } else {
                return ActionResult.PASS;
            }
        });
    }

    public static boolean isPatchouliLoaded() {
        return isPatchouliLoaded;
    }

}
