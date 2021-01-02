package agency.highlysuspect.incorporeal2;

import agency.highlysuspect.incorporeal2.flower.IncorporeticFlowers;
import agency.highlysuspect.incorporeal2.net.IncorporeticNetCommon;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Init implements ModInitializer {
	public static final String MODID = "incorporeal_2";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	
	public static Identifier id(String path) {
		return new Identifier(MODID, path);
	}
	
	@Override
	public void onInitialize() {
		IncorporeticFlowers.onInitialize();
		
		IncorporeticNetCommon.onInitialize();
	}
}
