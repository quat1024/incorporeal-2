package agency.highlysuspect.incorporeal2.block.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Optional;

public abstract class SoulCoreBlockEntity extends BlockEntity {
	public SoulCoreBlockEntity(BlockEntityType<?> type) {
		super(type);
	}
	
	public GameProfile ownerProfile;
	
	public void onUse(PlayerEntity user) {
		ownerProfile = user.getGameProfile();
	}
	
	public Optional<ServerPlayerEntity> findPlayerEntity() {
		if(world == null || world.isClient()) throw new IllegalStateException("Do not call findOwner on null/client world !");
		
		if(ownerProfile == null) return Optional.empty();
		
		Entity e = ((ServerWorld) world).getEntity(ownerProfile.getId());
		if(e instanceof ServerPlayerEntity) {
			return Optional.of((ServerPlayerEntity) e);
		} else return Optional.empty();
	}
	
	@Override
	public CompoundTag toTag(CompoundTag tag) {
		if(ownerProfile != null) {
			tag.put("CoreOwner", NbtHelper.fromGameProfile(new CompoundTag(), ownerProfile));
		}
		
		return super.toTag(tag);
	}
	
	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		if(tag.contains("CoreOwner")) {
			ownerProfile = NbtHelper.toGameProfile(tag.getCompound("CoreOwner"));	
		} else {
			ownerProfile = null;
		}
	}
}
