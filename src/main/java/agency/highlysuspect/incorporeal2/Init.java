package agency.highlysuspect.incorporeal2;

import agency.highlysuspect.incorporeal2.block.IncorporeticBlocks;
import agency.highlysuspect.incorporeal2.block.entity.EnderSoulCoreBlockEntity;
import agency.highlysuspect.incorporeal2.block.entity.IncorporeticBlockEntityTypes;
import agency.highlysuspect.incorporeal2.item.IncorporeticItems;
import agency.highlysuspect.incorporeal2.net.IncorporeticNetCommon;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vazkii.botania.api.BotaniaAPI;

public class Init implements ModInitializer {
	public static final String MODID = "incorporeal_2";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	
	public static Identifier id(String path) {
		return new Identifier(MODID, path);
	}
	
	@Override
	public void onInitialize() {
		IncorporeticBlocks.onInitialize();
		IncorporeticBlockEntityTypes.onInitialize();
		
		IncorporeticItems.onInitialize();
		
		IncorporeticNetCommon.onInitialize();
		
		BotaniaAPI.instance().registerCorporeaNodeDetector(new EnderSoulCoreBlockEntity.NodeDetector());
	}
}
