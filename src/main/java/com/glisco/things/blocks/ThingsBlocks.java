package com.glisco.things.blocks;

import com.glisco.things.ThingsCommon;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.OreBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class ThingsBlocks {

    public static final Block STONE_GLOWSTONE_FIXTURE = new GlowstoneFixtureBlock();
    public static final Block QUARTZ_GLOWSTONE_FIXTURE = new GlowstoneFixtureBlock();

    public static final Block GLEAMING_ORE = new OreBlock(FabricBlockSettings.copyOf(Blocks.DIAMOND_ORE).breakByTool(FabricToolTags.PICKAXES, 3).luminance(5).requiresTool()) {
        @Override
        protected int getExperienceWhenMined(Random random) {
            return MathHelper.nextInt(random, 3, 7);
        }
    };

    public static final Block DIAMOND_PRESSURE_PLATE = new DiamondPressurePlateBlock();
    public static final Item DIAMOND_PRESSURE_PLATE_ITEM = new BlockItem(ThingsBlocks.DIAMOND_PRESSURE_PLATE, new Item.Settings().group(ThingsCommon.THINGS_ITEMS)) {
        @Override
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            tooltip.add(new LiteralText("Players only").formatted(Formatting.GRAY));
        }
    };

    public static final Block PLACED_ITEM = new PlacedItemBlock();
    public static BlockEntityType<PlacedItemBlockEntity> PLACED_ITEM_BLOCK_ENTITY;

    static {
        PLACED_ITEM_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "things:placed_item", BlockEntityType.Builder.create(PlacedItemBlockEntity::new, PLACED_ITEM).build(null));
    }

    public static void register() {
        Registry.register(Registry.BLOCK, new Identifier(ThingsCommon.MOD_ID, "stone_glowstone_fixture"), ThingsBlocks.STONE_GLOWSTONE_FIXTURE);
        Registry.register(Registry.BLOCK, new Identifier(ThingsCommon.MOD_ID, "quartz_glowstone_fixture"), ThingsBlocks.QUARTZ_GLOWSTONE_FIXTURE);

        Registry.register(Registry.ITEM, new Identifier(ThingsCommon.MOD_ID, "stone_glowstone_fixture"), new BlockItem(ThingsBlocks.STONE_GLOWSTONE_FIXTURE, new Item.Settings().group(ThingsCommon.THINGS_ITEMS)));
        Registry.register(Registry.ITEM, new Identifier(ThingsCommon.MOD_ID, "quartz_glowstone_fixture"), new BlockItem(ThingsBlocks.QUARTZ_GLOWSTONE_FIXTURE, new Item.Settings().group(ThingsCommon.THINGS_ITEMS)));

        Registry.register(Registry.BLOCK, new Identifier(ThingsCommon.MOD_ID, "placed_item"), ThingsBlocks.PLACED_ITEM);

        Registry.register(Registry.BLOCK, new Identifier(ThingsCommon.MOD_ID, "gleaming_ore"), ThingsBlocks.GLEAMING_ORE);
        Registry.register(Registry.ITEM, new Identifier(ThingsCommon.MOD_ID, "gleaming_ore"), new BlockItem(ThingsBlocks.GLEAMING_ORE, new Item.Settings().group(ThingsCommon.THINGS_ITEMS)));

        Registry.register(Registry.BLOCK, new Identifier(ThingsCommon.MOD_ID, "diamond_pressure_plate"), ThingsBlocks.DIAMOND_PRESSURE_PLATE);
        Registry.register(Registry.ITEM, new Identifier(ThingsCommon.MOD_ID, "diamond_pressure_plate"), DIAMOND_PRESSURE_PLATE_ITEM);

    }
}
