package com.glisco.things.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class DiamondPressurePlateBlock extends PressurePlateBlock {

    protected DiamondPressurePlateBlock() {
        super(ActivationRule.MOBS, FabricBlockSettings.copyOf(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE).sounds(BlockSoundGroup.METAL).mapColor(MapColor.DIAMOND_BLUE));
    }

    @Override
    protected int getRedstoneOutput(World world, BlockPos pos) {
        Box box = BOX.offset(pos);
        List<PlayerEntity> entities = world.getNonSpectatingEntities(PlayerEntity.class, box);

        if (!entities.isEmpty()) {
            for (Entity entity : entities) {
                if (!entity.canAvoidTraps()) {
                    return 15;
                }
            }
        }

        return 0;
    }
}
