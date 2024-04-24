package com.serenibyss.etfuturum.blocks;

import com.serenibyss.etfuturum.sounds.EFMSounds;
import com.serenibyss.etfuturum.util.IModelRegister;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockBamboo extends Block implements IGrowable, IModelRegister {


    protected static final AxisAlignedBB BAMBOO_COLLISION = new AxisAlignedBB(.40625, 0.0, .40625, 0.59375, 1.0, 0.59375);
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 1);
    public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 1);
    public static final PropertyEnum<BambooLeaves> BAMBOO_LEAVES = PropertyEnum.create("leaves", BambooLeaves.class);

    public static List<IBlockState> PLANTABLE_LIST = new ArrayList<>();

    public BlockBamboo() {
        super(Material.PLANTS);
        this.setDefaultState(this.getDefaultState().withProperty(AGE, Integer.valueOf(0)).withProperty(BAMBOO_LEAVES, BambooLeaves.NONE).withProperty(STAGE, Integer.valueOf(0)));
        setTickRandomly(true);
        setHardness(1.0f);
        setResistance(1.0f);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{AGE, BAMBOO_LEAVES, STAGE});
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(STAGE) | state.getValue(BAMBOO_LEAVES).ordinal() << 1 | state.getValue(AGE) << 3;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(AGE, (meta & 8) >> 3)
                .withProperty(BAMBOO_LEAVES, BambooLeaves.values()[((meta >> 1) & 3)])
                .withProperty(STAGE, meta & 1);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }



    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return super.getCollisionBoundingBox(blockState, worldIn, pos);
    }

    @Override
    public EnumOffsetType getOffsetType() {
        return EnumOffsetType.XZ;
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        Vec3d vec = state.getOffset(source, pos);
        return BAMBOO_COLLISION.offset(vec);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        IBlockState state = world.getBlockState(pos.down());
        if(canBlockStay(state, world, pos)) {
            Block block = state.getBlock();
            if(block == EFMBlocks.BAMBOO_SAPLING.getBlock()) {
                return this.getDefaultState().withProperty(AGE, Integer.valueOf(0));
            } else if(block == EFMBlocks.BAMBOO.getBlock()) {
                int i = state.getValue(AGE) > 0 ? 1 : 0;
                return this.getDefaultState().withProperty(AGE, Integer.valueOf(i));
            }
            else {
                return EFMBlocks.BAMBOO_SAPLING.getBlock().getDefaultState();
            }
        } else {
            return Blocks.AIR.getDefaultState();
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if(!this.canBlockStay(state, worldIn, pos)) {
            worldIn.destroyBlock(pos, true);
        } else if(state.getValue(STAGE) == 0) {
            if(rand.nextInt(3) == 0 && worldIn.isAirBlock(pos.up()) && worldIn.getLightFromNeighbors(pos) >= 9) {
                int i = this.getNumBambooBelow(worldIn, pos) + 1;
                if (i < 16) {
                    this.grow(state, worldIn, pos, rand, i);
                }
            }
        }
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {

        return state;
    }

    public boolean canBlockStay(IBlockState state, World worldIn, BlockPos pos) {
        return PLANTABLE_LIST.contains(worldIn.getBlockState(pos.down())) ||
                worldIn.getBlockState(pos.down()).getBlock() == EFMBlocks.BAMBOO.getBlock();
    }



    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        this.checkForDrop(worldIn, pos, state);

        for(EnumFacing face : EnumFacing.values()) {
            if(face == EnumFacing.UP) {
                IBlockState facingState = worldIn.getBlockState(pos.offset(face));
                if(facingState.getBlock() == EFMBlocks.BAMBOO.getBlock() && facingState.getValue(AGE) > state.getValue(AGE)) {
                    worldIn.setBlockState(pos, state.cycleProperty(AGE));
                    //state = state.cycleProperty(AGE);
                }
            }
        }
    }

    protected final boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state) {

        if (this.canBlockStay(state, worldIn, pos)) {
            return true;
        }
        else {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
            return false;
        }
    }



    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        int i = this.getNumBambooAbove(worldIn, pos);
        int j = this.getNumBambooBelow(worldIn, pos);
        return i + j + 1 < 16 && worldIn.getBlockState(pos.up(i)).getValue(STAGE) != 1;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return true;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        int i = this.getNumBambooAbove(worldIn, pos);
        int j = this.getNumBambooBelow(worldIn, pos);
        int k = i + j + 1;
        int l = 1 + rand.nextInt(2);

        for(int x = 0; x < l; ++x) {
            BlockPos pos1 = pos.up(i);
            IBlockState s = worldIn.getBlockState(pos1);
            if(k >= 16 || s.getValue(STAGE) == 1 || !worldIn.isAirBlock(pos1.up())) {
                return;
            }

            this.grow(s, worldIn, pos1, rand, k);
            ++i;
            ++k;
        }
    }

    protected void grow(IBlockState stateIn, World world, BlockPos posIn, Random rand, int offset) {
        IBlockState state = world.getBlockState(posIn.down());
        BlockPos pos = posIn.down(2);
        IBlockState state1 = world.getBlockState(pos);
        BambooLeaves leaves = BambooLeaves.NONE;
        if(offset >= 1) {
            if(state.getBlock() == EFMBlocks.BAMBOO.getBlock() && state.getValue(BAMBOO_LEAVES) != BambooLeaves.NONE) {
                if(state.getBlock() == EFMBlocks.BAMBOO.getBlock() && state.getValue(BAMBOO_LEAVES) != BambooLeaves.NONE) {
                    leaves = BambooLeaves.LARGE;
                    if (state1.getBlock() == EFMBlocks.BAMBOO.getBlock()) {
                        world.setBlockState(posIn.down(), state.withProperty(BAMBOO_LEAVES, BambooLeaves.SMALL), 3);
                        world.setBlockState(pos, state1.withProperty(BAMBOO_LEAVES, BambooLeaves.NONE), 3);
                    }
                }
            } else {
                leaves = BambooLeaves.SMALL;
            }
        }

        int i = stateIn.getValue(AGE) != 1 && state1.getBlock() != EFMBlocks.BAMBOO.getBlock() ? 0 : 1;
        int j = (offset < 11 || !(rand.nextFloat() < 0.25f)) && offset != 15 ? 0 : 1;
        world.setBlockState(posIn.up(), this.getDefaultState().withProperty(AGE, Integer.valueOf(i)).withProperty(BAMBOO_LEAVES, leaves).withProperty(STAGE, Integer.valueOf(j)), 3);
    }

    protected int getNumBambooAbove(World world, BlockPos pos) {
        int i = 0;
        while(i < 16 && world.getBlockState(pos.up(i + 1)).getBlock() == EFMBlocks.BAMBOO.getBlock()) {
            i++;
        }
        return i;
    }

    protected int getNumBambooBelow(World world, BlockPos pos) {
        int i = 0;
        while(i < 16 && world.getBlockState(pos.down(i + 1)).getBlock() == EFMBlocks.BAMBOO.getBlock()) {
            i++;
        }
        return i;
    }

    @Override
    public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World worldIn, BlockPos pos) {
        return player.getHeldItemMainhand().getItem() instanceof ItemSword ? 1.0f : super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public static enum BambooLeaves implements IStringSerializable {
        NONE("none"),
        SMALL("small"),
        LARGE("large");

        private final String name;

        private BambooLeaves(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

    }
}
