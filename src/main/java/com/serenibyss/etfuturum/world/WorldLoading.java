package com.serenibyss.etfuturum.world;

import com.serenibyss.etfuturum.blocks.EFMBlocks;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockSand;
import net.minecraft.init.Blocks;

import static com.serenibyss.etfuturum.blocks.BlockBamboo.PLANTABLE_LIST;

public class WorldLoading {




    public static void initBambooPlacement() {
        PLANTABLE_LIST.add(Blocks.SAND.getDefaultState());
        PLANTABLE_LIST.add(Blocks.SAND.getDefaultState().withProperty(BlockSand.VARIANT, BlockSand.EnumType.RED_SAND));
        PLANTABLE_LIST.add(Blocks.GRASS.getDefaultState());
        PLANTABLE_LIST.add(Blocks.DIRT.getDefaultState());
        PLANTABLE_LIST.add(Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.PODZOL));
        PLANTABLE_LIST.add(Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT));
        PLANTABLE_LIST.add(EFMBlocks.BAMBOO.getBlock().getDefaultState());
        PLANTABLE_LIST.add(EFMBlocks.BAMBOO_SAPLING.getBlock().getDefaultState());
    }
}
