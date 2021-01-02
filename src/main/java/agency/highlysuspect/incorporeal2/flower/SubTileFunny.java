package agency.highlysuspect.incorporeal2.flower;

import agency.highlysuspect.incorporeal2.junk.Despacito;
import agency.highlysuspect.incorporeal2.net.IncorporeticNetCommon;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.Instrument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.TileEntityFunctionalFlower;

public class SubTileFunny extends TileEntityFunctionalFlower {
	public static SubTileFunny kingSize() {
		return new SubTileFunny(IncorporeticFlowers.FUNNY.kingSizeType, 4, 4, 0, 0.75d);
	}
	
	public static SubTileFunny funSize() {
		return new SubTileFunny(IncorporeticFlowers.FUNNY.funSizeType, 2, 3, 7, 0.6d);
	}
	
	public SubTileFunny(BlockEntityType<?> type, int range, int ticksBetweenNotes, int pitchShift, double sparkleHeight) {
		super(type);
		this.range = range;
		this.ticksBetweenNotes = ticksBetweenNotes;
		this.pitchShift = pitchShift;
		this.sparkleHeight = sparkleHeight;
	}
	
	public final int range;
	public final int ticksBetweenNotes;
	public final int pitchShift;
	public final double sparkleHeight;
	public final int NOTE_MANA_COST = 10;
	
	public int clock = -1;
	
	@Override
	public void tickFlower() {
		super.tickFlower();
		
		World world = getWorld();
		int mana = getMana();
		
		if(world == null || world.isClient) return;
		
		if(redstoneSignal == 15) {
			//Reset
			clock = -1;
		} else if(redstoneSignal > 0 || mana < NOTE_MANA_COST) {
			//Pause
		} else {
			clock++;
			
			int ticksBetween = ticksBetweenNotes / (overgrowth || overgrowthBoost ? 2 : 1);
			if(ticksBetween == 0) ticksBetween = 1;
			
			int tick = clock;
			if(tick < 0 || tick % ticksBetween != 0) return;
			tick /= ticksBetween;
			
			//Locate noteblocks to play the music from
			BlockPos flutePos = null;
			BlockPos snarePos = null;
			BlockPos basedrumPos = null;
			BlockPos bassPos = null;
			BlockPos flowerPos = getEffectivePos();
			
			for(BlockPos pos : BlockPos.iterate(flowerPos.add(-range, 0, -range), flowerPos.add(range, 1, range))) {
				if(world.getBlockState(pos).getBlock() == Blocks.NOTE_BLOCK && world.getBlockState(pos.up()).isAir()) {
					Instrument under = Instrument.fromBlockState(world.getBlockState(pos.down()));
					if(under != Instrument.HARP) {
						if(under == Instrument.FLUTE && flutePos == null) {
							flutePos = pos.toImmutable();
						} else if(under == Instrument.SNARE && snarePos == null) {
							snarePos = pos.toImmutable();
						} else if(under == Instrument.BASEDRUM && basedrumPos == null) {
							basedrumPos = pos.toImmutable();
						} else if(under == Instrument.BASS && bassPos == null) {
							bassPos = pos.toImmutable();
						}
					}
				}
			}
			
			//Play the music
			Vec3d particleSrc = world.getBlockState(pos).getModelOffset(world, pos).add(pos.getX() + .5, pos.getY() + sparkleHeight, pos.getZ() + .5);
			
			boolean dirtyMana = doIt(world, pos, tick, particleSrc, flutePos, Instrument.FLUTE);
			if(getMana() > NOTE_MANA_COST) dirtyMana |= doIt(world, pos, tick, particleSrc, snarePos, Instrument.SNARE);
			if(getMana() > NOTE_MANA_COST) dirtyMana |= doIt(world, pos, tick, particleSrc, basedrumPos, Instrument.BASEDRUM);
			if(getMana() > NOTE_MANA_COST) dirtyMana |= doIt(world, pos, tick, particleSrc, bassPos, Instrument.BASS);
			if(dirtyMana) sync();
		}
	}
	
	private boolean doIt(World world, BlockPos pos, int tick, Vec3d particleSrc, BlockPos noteblockPos, Instrument inst) {
		if(noteblockPos == null) return false;
		
		int[] notes = Despacito.notesForTick(tick, inst);
		if(notes != null) {
			IncorporeticNetCommon.sendFunnyFlower((ServerWorld) world, particleSrc, new Vec3d(noteblockPos.getX() + .5, noteblockPos.getY() + .5, noteblockPos.getZ() + .5), 2, notes);
			for(int note : notes) {
				if(getMana() > NOTE_MANA_COST) {
					addMana(-NOTE_MANA_COST);
					float convertedPitch = (float) Math.pow(2, (note - 12) / 12d);
					world.playSound(null, pos, inst.getSound(), SoundCategory.RECORDS, 3f, convertedPitch);
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean acceptsRedstone() {
		return true;
	}
	
	@Override
	public int getMaxMana() {
		return 2000;
	}
	
	@Override
	public int getColor() {
		return 0xBB4422;
	}
	
	@Override
	public RadiusDescriptor getRadius() {
		return new RadiusDescriptor.Square(getEffectivePos(), range);
	}
	
	@Override
	public void writeToPacketNBT(CompoundTag cmp) {
		super.writeToPacketNBT(cmp);
		cmp.putInt("Clock", clock);
	}
	
	@Override
	public void readFromPacketNBT(CompoundTag cmp) {
		super.readFromPacketNBT(cmp);
		clock = cmp.getInt("Clock");
	}
}
