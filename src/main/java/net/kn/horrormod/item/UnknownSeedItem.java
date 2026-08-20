package net.kn.horrormod.item;

import net.kn.horrormod.block.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class UnknownSeedItem extends BlockItem {
    public UnknownSeedItem(Item.Properties properties) {
        super(ModBlocks.ANOMALOUS_CROP.get(), properties);
    }
}