package com.serenibyss.etfuturum.blocks;

import com.serenibyss.etfuturum.EFMTags;
import com.serenibyss.etfuturum.blocks.base.EFMBlockDirectional;
import com.serenibyss.etfuturum.util.IModelRegister;
import git.jbredwards.fluidlogged_api.api.util.FluidState;
import git.jbredwards.fluidlogged_api.mod.FluidloggedAPI;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class BlockCoralPlant extends EFMBlockDirectional implements IModelRegister {
    public AxisAlignedBB raytraceCoralAABB;
    public AxisAlignedBB raytraceCoralFanAABB;

    private String name;
    private static final PropertyBool isDead = PropertyBool.create("dead");
    private boolean isFan;

    //private static final PropertyEnum<EnumType> VARIANT = PropertyEnum.<EnumType>create("variant", EnumType.class);

    public BlockCoralPlant(String name, boolean isFan) {
        super(new Settings(Material.CORAL)
                .nonOpaque()
                .nonFullCube());
        this.name = name;
        this.isFan = isFan;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{FACING, isDead});
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public boolean isWaterloggable(IBlockState state, IBlockAccess world, BlockPos pos) {
        return true;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this, 1, 0));
        items.add(new ItemStack(this, 1, 8));
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(isDead) ? 1 : 0;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if(!this.canLive(worldIn, pos)) {
            worldIn.setBlockState(pos, state.withProperty(isDead, true), 2);
        }
    }

    protected boolean canLive(IBlockAccess world, BlockPos pos) {
        if(FluidState.get(pos).getFluid() == FluidRegistry.WATER) {
            return true;
        }

        for(EnumFacing value : EnumFacing.VALUES) {
            IBlockState state = world.getBlockState(pos.offset(value));

            if(state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.FLOWING_WATER || FluidState.get(pos.offset(value)).getFluid() == FluidRegistry.WATER) {
                return true;
            }


        }
        return false;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        if(!this.canLive(worldIn, pos)) {
            return state.withProperty(isDead, true);
        }
        return super.getActualState(state, worldIn, pos);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        if(!isFan) {
            if(facing != EnumFacing.UP) {
                return Blocks.AIR.getDefaultState();
            }
        }
        else {
            if(facing == EnumFacing.DOWN) {
                return Blocks.AIR.getDefaultState();
            }
        }

        if(this.canLive(world, pos)) {
            world.scheduleUpdate(pos, this.getStateFromMeta(meta).withProperty(isDead, true).getBlock(), 60 + world.rand.nextInt(40));
        }

        return this.getStateFromMeta(meta).withProperty(FACING, facing);
    }

    @Override
    public boolean getHasItemSubtypes() {
        return true;
    }

    @Override
    public String getTranslationKey(int meta) {
        boolean ded = this.getStateFromMeta(meta).getValue(isDead);
        return String.format("tile.%s", ((ded ? "dead_" : "") + this.name));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = super.getMetaFromState(state);
        return meta |= (state.getValue(isDead) ? 1 : 0) << 3;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(isDead, ((meta & 8) >> 3 == 1)).withProperty(FACING, EnumFacing.byIndex(meta & 7));
    }

    @Override
    public void registerModel() {
        Item itemBlock = Item.getItemFromBlock(this);
        ModelLoader.setCustomModelResourceLocation(itemBlock, 0, new ModelResourceLocation(new ResourceLocation(EFMTags.MODID, name + "_plant"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(itemBlock, 8, new ModelResourceLocation(new ResourceLocation(EFMTags.MODID, "dead_" + name + "_plant"), "inventory"));
    }

    /*
    public enum EnumType implements IStringSerializable {
        TUBE_CORAL(0, "tube_coral"),
        BRAIN_CORAL(1, "brain_coral"),
        BUBBLE_CORAL(2, "bubble_coral"),
        FIRE_CORAL(3, "fire_coral"),
        HORN_CORAL(4, "horn_coral"),
        DEAD_TUBE_CORAL(5, "dead_tube_coral"),
        DEAD_BRAIN_CORAL(6, "dead_brain_coral"),
        DEAD_BUBBLE_CORAL(7, "dead_bubble_coral"),
        DEAD_FIRE_CORAL(8, "dead_fire_coral"),
        DEAD_HORN_CORAL(9, "dead_horn_coral"),
        TUBE_CORAL_FAN(10, "tube_coral_fan"),
        BRAIN_CORAL_FAN(11, "brain_coral_fan"),
        BUBBLE_CORAL_FAN(12, "bubble_coral_fan"),
        FIRE_CORAL_FAN(13, "fire_coral_fan"),
        HORN_CORAL_FAN(14, "horn_coral_fan"),
        DEAD_TUBE_CORAL_FAN(15, "dead_tube_coral_fan"),
        DEAD_BRAIN_CORAL_FAN(16, "dead_brain_coral_fan"),
        DEAD_BUBBLE_CORAL_FAN(17, "dead_bubble_coral_fan"),
        DEAD_FIRE_CORAL_FAN(18, "dead_fire_coral_fan"),
        DEAD_HORN_CORAL_FAN(19, "dead_horn_coral_fan")
        ;

        private static final BlockCoralPlant.EnumType[] META_LOOKUP = new BlockCoralPlant.EnumType[values().length];

        private final int meta;
        private final String name;
        private final String translationName;

        private EnumType(int meta, String name) {
            this(meta, name, name);
        }

        private EnumType(int meta, String name, String translationName) {
            this.meta = meta;
            this.name = name;
            this.translationName = translationName;
        }

        public int getMetadata() {
            return meta;
        }

        @Override
        public String getName() {
            return name;
        }

        public String getTranslationName() {
            return translationName;
        }

        public static BlockCoralPlant.EnumType byMetadata(int meta) {
            if(meta < 0 || meta > META_LOOKUP.length) {
                meta = 0;
            }
            return META_LOOKUP[meta];
        }

        public static BlockCoralPlant.EnumType getDeadVariant(int meta) {
            if(meta < 0 || meta > META_LOOKUP.length) {
                meta = 1;
            }
            return META_LOOKUP[meta % 2 == 1 ? meta : meta + 1];
        }

        static {
            for(BlockCoralPlant.EnumType type : values()) {
                META_LOOKUP[type.getMetadata()] = type;
            }
        }
    }

    */

}
