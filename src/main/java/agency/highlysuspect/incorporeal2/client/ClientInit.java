package agency.highlysuspect.incorporeal2.client;

import agency.highlysuspect.incorporeal2.net.IncorporeticNetClient;
import net.fabricmc.api.ClientModInitializer;

public class ClientInit implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		IncorporeticNetClient.onInitialize();
	}
}
