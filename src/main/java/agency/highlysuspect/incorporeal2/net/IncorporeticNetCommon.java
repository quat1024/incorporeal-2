package agency.highlysuspect.incorporeal2.net;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class IncorporeticNetCommon {
	public static void onInitialize() {
		//Nothing yet
		
	}
	
	//Unused currently, the sanvocalia flower uses this tho
	public static void sendSparkleLine(ServerWorld world, Vec3d start, Vec3d end, int decay) {
		BlockPos trackingPos = new BlockPos(start);
		
		for(ServerPlayerEntity player : PlayerLookup.tracking(world, trackingPos)) {
			//TODO: Can I factor this out of the loop?
			// My understanding is that Unpooled-backed PacketByteBufs are explicitly reference counted.
			// The Fabric wiki, though, has an example where a buf is created and sent multiple times.
			// When is it dropped?
			PacketByteBuf buf = PacketByteBufs.create();
			
			IncorporeticNet.writeVec3d(buf, start);
			IncorporeticNet.writeVec3d(buf, end);
			buf.writeInt(decay);
			
			ServerPlayNetworking.send(player, IncorporeticNet.SPARKLE_LINE, buf);
		}
	}
	
	public static void sendFunnyFlower(ServerWorld world, Vec3d start, Vec3d end, int decay, int[] notes) {
		BlockPos trackingPos = new BlockPos(start);
		
		for(ServerPlayerEntity player : PlayerLookup.tracking(world, trackingPos)) {
			PacketByteBuf buf = PacketByteBufs.create();
			
			IncorporeticNet.writeVec3d(buf, start);
			IncorporeticNet.writeVec3d(buf, end);
			buf.writeInt(decay);
			buf.writeIntArray(notes);
			
			ServerPlayNetworking.send(player, IncorporeticNet.FUNNY_FLOWER, buf);
		}
	}
}
