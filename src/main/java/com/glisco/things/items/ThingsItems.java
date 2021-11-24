package com.glisco.things.items;

import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import com.glisco.things.ThingsCommon;
import com.glisco.things.mixin.ItemAccessor;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ThingsItems implements ItemRegistryContainer {

    public static final Item RECALL_POTION = new RecallPotionItem();
    public static final Item CONTAINER_KEY = new ContainerKeyItem();
    public static final Item BATER_WUCKET = new BaterWucketItem();
    public static final Item ENDER_POUCH = new EnderPouchItem();
    public static final Item MONOCLE = new MonocleItem();
    public static final Item MOSS_NECKLACE = new MossNecklaceItem();
    public static final Item PLACEBO = new PlaceboItem();
    public static final Item DISPLACEMENT_TOME = new DisplacementTomeItem();
    public static final Item DISPLACEMENT_PAGE = new Item(new Item.Settings().group(ThingsCommon.THINGS_ITEMS).maxCount(8));
    public static final Item MINING_GLOVES = new MiningGlovesItem();
    public static final Item RIOT_GAUNTLET = new RiotGauntletItem();
    public static final Item INFERNAL_SCEPTER = new InfernalScepterItem();
    public static final Item HADES_CRYSTAL = new HadesCrystalItem();
    public static final Item ENCHANTED_WAX_GLAND = new EnchantedWaxGlandItem();
    public static final Item ITEM_MAGNET = new ItemMagnetItem();
    public static final Item RABBIT_FOOT_CHARM = new RabbitFootCharmItem();
    public static final Item LUCK_OF_THE_IRISH = new LuckOfTheIrishItem();

    public static final Item HARDENING_CATALYST = new ItemWithExtendableTooltip(new Item.Settings().group(ThingsCommon.THINGS_ITEMS).maxCount(1).rarity(Rarity.UNCOMMON).fireproof()) {
        @Override
        public List<Text> getExtendedTooltip() {
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

    @Override
    public void afterFieldProcessing() {
        if (ThingsCommon.CONFIG.appleTrinket) {
            TrinketsApi.registerTrinket(Items.APPLE, new AppleTrinket());
        }

        ((ItemAccessor) BATER_WUCKET).things$setRecipeRemainder(BATER_WUCKET);
    }
}
