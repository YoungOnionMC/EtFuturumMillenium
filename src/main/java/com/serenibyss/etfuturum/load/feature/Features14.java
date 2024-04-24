package com.serenibyss.etfuturum.load.feature;

import com.serenibyss.etfuturum.load.config.ConfigBlocksItems;

public class Features14 extends FeatureManager {

    public final Feature barrel;
    public final Feature stonecutter;
    public final Feature smoothStone;
    public final Feature bamboo;

    public Features14() {
        // initialize your features here
        barrel = Feature.builder("barrel", this, () -> ConfigBlocksItems.enableBarrels)
                .addTextures("block/barrel_bottom.png", "block/barrel_side.png", "block/barrel_top.png",
                        "block/barrel_top_open.png", "gui/container/shulker_box.png")
                .addSounds("block/barrel/open1.ogg", "block/barrel/open2.ogg", "block/barrel/close.ogg")
                .build();

        stonecutter = Feature.builder("stonecutter", this, () -> ConfigBlocksItems.enableStonecutter)
                .addTextures("block/stonecutter_bottom.png", "block/stonecutter_top.png", "block/stonecutter_side.png",
                        "block/stonecutter_saw.png", "block/stonecutter_saw.png.mcmeta", "gui/container/stonecutter.png")
                .addSounds("ui/stonecutter/cut1.ogg", "ui/stonecutter/cut2.ogg")
                .build();

        smoothStone = Feature.builder("smooth_stone", this, () -> ConfigBlocksItems.enableSmoothStone)
                .addTextures("block/smooth_stone.png")
                .addLangOverrides("tile.stoneSlab.name", "tile.stoneSlab.stone.name")
                .build();

        bamboo = Feature.builder("bamboo", this, () -> ConfigBlocksItems.enableBamboo)
                .addTextures("block/bamboo_stalk.png", "item/bamboo.png", "block/bamboo_stage0.png", "block/bamboo_large_leaves.png", "block/bamboo_small_leaves.png",
                            "block/scaffolding_top.png", "block/scaffolding_side.png", "block/scaffolding_bottom.png")
                .build();
    }

    @Override
    public MCVersion getMinecraftVersion() {
        return MCVersion.MC1_14;
    }
}
