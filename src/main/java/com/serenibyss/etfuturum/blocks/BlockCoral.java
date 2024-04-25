package com.serenibyss.etfuturum.blocks;

import com.serenibyss.etfuturum.EFMTags;
import com.serenibyss.etfuturum.blocks.base.IMultiItemBlock;
import com.serenibyss.etfuturum.util.IModelRegister;
import git.jbredwards.fluidlogged_api.api.capability.IFluidStateCapability;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;

import java.util.Random;

import static com.serenibyss.etfuturum.load.feature.Features.MC13;

public class BlockCoral extends Block implements IModelRegister, IMultiItemBlock {
    public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.<BlockCoral.EnumType>create("variant", EnumType.class);

    public BlockCoral() {
        super(Material.CORAL);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, EnumType.TUBE_CORAL_BLOCK));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{VARIANT});
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for(EnumType type : EnumType.values()) {
            items.add(new ItemStack(this, 1, type.getMetadata()));
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return ((EnumType)state.getValue(VARIANT)).getMetadata();
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if(!this.canLive(worldIn, pos)) {
            worldIn.setBlockState(pos, getDeadState(state), 2);
        }
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        if(!this.canLive(worldIn, pos)) {
            return getDeadState(state);
        }

        return super.getActualState(state, worldIn, pos);
    }

    private IBlockState getDeadState(IBlockState state) {
        return this.getDefaultState().withProperty(VARIANT, EnumType.getDeadVariant(state.getValue(VARIANT).getMetadata()));
    }

    protected boolean canLive(IBlockAccess world, BlockPos pos) {
        for(EnumFacing value : EnumFacing.VALUES) {
            IBlockState state = world.getBlockState(pos.offset(value));
            if(state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.FLOWING_WATER) {
                return true;
            }
        }
        return false;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        if(!this.canLive(world, pos)) {
            world.scheduleUpdate(pos, this.getDefaultState().withProperty(VARIANT, EnumType.getDeadVariant(meta)).getBlock(), 60 + world.rand.nextInt(40));
        }

        return this.getDefaultState().withProperty(VARIANT, EnumType.byMetadata(meta));
    }

    @Override
    public boolean getHasItemSubtypes() {
        return true;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return ((EnumType)state.getValue(VARIANT)).getMetadata();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(VARIANT, EnumType.byMetadata(meta));
    }

    @Override
    public String getTranslationKey(int meta) {
        IBlockState state = this.getStateFromMeta(meta);
        return String.format("tile.%s", state.getValue(VARIANT).getTranslationKey());
    }

    @Override
    public void registerModel() {
        Item itemBlock = Item.getItemFromBlock(this);
        ModelLoader.setCustomModelResourceLocation(itemBlock, 0, new ModelResourceLocation(new ResourceLocation(EFMTags.MODID, "tube_coral"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(itemBlock, 1, new ModelResourceLocation(new ResourceLocation(EFMTags.MODID, "dead_tube_coral"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(itemBlock, 2, new ModelResourceLocation(new ResourceLocation(EFMTags.MODID, "bubble_coral"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(itemBlock, 3, new ModelResourceLocation(new ResourceLocation(EFMTags.MODID, "dead_bubble_coral"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(itemBlock, 4, new ModelResourceLocation(new ResourceLocation(EFMTags.MODID, "fire_coral"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(itemBlock, 5, new ModelResourceLocation(new ResourceLocation(EFMTags.MODID, "dead_fire_coral"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(itemBlock, 6, new ModelResourceLocation(new ResourceLocation(EFMTags.MODID, "brain_coral"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(itemBlock, 7, new ModelResourceLocation(new ResourceLocation(EFMTags.MODID, "dead_brain_coral"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(itemBlock, 8, new ModelResourceLocation(new ResourceLocation(EFMTags.MODID, "horn_coral"), "inventory"));
        ModelLoader.setCustomModelResourceLocation(itemBlock, 9, new ModelResourceLocation(new ResourceLocation(EFMTags.MODID, "dead_horn_coral"), "inventory"));
    }

    public static enum EnumType implements IStringSerializable {
        TUBE_CORAL_BLOCK(0, "tube_coral"),
        DEAD_TUBE_CORAL_BLOCK(1, "dead_tube_coral"),
        BUBBLE_CORAL_BLOCK(2, "bubble_coral"),
        DEAD_BUBBLE_CORAL_BLOCK(3, "dead_bubble_coral"),
        FIRE_CORAL_BLOCK(4, "fire_coral"),
        DEAD_FIRE_CORAL_BLOCK(5, "dead_fire_coral"),
        BRAIN_CORAL_BLOCK(6, "brain_coral"),
        DEAD_BRAIN_CORAL_BLOCK(7, "dead_brain_coral"),
        HORN_CORAL_BLOCK(8, "horn_coral"),
        DEAD_HORN_CORAL_BLOCK(9, "dead_horn_coral");

        private static final BlockCoral.EnumType[] META_LOOKUP = new EnumType[values().length];

        private final int meta;
        private final String name;
        private final String translationKey;

        private EnumType(int meta, String name) {
            this(meta, name, name);
        }

        private EnumType(int meta, String name, String translationKey) {
            this.meta = meta;
            this.name = name;
            this.translationKey = translationKey;
        }

        public static BlockCoral.EnumType byMetadata(int meta) {
            if(meta < 0 || meta > META_LOOKUP.length) {
                meta = 0;
            }
            return META_LOOKUP[meta];
        }

        public static BlockCoral.EnumType getDeadVariant(int meta) {
            if(meta < 0 || meta > META_LOOKUP.length) {
                meta = 1;
            }
            return META_LOOKUP[meta % 2 == 1 ? meta : meta + 1];
        }


        @Override
        public String getName() {
            return this.name;
        }

        public int getMetadata() {
            return meta;
        }

        public String getTranslationKey() {
            return translationKey;
        }

        static {
            for(EnumType type : values()) {
                META_LOOKUP[type.getMetadata()] = type;
            }
        }
    }
}
