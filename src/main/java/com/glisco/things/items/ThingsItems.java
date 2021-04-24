package com.glisco.things.items;

import com.glisco.things.ThingsCommon;
import nerdhub.cardinal.components.api.event.ItemComponentCallbackV2;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosComponent;

import java.util.Collections;
import java.util.List;

public class ThingsItems {

    public static final Item RECALL_POTION = new RecallPotionItem();
    public static final Item CONTAINER_KEY = new ContainerKeyItem();
    public static final Item BATER_WUCKET = new BaterWucketItem();
    public static final Item ENDER_POUCH = new EnderPouchItem();
    public static final Item MONOCLE = new MonocleItem();
    public static final Item MOSS_NECKLACE = new MossNecklaceItem();
    public static final Item PLACEBO = new PlaceboItem();
    public static final Item DISPLACEMENT_TOME = new DisplacementTomeItem();
    public static final Item DISPLACEMENT_PAGE = new Item(new Item.Settings().group(ThingsCommon.THINGS_ITEMS).maxCount(8));
    public static final Item GLOWING_INK = new Item(new Item.Settings().group(ThingsCommon.THINGS_ITEMS));
    public static final Item MINING_GLOVES = new MiningGlovesItem();
    public static final Item RIOT_GAUNTLET = new RiotGauntletItem();
    public static final Item INFERNAL_SCEPTER = new InfernalScepterItem();
    public static final Item HADES_CRYSTAL = new HadesCrystalItem();
    public static final Item ENCHANTED_WAX_GLAND = new EnchantedWaxGlandItem();
    public static final Item ITEM_MAGNET = new ItemMagnetItem();
    public static final Item RABBIT_FOOT_CHARM = new RabbitFootCharmItem();
    public static final Item LUCK_OF_THE_IRISH = new LuckOfTheIrishItem();

    public static final Item HARDENING_CRYSTAL = new ItemWithOptionalTooltip(new Item.Settings().group(ThingsCommon.THINGS_ITEMS).maxCount(1).rarity(Rarity.UNCOMMON).fireproof()) {
        @Override
        List<Text> getTooltipText() {
            return Collections.singletonList(new LiteralText("§7Apply in an Anvil to make any item unbreakable"));
        }

        @Override
        public boolean hasGlint(ItemStack stack) {
            return true;
        }
    };

