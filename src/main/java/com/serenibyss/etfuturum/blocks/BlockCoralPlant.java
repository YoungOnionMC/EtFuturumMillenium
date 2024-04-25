package com.serenibyss.etfuturum.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;

public class BlockCoralPlant extends BlockRotatedPillar {
    public AxisAlignedBB raytraceCoralAABB;
    public AxisAlignedBB raytraceCoralFanAABB;

    private static final PropertyEnum<EnumType> VARIANT = PropertyEnum.<EnumType>create("variant", EnumType.class);

    public BlockCoralPlant() {
        super(Material.CORAL);
    }

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
}
