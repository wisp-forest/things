package com.glisco.things.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.Random;

@SuppressWarnings("deprecation")
public class GlowstoneFixtureBlock extends FacingBlock {

    private static final VoxelShape BASE_DOWN = Block.createCuboidShape(5, 0, 5, 11, 1, 11);
    private static final VoxelShape GLOWSTONE_DOWN = Block.createCuboidShape(6, 1, 6, 10, 2, 10);
    private static final VoxelShape BASE_UP = Block.createCuboidShape(5, 15, 5, 11, 16, 11);
    private static final VoxelShape GLOWSTONE_UP = Block.createCuboidShape(6, 14, 6, 10, 15, 10);

    private static final VoxelShape BASE_NORTH = Block.createCuboidShape(5, 5, 0, 11, 11, 1);
    private static final VoxelShape GLOWSTONE_NORTH = Block.createCuboidShape(6, 6, 1, 10, 10, 2);
    private static final VoxelShape BASE_SOUTH = Block.createCuboidShape(5, 5, 15, 11, 11, 16);
    private static final VoxelShape GLOWSTONE_SOUTH = Block.createCuboidShape(6, 6, 14, 10, 10, 15);

    private static final VoxelShape BASE_WEST = Block.createCuboidShape(0, 5, 5, 1, 11, 11);
    private static final VoxelShape GLOWSTONE_WEST = Block.createCuboidShape(1, 6, 6, 2, 10, 10);
    private static final VoxelShape BASE_EAST = Block.createCuboidShape(15, 5, 5, 16, 11, 11);
    private static final VoxelShape GLOWSTONE_EAST = Block.createCuboidShape(14, 6, 6, 15, 10, 10);

    private static final VoxelShape SHAPE_DOWN = VoxelShapes.union(BASE_DOWN, GLOWSTONE_DOWN);
    private static final VoxelShape SHAPE_UP = VoxelShapes.union(BASE_UP, GLOWSTONE_UP);
    private static final VoxelShape SHAPE_NORTH = VoxelShapes.union(BASE_NORTH, GLOWSTONE_NORTH);
    private static final VoxelShape SHAPE_SOUTH = VoxelShapes.union(BASE_SOUTH, GLOWSTONE_SOUTH);
    private static final VoxelShape SHAPE_WEST = VoxelShapes.union(BASE_WEST, GLOWSTONE_WEST);
    private static final VoxelShape SHAPE_EAST = VoxelShapes.union(BASE_EAST, GLOWSTONE_EAST);

    public GlowstoneFixtureBlock() {
        super(FabricBlockSettings.of(Material.STONE).nonOpaque().luminance(15).requiresTool().hardness(1));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.FACING);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getSide().getOpposite());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case UP -> SHAPE_UP;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            default -> SHAPE_DOWN;
        };
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {

        if (random.nextDouble() > 0.15) return;

        var facing = state.get(FACING);
        var center = Vec3d.ofCenter(pos);

        double x = center.getX() + facing.getOffsetX() * .35f;
        double y = center.getY() + facing.getOffsetY() * .35f;
        double z = center.getZ() + facing.getOffsetZ() * .35f;

        world.addParticle(new DustParticleEffect(new Vec3f(1, 1, 1), 1),
                x, y, z, 0, 0, 0);
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction direction = state.get(FACING);
        BlockPos blockPos = pos.offset(direction);
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.isSideSolidFullSquare(world, blockPos, direction.getOpposite());
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        return direction == state.get(FACING) && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : state;
    }

}
