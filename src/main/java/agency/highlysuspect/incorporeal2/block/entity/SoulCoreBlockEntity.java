package agency.highlysuspect.incorporeal2.block.entity;

import agency.highlysuspect.incorporeal2.junk.DermageSerce;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.mana.IManaReceiver;

import java.util.Optional;

public abstract class SoulCoreBlockEntity extends BlockEntity implements IManaReceiver, BlockEntityClientSerializable {
	public SoulCoreBlockEntity(BlockEntityType<?> type) {
		super(type);
	}
	
	public abstract int getMaxMana();
	
	///
	
	public static final DamageSource SOUL = new DermageSerce("incorporeal.soul").setUsesMagic();
	
	protected @Nullable GameProfile ownerProfile;
	protected int mana;
	
	public Optional<ServerPlayerEntity> findPlayer() {
		if(world == null || world.isClient()) throw new IllegalStateException("Do not call findOwner on null/client world !");
		
		if(ownerProfile == null) return Optional.empty();
		
		Entity e = ((ServerWorld) world).getEntity(ownerProfile.getId());
		if(e instanceof ServerPlayerEntity) {
			return Optional.of((ServerPlayerEntity) e);
		} else return Optional.empty();
	}
	
	//Returns whether anything happened
	public boolean onUse(PlayerEntity user) {
		assert world != null;
		
		boolean isDifferent = changeOwnerProfile(user.getGameProfile());
		if(isDifferent) {
			if(!world.isClient()) {
				user.damage(SOUL, 5f);
				receiveInitialMana();
				sync();
				return true;
			}
		}
		
		return false;
	}
	
	public @Nullable GameProfile getOwnerProfile() {
		return ownerProfile;
	}
	
	//Returns whether the profile is different
	public boolean changeOwnerProfile(@Nullable GameProfile newProfile) {
		if(ownerProfile == null && newProfile == null) return false;
		if(ownerProfile != null && ownerProfile.equals(newProfile)) return false;
		
		this.ownerProfile = newProfile;
		markDirty();
		return true;
	}
	
	public void setMana(int mana) {
		this.mana = mana;
		markDirty();
	}
	
	private void receiveInitialMana() {
		int n = getMaxMana() / 2;
		if(getMana() < n) {
			setMana(n);
		}
	}
	
	@Override
	public boolean isFull() {
		return mana >= getMaxMana();
	}
	
	@Override
	public void receiveMana(int manaAdd) {
		mana = MathHelper.clamp(mana + manaAdd, 0, getMaxMana());
		markDirty();
	}
	
	public void drainMana(int manaSub) {
		mana = MathHelper.clamp(mana - manaSub, 0, getMaxMana());
		
		if(mana == 0 && getOwnerProfile() != null && world != null) {
			findPlayer().ifPresent(player -> player.damage(SOUL, 5f));
			changeOwnerProfile(null);
			world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 0.5f, 1.2f);
		}
		//TODO sync (pepper in a few more sync calls in this class, maybe)
		markDirty();
	}
	
	@Override
	public boolean canReceiveManaFromBursts() {
		return getMaxMana() != 0;
	}
	
	@Override
	public int getCurrentMana() {
		return mana;
	}
	
	//alias
	public int getMana() {
		return getCurrentMana();
	}
	
	@Override
	public CompoundTag toTag(CompoundTag tag) {
		toClientTag(tag);
		return super.toTag(tag);
	}
	
	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		fromClientTag(tag);
	}
	
	@Override
	public CompoundTag toClientTag(CompoundTag tag) {
		if(ownerProfile != null) {
			tag.put("CoreOwner", NbtHelper.fromGameProfile(new CompoundTag(), ownerProfile));
		}
		
		tag.putInt("Mana", mana);
		return tag;
	}
	
	@Override
	public void fromClientTag(CompoundTag tag) {
		if(tag.contains("CoreOwner")) {
			ownerProfile = NbtHelper.toGameProfile(tag.getCompound("CoreOwner"));
		} else {
			ownerProfile = null;
		}
		
		mana = tag.getInt("Mana");
	}
}
