package agency.highlysuspect.incorporeal2.item;

import agency.highlysuspect.incorporeal2.Init;
import agency.highlysuspect.incorporeal2.block.IncorporeticBlocks;
import agency.highlysuspect.incorporeal2.flower.IncorporeticFlowers;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

public class IncorporeticItems {
	public static final ItemGroup GROUP = FabricItemGroupBuilder.build(Init.id("group"), () -> new ItemStack(IncorporeticFlowers.FUNNY.regular.asItem()));
	public static final Item.Settings ITEM_SETTINGS = new Item.Settings().group(GROUP);
	
	public static final BlockItem ENDER_SOUL_CORE = new BlockItem(IncorporeticBlocks.ENDER_SOUL_CORE, ITEM_SETTINGS);
	
	public static void onInitialize() {
		IncorporeticFlowers.forEach(IncorporeticFlowers.FlowerCollection::registerItems);
		
		registerBlockItem(ENDER_SOUL_CORE);
	}
	
	//Convenience method... must be called after the block is registered
	private static void registerBlockItem(BlockItem b) {
		Registry.register(Registry.ITEM, Registry.BLOCK.getId(b.getBlock()), b);
	}
}
