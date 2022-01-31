package com.glisco.things.items;

import com.glisco.things.Things;
import com.glisco.things.items.generic.*;
import com.glisco.things.items.trinkets.*;
import com.glisco.things.mixin.access.ItemAccessor;
import dev.emi.trinkets.api.TrinketsApi;
import io.wispforest.owo.ops.TextOps;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import io.wispforest.owo.util.TagInjector;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class ThingsItems implements ItemRegistryContainer {

    public static final Item THINGS_ALMANAC = new ThingsAlmanacItem();

    public static final Item RECALL_POTION = new RecallPotionItem();
    public static final Item CONTAINER_KEY = new ContainerKeyItem();
    public static final Item BATER_WUCKET = new BaterWucketItem();
    public static final Item ENDER_POUCH = new EnderPouchItem();
    public static final Item MONOCLE = new MonocleItem();
    public static final Item MOSS_NECKLACE = new MossNecklaceItem();
    public static final Item PLACEBO = new PlaceboItem();
    public static final Item DISPLACEMENT_TOME = new DisplacementTomeItem();
    public static final Item DISPLACEMENT_PAGE = new Item(new Item.Settings().group(Things.THINGS_GROUP).maxCount(8));
    public static final Item MINING_GLOVES = new MiningGlovesItem();
    public static final Item RIOT_GAUNTLET = new RiotGauntletItem();
    public static final Item INFERNAL_SCEPTER = new InfernalScepterItem();
    public static final Item HADES_CRYSTAL = new HadesCrystalItem();
    public static final Item ENCHANTED_WAX_GLAND = new EnchantedWaxGlandItem();
    public static final Item ITEM_MAGNET = new ItemMagnetItem();
    public static final Item RABBIT_FOOT_CHARM = new RabbitFootCharmItem();
    public static final Item LUCK_OF_THE_IRISH = new LuckOfTheIrishItem();
    public static final Item HARDENING_CATALYST = new HardeningCatalystItem();
    public static final Item SOCKS = new SocksItem();

    public static final Item GLEAMING_POWDER = new GleamingItem();
    public static final Item GLEAMING_COMPOUND = new GleamingItem();

    @Override
    public void afterFieldProcessing() {
        if (Things.CONFIG.appleTrinket) {
            TrinketsApi.registerTrinket(Items.APPLE, new AppleTrinket());
            TagInjector.injectItems(new Identifier("trinkets", "head/face"), Items.APPLE);
        }

        BaterWucketItem.registerCauldronBehavior();
        ((ItemAccessor) BATER_WUCKET).things$setRecipeRemainder(BATER_WUCKET);
        ((ItemAccessor) Items.POTION).things$setRecipeRemainder(Items.GLASS_BOTTLE);
    }

    private static final class GleamingItem extends Item {
        public GleamingItem() {
            super(new Settings().group(Things.THINGS_GROUP).rarity(Rarity.UNCOMMON));
        }

        @Override
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            tooltip.add(TextOps.translateWithColor("text.things.crafting_component", TextOps.color(Formatting.GRAY)));
        }
    }

    private static final class HardeningCatalystItem extends ItemWithExtendableTooltip {
        public HardeningCatalystItem() {
            super(new Item.Settings().group(Things.THINGS_GROUP).maxCount(1).rarity(Rarity.UNCOMMON).fireproof());
        }

        @Override
        public boolean hasGlint(ItemStack stack) {
            return true;
        }
    }
}
