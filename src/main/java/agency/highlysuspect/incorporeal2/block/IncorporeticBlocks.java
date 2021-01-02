package agency.highlysuspect.incorporeal2.block;

import agency.highlysuspect.incorporeal2.Init;
import agency.highlysuspect.incorporeal2.flower.IncorporeticFlowers;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.util.registry.Registry;
import vazkii.botania.common.block.ModBlocks;

public class IncorporeticBlocks {
	public static final EnderSoulCoreBlock ENDER_SOUL_CORE = new EnderSoulCoreBlock(FabricBlockSettings.copyOf(ModBlocks.corporeaFunnel));
	
	public static void onInitialize() {
		IncorporeticFlowers.forEach(IncorporeticFlowers.FlowerCollection::registerBlocks);
		
		Registry.register(Registry.BLOCK, Init.id("ender_soul_core"), ENDER_SOUL_CORE);
	}
}
