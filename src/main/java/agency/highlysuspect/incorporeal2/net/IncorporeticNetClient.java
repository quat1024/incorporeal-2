package agency.highlysuspect.incorporeal2.net;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import vazkii.botania.client.fx.SparkleParticleData;

public class IncorporeticNetClient {
	public static void onInitialize() {
		ClientPlayNetworking.registerGlobalReceiver(IncorporeticNet.SPARKLE_LINE, (client, handler, buf, responseSender) -> {
			Vec3d start = IncorporeticNet.readVec3d(buf);
			Vec3d end = IncorporeticNet.readVec3d(buf);
			int decay = buf.readInt();
			
			client.execute(() -> {
				World world = client.world;
				if(client.world == null) return;
				
				doSparkleLine(world, start, end, decay);
			});
		});
		
		ClientPlayNetworking.registerGlobalReceiver(IncorporeticNet.FUNNY_FLOWER, (client, handler, buf, responseSender) -> {
			Vec3d start = IncorporeticNet.readVec3d(buf);
			Vec3d end = IncorporeticNet.readVec3d(buf);
			int decay = buf.readInt();
			int[] notes = buf.readIntArray();
			
			client.execute(() -> {
				World world = client.world;
				if(client.world == null) return;
				
				doSparkleLine(world, start, end, decay);
				
				if(notes.length == 1) {
					world.addParticle(ParticleTypes.NOTE, end.x, end.y + 0.7, end.z, notes[0] / 24d, 0.0D, 0.0D);
				} else if(notes.length == 2) {
					world.addParticle(ParticleTypes.NOTE, end.x - 0.2, end.y + 0.7, end.z, notes[0] / 24d, 0.0D, 0.0D);
					world.addParticle(ParticleTypes.NOTE, end.x + 0.2, end.y + 0.7, end.z, notes[1] / 24d, 0.0D, 0.0D);
				}
			});
		});
	}
	
	private static void doSparkleLine(World world, Vec3d start, Vec3d end, int decay) {
		//Loosely based on PacketBotaniaEffect's SPARK_NET_INDICATOR
		//Modified to use passed-in vectors instead of entities
		//and to use Ved3d instead of the custom Vector3
		//...is the comment i wrote back in 2018 when this was a forge mod, who knows if that still applies!
		
		Vec3d diff = end.subtract(start);
		Vec3d movement = diff.normalize().multiply(.2); //Scale it up a bit bc there's really a lot of particles
		int iters = (int) (diff.length() / movement.length());
		float huePer = 1F / iters;
		float hueSum = (float) Math.random();
		
		Vec3d currentPos = start;
		
		for(int i = 0; i < iters; i++) {
			float hue = i * huePer + hueSum;
			
			int color = hsbToRgb(hue, 1f, 1f);
			
			float r = Math.min(1F, ((color & 0xFF0000) >> 16) / 255F + 0.4F);
			float g = Math.min(1F, ((color & 0x00FF00) >> 8) / 255F + 0.4F);
			float b = Math.min(1F, (color & 0x0000FF) / 255F + 0.4F);
			
			world.addParticle(SparkleParticleData.noClip(1f, r, g, b, decay), currentPos.x, currentPos.y, currentPos.z, 0, 0, 0);
			
			currentPos = currentPos.add(movement);
		}
	}
	
	//Copy-pasted from Java AWT, because you can't use that on Macs these days, or something
	@SuppressWarnings({"PointlessBitwiseExpression", "SameParameterValue"}) //Wasn't me!
	private static int hsbToRgb(float hue, float saturation, float brightness) {
		int r = 0, g = 0, b = 0;
		if (saturation == 0) {
			r = g = b = (int) (brightness * 255.0f + 0.5f);
		} else {
			float h = (hue - (float)Math.floor(hue)) * 6.0f;
			float f = h - (float)java.lang.Math.floor(h);
			float p = brightness * (1.0f - saturation);
			float q = brightness * (1.0f - saturation * f);
			float t = brightness * (1.0f - (saturation * (1.0f - f)));
			switch ((int) h) {
				case 0:
					r = (int) (brightness * 255.0f + 0.5f);
					g = (int) (t * 255.0f + 0.5f);
					b = (int) (p * 255.0f + 0.5f);
					break;
				case 1:
					r = (int) (q * 255.0f + 0.5f);
					g = (int) (brightness * 255.0f + 0.5f);
					b = (int) (p * 255.0f + 0.5f);
					break;
				case 2:
					r = (int) (p * 255.0f + 0.5f);
					g = (int) (brightness * 255.0f + 0.5f);
					b = (int) (t * 255.0f + 0.5f);
					break;
				case 3:
					r = (int) (p * 255.0f + 0.5f);
					g = (int) (q * 255.0f + 0.5f);
					b = (int) (brightness * 255.0f + 0.5f);
					break;
				case 4:
					r = (int) (t * 255.0f + 0.5f);
					g = (int) (p * 255.0f + 0.5f);
					b = (int) (brightness * 255.0f + 0.5f);
					break;
				case 5:
					r = (int) (brightness * 255.0f + 0.5f);
					g = (int) (p * 255.0f + 0.5f);
					b = (int) (q * 255.0f + 0.5f);
					break;
			}
		}
		return 0xff000000 | (r << 16) | (g << 8) | (b << 0);
	}
}
