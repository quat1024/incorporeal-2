package agency.highlysuspect.incorporeal2.net;

import agency.highlysuspect.incorporeal2.Init;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class IncorporeticNet {
	public static final Identifier FUNNY_FLOWER = Init.id("funny_flower");
	public static final Identifier SPARKLE_LINE = Init.id("sparkle_line");
	
	public static void writeVec3d(PacketByteBuf buf, Vec3d v) {
		buf.writeDouble(v.x);
		buf.writeDouble(v.y);
		buf.writeDouble(v.z);
	}
	
	public static Vec3d readVec3d(PacketByteBuf buf) {
		return new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
	}
}
