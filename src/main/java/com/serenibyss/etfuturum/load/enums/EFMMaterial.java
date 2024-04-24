package com.serenibyss.etfuturum.load.enums;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class EFMMaterial {

    public static final Material MISCELLANEOUS = (new Material(MapColor.AIR) {
        @Override
        public boolean blocksMovement() {
            return false;
        }

        @Override
        public boolean isSolid() {
            return false;
        }

        @Override
        public EnumPushReaction getPushReaction() {
            return EnumPushReaction.DESTROY;
        }

        @Override
        public boolean isOpaque() {
            return false;
        }
    });

    public static void init() {


    }

}
