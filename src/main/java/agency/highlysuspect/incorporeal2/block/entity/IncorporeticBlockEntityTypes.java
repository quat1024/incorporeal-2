package agency.highlysuspect.incorporeal2.block.entity;

import agency.highlysuspect.incorporeal2.Init;
import agency.highlysuspect.incorporeal2.block.IncorporeticBlocks;
import agency.highlysuspect.incorporeal2.flower.IncorporeticFlowers;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class IncorporeticBlockEntityTypes {
	public static final BlockEntityType<EnderSoulCoreBlockEntity> ENDER_SOUL_CORE_TYPE = BlockEntityType.Builder.create(EnderSoulCoreBlockEntity::new, IncorporeticBlocks.ENDER_SOUL_CORE).build(null);
	
	public static void onInitialize() {
		IncorporeticFlowers.forEach(IncorporeticFlowers.FlowerCollection::registerBlockEntityTypes);
		
		Registry.register(Registry.BLOCK_ENTITY_TYPE, Init.id("ender_soul_core"), ENDER_SOUL_CORE_TYPE);
	}
}
