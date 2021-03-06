package agency.highlysuspect.incorporeal2.junk;

import net.minecraft.block.enums.Instrument;
import org.jetbrains.annotations.Nullable;

public final class Despacito {
	private Despacito() {}
	
	//Don't ask.
	private static final byte[] MAGIC = new byte[] {-126, 34, 114, 34, 82, 2, 0, 0, 5, 85, 82, 53, 33, 34, 17, 17, 21, 85, 82, 120, 35, 34, 51, 51, 56, -120, -126, -86, 39, 34, 34, 34, -126, 34, 114, 34, 82, 2, 0, 0, 5, 85, 82, 53, 33, 34, 17, 17, 21, 85, 82, 120, 35, 34, 51, 51, 56, -120, -126, -86, 39, 34, 34, 34, 34, -89, -84, 34, -54, -54, -62, -84, 42, -54, -62, -35, 40, 34, 34, 40, 45, -35, 45, -3, 44, 34, 34, 34, 44, -52, 47, -36, 42, 34, -86, -86, -86, -86, 114, -126, -54, -54, -54, -62, 42, -54, -62, -35, 40, 34, 34, 40, 45, -35, -46, -3, 44, 34, 34, 34, 44, -52, 47, -36, 42, 34, 34, 34, 4, 4, 2, 8, 2, 8, 2, 8, 2, 8, 2, 8, 2, 8, 2, 8, 0, 0, 2, 8, 2, 8, 2, 8, 2, 8, 2, 8, 2, 8, 2, 8, 0, 0, 6, 12, 6, 12, 6, 12, 6, 12, 6, 12, 6, 8, 6, 12, 6, 12, 6, 12, 6, 12, 6, 12, 6, 12, 6, 12, 6, 12, 6, 12, -91, -1, -1, -1, -81, -1, 111, -1, 111, -1, 111, 111, 111, -1, 111, -1, 111, -1, 111, 111, 111, -1, 111, -1, 111, -1, 111, 111, 111, -1, 111, -1, 111, -1, -1, -1, -1, -1, 111, -1, 111, -1, 111, 111, 111, -1, 111, -1, 111, -1, 111, 111, 111, -1, 111, -1, 111, -1, 111, 111, 111, -1, 111, -1, 111, -1, -1, -1, 105, -1, 105, -1, 105, -1, 105, -1, 105, -1, 105, -1, 105, -1, 105, -1, 105, -1, 105, -1, 105, -1, -91, 111, 105, -1, 105, -1, -91, -1, -91, -1, 105, -1, 105, -1, 105, -1, 105, -1, 105, -1, 105, -1, 105, -1, 105, -1, 105, -1, 105, -1, 105, -1, -91, -1, 105, -1, 105, -1, 50, 34, 34, 34, 82, 34, -126, -62, 82, 34, -126, -62, 18, 34, 82, -126, 18, 34, 82, -126, -126, 34, -62, -14, -126, 34, -62, -14, 50, 34, 114, -94};
	
	private static final int[] ONE = new int[1];
	private static final int[] TWO = new int[2];
	
	@Nullable
	public static int[] notesForTick(int tick, Instrument inst) {
		int t = tick % 256;
		
		if(inst == Instrument.FLUTE) {
			int unpacked = biunpack(0, t);
			return unpacked == 2 ? null : one(unpacked);
		}
		
		if(inst == Instrument.SNARE) {
			boolean low = unpackSnare(t * 2);
			boolean high = unpackSnare(t * 2 + 1);
			if(!low && !high) return null;
			else if(low && !high) return one(8);
			else if(!low) return one(22);
			else return two(8, 22);
		}
		
		if(inst == Instrument.BASEDRUM) {
			if(t % 2 == 1) return null;
			
			int packed = MAGIC[192 + t / 2];
			if(packed == -1) return null;
			
			int a = (packed & 0xF0) >> 4;
			if((packed & 0xF) == 0xF) {
				return one(a);
			}
			int b = (packed & 0xF) - 1;
			return two(a, b);
		}
		
		if(inst == Instrument.BASS) {
			if(t == 0 || t == 128) return null;
			
			int unpacked = biunpack(320, t % 64);
			return unpacked == 2 ? null : one(unpacked);
		}
		
		return null;
	}
	
	private static int[] one(int x) {
		ONE[0] = x;
		return ONE;
	}
	
	private static int[] two(int x, int y) {
		TWO[0] = x;
		TWO[1] = y;
		return TWO;
	}
	
	private static byte biunpack(int wholeOffset, int index) {
		boolean even = index % 2 == 0;
		return (byte) ((Despacito.MAGIC[wholeOffset + index / 2] & (even ? 0b11110000 : 0b00001111)) >>> (even ? 4 : 0));
	}
	
	private static boolean unpackSnare(int index_) {
		int index = index_ / 8;
		int shift = 7 - (index_ % 8);
		int mask = 0b00000001 << shift;
		return ((MAGIC[128 + index] & mask) >>> shift) == 1;
	}
}
