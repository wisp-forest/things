package com.glisco.things;

import com.github.crimsondawn45.fabricshieldlib.lib.object.FabricShield;
import com.glisco.things.blocks.ThingsBlocks;
import com.glisco.things.enchantments.RetributionEnchantment;
import com.glisco.things.items.ThingsItems;
import com.glisco.things.misc.*;
import com.google.common.collect.ImmutableSet;
import com.mojang.brigadier.arguments.FloatArgumentType;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import io.wispforest.owo.Owo;
import io.wispforest.owo.particles.ClientParticles;
import io.wispforest.owo.particles.systems.ParticleSystem;
import io.wispforest.owo.particles.systems.ParticleSystemController;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import io.wispforest.owo.util.Maldenhagen;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.tag.BiomeTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Things implements ModInitializer, EntityComponentInitializer {

    public static final String MOD_ID = "things";

    public static ThingsConfig CONFIG;

    public static final ItemGroup THINGS_GROUP = FabricItemGroupBuilder.build(new Identifier("things", "things"), () -> new ItemStack(ThingsItems.BATER_WUCKET));
    public static final Enchantment RETRIBUTION = new RetributionEnchantment();
    public static final StatusEffect MOMENTUM = new MomentumStatusEffect();

    public static final ComponentKey<SockDataComponent> SOCK_DATA =
            ComponentRegistry.getOrCreate(id("sock_data"), SockDataComponent.class);

    public static final ScreenHandlerType<DisplacementTomeScreenHandler> DISPLACEMENT_TOME_SCREEN_HANDLER;

    private static final RegistryEntry<ConfiguredFeature<OreFeatureConfig, ?>> CONFIGURED_GLEAMING_ORE = ConfiguredFeatures.register("things:ore_gleaming",
            Feature.ORE, new OreFeatureConfig(List.of(
                    OreFeatureConfig.createTarget(OreConfiguredFeatures.STONE_ORE_REPLACEABLES, ThingsBlocks.GLEAMING_ORE.getDefaultState()),
                    OreFeatureConfig.createTarget(OreConfiguredFeatures.DEEPSLATE_ORE_REPLACEABLES, ThingsBlocks.DEEPSLATE_GLEAMING_ORE.getDefaultState())),
                    3));

    private static final RegistryEntry<PlacedFeature> GLEAMING_ORE = PlacedFeatures.register("things:ore_gleaming", CONFIGURED_GLEAMING_ORE, List.of(CountPlacementModifier.of(3),
            SquarePlacementModifier.of(),
            HeightRangePlacementModifier.uniform(YOffset.fixed(-15), YOffset.fixed(15)),
            BiomePlacementModifier.of()));

    public static final TagKey<Item> HARDENING_CATALYST_BLACKLIST = TagKey.of(Registry.ITEM_KEY, id("hardening_catalyst_blacklist"));

    static {
        DISPLACEMENT_TOME_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(id("displacement_tome"), DisplacementTomeScreenHandler::new);
    }

    private static Predicate<Item> SHIELD_PREDICATE = item -> item instanceof ShieldItem;
    private static Set<Item> BROKEN_WATCH_RECIPE;

    private static final ParticleSystemController CONTROLLER = new ParticleSystemController(id("particles"));
    public static final ParticleSystem<Void> TOGGLE_JUMP_BOOST_PARTICLES = CONTROLLER.register(Void.class, (world, pos, data) -> {
        ClientParticles.setParticleCount(25);
        ClientParticles.spawnPrecise(ParticleTypes.WAX_OFF, world, pos.add(0, 1, 0), 1, 2, 1);
    });

    @Override
    public void onInitialize() {

        AutoConfig.register(ThingsConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(ThingsConfig.class).getConfig();

        FieldRegistrationHandler.register(ThingsItems.class, MOD_ID, false);
        FieldRegistrationHandler.register(ThingsBlocks.class, MOD_ID, false);

        Registry.register(Registry.ENCHANTMENT, id("retribution"), RETRIBUTION);

        if (CONFIG.generateGleamingOre) {
            BiomeModifications.addFeature(overworldSelector(), GenerationStep.Feature.UNDERGROUND_ORES, GLEAMING_ORE.getKey().get());
            Maldenhagen.injectCopium(ThingsBlocks.GLEAMING_ORE);
        }

        Registry.register(Registry.RECIPE_TYPE, id("sock_upgrade_crafting"), SockUpgradeRecipe.Type.INSTANCE);
        Registry.register(Registry.RECIPE_SERIALIZER, id("sock_upgrade_crafting"), SockUpgradeRecipe.Serializer.INSTANCE);

        Registry.register(Registry.RECIPE_TYPE, id("jumpy_sock_crafting"), JumpySocksRecipe.Type.INSTANCE);
        Registry.register(Registry.RECIPE_SERIALIZER, id("jumpy_sock_crafting"), JumpySocksRecipe.Serializer.INSTANCE);

        Registry.register(Registry.STATUS_EFFECT, id("momentum"), MOMENTUM);

        ThingsNetwork.init();

        if (FabricLoader.getInstance().isModLoaded("fabricshieldlib")) {
            SHIELD_PREDICATE = SHIELD_PREDICATE.or(item -> item instanceof FabricShield);
        }

        BROKEN_WATCH_RECIPE = ImmutableSet.of(Items.LEATHER, Items.CLOCK, ThingsItems.GLEAMING_COMPOUND);

        if (Owo.DEBUG) {
            CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> {
                dispatcher.register(literal("things:set_walk_speed_modifier")
                        .then(argument("speed", FloatArgumentType.floatArg()).executes(context -> {
                            float speed = FloatArgumentType.getFloat(context, "speed");
                            SOCK_DATA.get(context.getSource().getPlayer()).setModifier(speed);
                            return 0;
                        })));
            });
        }
    }

    public static Predicate<BiomeSelectionContext> overworldSelector() {
        return context -> context.getBiomeRegistryEntry().isIn(BiomeTags.IS_OVERWORLD);
    }

    public static boolean isShield(Item item) {
        return SHIELD_PREDICATE.test(item);
    }

    public static Set<Item> brokenWatchRecipe() {
        return BROKEN_WATCH_RECIPE;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static TrinketComponent getTrinkets(LivingEntity entity) {
        return TrinketsApi.getTrinketComponent(entity).get();
    }

    public static boolean hasTrinket(LivingEntity entity, Item trinket) {
        return getTrinkets(entity).isEquipped(trinket);
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(SOCK_DATA, SockDataComponent::new, RespawnCopyStrategy.NEVER_COPY);
    }
}
