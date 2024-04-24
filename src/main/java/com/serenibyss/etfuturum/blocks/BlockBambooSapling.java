package com.serenibyss.etfuturum.blocks;

import com.serenibyss.etfuturum.util.IModelRegister;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class BlockBambooSapling extends Block implements IGrowable, IModelRegister {

    protected static final AxisAlignedBB SAPLINGSPACE = new AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 0.75, 0.75);

    public BlockBambooSapling() {
        super(Material.PLANTS);
        setTickRandomly(true);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        Vec3d vec = state.getOffset(source, pos);
        return SAPLINGSPACE.offset(vec);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public EnumOffsetType getOffsetType() {
        return EnumOffsetType.XZ;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.down()) == Blocks.SAND.getDefaultState();
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);

        for(EnumFacing face : EnumFacing.values()) {
            if(face == EnumFacing.UP && worldIn.getBlockState(pos.offset(face)).getBlock() == EFMBlocks.BAMBOO.getBlock()) {
                worldIn.setBlockState(pos, EFMBlocks.BAMBOO.getBlock().getDefaultState(), 3);
            }
        }
    }


    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if(rand.nextInt(3) == 0 && worldIn.isAirBlock(pos.up()) && worldIn.getCombinedLight(pos.up(), 0) >= 9) {
            growBamboo(worldIn, pos);
        }

    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return EFMBlocks.BAMBOO.getItemStack();
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return worldIn.getBlockState(pos.up()).getBlock() == Blocks.AIR;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return true;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        growBamboo(worldIn, pos);
    }

    protected void growBamboo(World world, BlockPos pos) {
        world.setBlockState(pos.up(), EFMBlocks.BAMBOO.getBlock().getDefaultState().withProperty(BlockBamboo.BAMBOO_LEAVES, BlockBamboo.BambooLeaves.SMALL), 3);
    }

    @Override
    public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World worldIn, BlockPos pos) {
        return player.getHeldItemMainhand().getItem() instanceof ItemSword ? 1.0f : super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
}