    public static final Item GLEAMING_POWDER = new Item(new Item.Settings().group(ThingsCommon.THINGS_ITEMS).rarity(Rarity.UNCOMMON)) {
        @Override
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            tooltip.add(new LiteralText("Crafting component").formatted(Formatting.GRAY));
        }
    };
    public static final Item GLEAMING_COMPOUND = new Item(new Item.Settings().group(ThingsCommon.THINGS_ITEMS).rarity(Rarity.UNCOMMON)) {
        @Override
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            tooltip.add(new LiteralText("Crafting component").formatted(Formatting.GRAY));
        }
    };

    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    public static void register() {
        Registry.register(Registry.ITEM, new Identifier(ThingsCommon.MOD_ID, "hardening_catalyst"), ThingsItems.HARDENING_CRYSTAL);
        Registry.register(Registry.ITEM, new Identifier(ThingsCommon.MOD_ID, "container_key"), ThingsItems.CONTAINER_KEY);
        Registry.register(Registry.ITEM, new Identifier(ThingsCommon.MOD_ID, "recall_potion"), ThingsItems.RECALL_POTION);
        Registry.register(Registry.ITEM, new Identifier(ThingsCommon.MOD_ID, "bater_wucket"), ThingsItems.BATER_WUCKET);
        Registry.register(Registry.ITEM, new Identifier(ThingsCommon.MOD_ID, "ender_pouch"), ThingsItems.ENDER_POUCH);
        Registry.register(Registry.ITEM, new Identifier(ThingsCommon.MOD_ID, "monocle"), ThingsItems.MONOCLE);
        Registry.register(Registry.ITEM, new Identifier(ThingsCommon.MOD_ID, "moss_necklace"), ThingsItems.MOSS_NECKLACE);
        Registry.register(Registry.ITEM, new Identifier(ThingsCommon.MOD_ID, "placebo"), ThingsItems.PLACEBO);
        Registry.register(Registry.ITEM, new Identifier(ThingsCommon.MOD_ID, "displacement_tome"), ThingsItems.DISPLACEMENT_TOME);
        Registry.register(Registry.ITEM, new Identifier(ThingsCommon.MOD_ID, "displacement_page"), ThingsItems.DISPLACEMENT_PAGE);
        Registry.register(Registry.ITEM, new Identifier(ThingsCommon.MOD_ID, "glowing_ink"), ThingsItems.GLOWING_INK);
        Registry.register(Registry.ITEM, new Identifier(ThingsCommon.MOD_ID, "mining_gloves"), ThingsItems.MINING_GLOVES);
        Registry.register(Registry.ITEM, new Identifier(ThingsCommon.MOD_ID, "riot_gauntlet"), ThingsItems.RIOT_GAUNTLET);
        Registry.register(Registry.ITEM, new Identifier(ThingsCommon.MOD_ID, "infernal_scepter"), ThingsItems.INFERNAL_SCEPTER);
        Registry.register(Registry.ITEM, new Identifier(ThingsCommon.MOD_ID, "gleaming_powder"), ThingsItems.GLEAMING_POWDER);
        Registry.register(Registry.ITEM, new Identifier(ThingsCommon.MOD_ID, "gleaming_compound"), ThingsItems.GLEAMING_COMPOUND);
        Registry.register(Registry.ITEM, new Identifier(ThingsCommon.MOD_ID, "hades_crystal"), ThingsItems.HADES_CRYSTAL);
        Registry.register(Registry.ITEM, new Identifier(ThingsCommon.MOD_ID, "enchanted_wax_gland"), ThingsItems.ENCHANTED_WAX_GLAND);
        Registry.register(Registry.ITEM, new Identifier(ThingsCommon.MOD_ID, "item_magnet"), ThingsItems.ITEM_MAGNET);
        Registry.register(Registry.ITEM, new Identifier(ThingsCommon.MOD_ID, "rabbit_foot_charm"), ThingsItems.RABBIT_FOOT_CHARM);
        Registry.register(Registry.ITEM, new Identifier(ThingsCommon.MOD_ID, "luck_of_the_irish"), ThingsItems.LUCK_OF_THE_IRISH);

        if (ThingsCommon.CONFIG.appleCurio) {
            ItemComponentCallbackV2.event(Items.APPLE).register((item, itemStack, componentContainer) -> componentContainer.put(CuriosComponent.ITEM, new AppleCurio()));
        }

        ItemComponentCallbackV2.event(ENCHANTED_WAX_GLAND).register((item, itemStack, componentContainer) -> componentContainer.put(CuriosComponent.ITEM, new EnchantedWaxGlandItem.Curio()));
        ItemComponentCallbackV2.event(HADES_CRYSTAL).register((item, itemStack, componentContainer) -> componentContainer.put(CuriosComponent.ITEM, new HadesCrystalItem.Curio()));
        ItemComponentCallbackV2.event(MONOCLE).register((item, itemStack, componentContainer) -> componentContainer.put(CuriosComponent.ITEM, new MonocleItem.Curio()));
        ItemComponentCallbackV2.event(MOSS_NECKLACE).register((item, itemStack, componentContainer) -> componentContainer.put(CuriosComponent.ITEM, new MossNecklaceItem.Curio()));
        ItemComponentCallbackV2.event(RABBIT_FOOT_CHARM).register((item, itemStack, componentContainer) -> componentContainer.put(CuriosComponent.ITEM, new RabbitFootCharmItem.Curio()));
        ItemComponentCallbackV2.event(RIOT_GAUNTLET).register((item, itemStack, componentContainer) -> componentContainer.put(CuriosComponent.ITEM, new RiotGauntletItem.Curio()));
        ItemComponentCallbackV2.event(MINING_GLOVES).register((item, itemStack, componentContainer) -> componentContainer.put(CuriosComponent.ITEM, new MiningGlovesItem.Curio()));
    }
}
