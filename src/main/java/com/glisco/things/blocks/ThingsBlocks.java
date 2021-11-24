package com.glisco.things.blocks;

import io.wispforest.owo.registration.reflect.BlockRegistryContainer;
import com.glisco.things.ThingsCommon;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
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
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ThingsBlocks implements BlockRegistryContainer {

    public static final Block STONE_GLOWSTONE_FIXTURE = new GlowstoneFixtureBlock();
    public static final Block QUARTZ_GLOWSTONE_FIXTURE = new GlowstoneFixtureBlock();

    public static final Block GLEAMING_ORE = new OreBlock(FabricBlockSettings.copyOf(Blocks.DIAMOND_ORE).luminance(5).requiresTool(), UniformIntProvider.create(3, 7));

    public static final Block DIAMOND_PRESSURE_PLATE = new DiamondPressurePlateBlock();
    public static final BlockItem DIAMOND_PRESSURE_PLATE_ITEM = new BlockItem(ThingsBlocks.DIAMOND_PRESSURE_PLATE, new Item.Settings().group(ThingsCommon.THINGS_ITEMS)) {
        @Override
        public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            tooltip.add(new LiteralText("Players only").formatted(Formatting.GRAY));
        }
    };

    public static final Block PLACED_ITEM = new PlacedItemBlock();
    public static final BlockEntityType<PlacedItemBlockEntity> PLACED_ITEM_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(PlacedItemBlockEntity::new, PLACED_ITEM).build();

    @Override
    public BlockItem createBlockItem(Block block, String identifier) {
        return block == DIAMOND_PRESSURE_PLATE ? DIAMOND_PRESSURE_PLATE_ITEM : new BlockItem(block, new Item.Settings().group(ThingsCommon.THINGS_ITEMS));
    }

    @Override
    public void postProcessField(String namespace, Block value, String identifier) {
        if (value == PLACED_ITEM) return;
        Registry.register(Registry.ITEM, new Identifier(namespace, identifier), createBlockItem(value, identifier));
    }

    @Override
    public void afterFieldProcessing() {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, ThingsCommon.id("placed_item"), PLACED_ITEM_BLOCK_ENTITY);
    }
}
