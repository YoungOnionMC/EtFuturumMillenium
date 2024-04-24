package com.serenibyss.etfuturum.blocks;

import com.google.common.collect.Lists;
import com.serenibyss.etfuturum.items.EFMItems;
import com.serenibyss.etfuturum.load.enums.EFMMaterial;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BlockScaffolding extends Block {

    private static List<AxisAlignedBB> standardAABBs;
    private static List<AxisAlignedBB> supportedAABBs;
    private static AxisAlignedBB bottomAABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, .125D, 1.0D);

    public static final PropertyInteger DISTANCE = PropertyInteger.create("distance", 0, 7);
    public static final PropertyBool ON_AIR = PropertyBool.create("in_air");

    public BlockScaffolding() {
        super(EFMMaterial.MISCELLANEOUS);
        setTranslationKey("scaffolding_block");
        this.setDefaultState(getDefaultState().withProperty(DISTANCE, Integer.valueOf(7)).withProperty(ON_AIR, false));
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Nullable
    @Override
    public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
        List<RayTraceResult> list = Lists.<RayTraceResult>newArrayList();

        for (AxisAlignedBB axisalignedbb : this.getActualState(blockState, worldIn, pos).getValue(ON_AIR) ? supportedAABBs : standardAABBs)
        {
            list.add(this.rayTrace(pos, start, end, axisalignedbb));
        }

        RayTraceResult raytraceresult1 = null;
        double d1 = 0.0D;

        for (RayTraceResult raytraceresult : list)
        {
            if (raytraceresult != null)
            {
                double d0 = raytraceresult.hitVec.squareDistanceTo(end);

                if (d0 > d1)
                {
                    raytraceresult1 = raytraceresult;
                    d1 = d0;
                }
            }
        }

        return raytraceresult1;
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
    {
        return true;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        //super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);

        if(entityIn instanceof EntityPlayer player) {
            //boolean a = isAboveAABB(player, Arrays.asList(FULL_BLOCK_AABB), pos, false);
            if(isAboveAABB(player, Arrays.asList(FULL_BLOCK_AABB), pos, false) && !player.isSneaking()) {
                for(AxisAlignedBB aabb : standardAABBs) {
                    addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb);
                }
            }
            else {
                //a = isAboveAABB(player, supportedAABBs, pos, false);
                if(state.getValue(DISTANCE) != 0 && state.getValue(ON_AIR) && isAboveAABB(player, supportedAABBs, pos, false)) {
                    addCollisionBoxToList(pos, entityBox, collidingBoxes, bottomAABB);
                }
                else {
                    addCollisionBoxToList(pos, entityBox, collidingBoxes, NULL_AABB);
                }
            }
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.getValue(ON_AIR) ? bottomAABB : FULL_BLOCK_AABB;
    }

    public boolean isAboveAABB(EntityPlayer player, List<AxisAlignedBB> aabbs, BlockPos pos, boolean phase) {
        double maxY = Double.NEGATIVE_INFINITY;
        for(AxisAlignedBB aabb : aabbs) {
            maxY = Math.max(maxY, aabb.maxY);
        }

        return player.posY > (double) pos.getY() + maxY - (double)1.0E-5F;
    }


    static
    {
        AxisAlignedBB topFace = new AxisAlignedBB(0.0D, 0.875D, 0.0D, 1.0D, 1.0D, 1.0D); // top face
        AxisAlignedBB frontLeftLeg = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 0.125D); // front left leg
        AxisAlignedBB frontRightLeg = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 0.125D); // front right leg
        AxisAlignedBB backLeftLeg = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 0.125D, 1.0D, 1.0D); // back left leg
        AxisAlignedBB backRightLeg = new AxisAlignedBB(0.875D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D); // back right leg
        standardAABBs = new ArrayList(Arrays.asList(topFace, frontLeftLeg, frontRightLeg, backRightLeg, backLeftLeg));
        AxisAlignedBB bottomLeftEdge = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 0.125D, 1.0D); // bottom left edge
        AxisAlignedBB bottomRightEdge = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D); // bottom right edge?
        AxisAlignedBB bottomBackEdge = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 0.125D, 1.0D); // bottom back edge
        AxisAlignedBB bottomFrontEdge = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 0.125D); // bottom front edge
        supportedAABBs = new ArrayList(Arrays.asList(bottomAABB, bottomLeftEdge, bottomRightEdge, bottomBackEdge, bottomFrontEdge));
        //for(AxisAlignedBB aabb : standardAABBs) {
            //supportedAABBs.add(aabb);
        //}
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return blockState.getValue(ON_AIR) ? bottomAABB : FULL_BLOCK_AABB;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(DISTANCE) | ((state.getValue(ON_AIR) ? 1 : 0) << 3);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(ON_AIR, (meta >> 3 & 1) == 1)
                .withProperty(DISTANCE, meta & 7);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return FULL_BLOCK_AABB;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{DISTANCE, ON_AIR});
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        int i = getDistance(world, pos);
        return this.getDefaultState().withProperty(DISTANCE, Integer.valueOf(i)).withProperty(ON_AIR, Boolean.valueOf(this.inAir(world, pos, i)));
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if(!worldIn.isRemote) {
            worldIn.scheduleUpdate(pos, this, 1);
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if(!worldIn.isRemote) {
            worldIn.scheduleUpdate(pos, this, 1);
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if(!worldIn.isRemote) {
            worldIn.scheduleUpdate(pos, this, 1);
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        int i = getDistance(worldIn, pos);
        IBlockState state1 = state.withProperty(DISTANCE, Integer.valueOf(i)).withProperty(ON_AIR, inAir(worldIn, pos, i));
        if(state1.getValue(DISTANCE) == 7) {
            if(state.getValue(DISTANCE) == 7) {
                worldIn.spawnEntity(new EntityFallingBlock(worldIn, (double)pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, state1));
            } else {
                worldIn.destroyBlock(pos, true);
            }
        } else if(state != state1) {
            worldIn.setBlockState(pos, state1, 3);
        }
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return getDistance(worldIn, pos) < 7;
    }

    private boolean inAir(IBlockAccess world, BlockPos pos, int height) {
        return height > 0 && world.getBlockState(pos.down()).getBlock() != this;
    }

    public static int getDistance(IBlockAccess world, BlockPos pos) {
        BlockPos.MutableBlockPos mutableBlockPos = (new BlockPos.MutableBlockPos(pos).move(EnumFacing.DOWN));
        IBlockState state = world.getBlockState(mutableBlockPos);
        int i = 7;
        if(state.getBlock() == EFMBlocks.SCAFFOLDING.getBlock()) {
            i = state.getValue(DISTANCE);
        } else if (state.isSideSolid(world, mutableBlockPos, EnumFacing.UP)) {
            return 0;
        }

        for(EnumFacing face : EnumFacing.HORIZONTALS) {
            IBlockState state1 = world.getBlockState(mutableBlockPos.setPos(pos).move(face));
            if(state1.getBlock() == EFMBlocks.SCAFFOLDING.getBlock()) {
                i = Math.min(i, state1.getValue(DISTANCE) + 1);
                if(i==1) {
                    break;
                }
            }
        }

        return i;
    }

    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
        return true;
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return EFMItems.SCAFFOLDING.getItemStack();
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return EFMItems.SCAFFOLDING.getItem();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }
}
