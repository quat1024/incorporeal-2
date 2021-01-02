package agency.highlysuspect.incorporeal2.block.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import java.util.Optional;
import java.util.stream.IntStream;

public class EnderSoulCoreBlockEntity extends SoulCoreBlockEntity implements SidedInventory, Tickable {
	public EnderSoulCoreBlockEntity() {
		super(IncorporeticBlockEntityTypes.ENDER_SOUL_CORE_TYPE);
	}
	
	private SidedInventory inv = DummySidedInventory.INSTANCE;
	
	@Override
	public void tick() {
		if(world == null || world.isClient()) return;
		
		Optional<ServerPlayerEntity> opPlayer = findPlayerEntity();
		if(opPlayer.isPresent()) {
			inv = new EnderChestSidedInventoryWrapper(opPlayer.get().getEnderChestInventory());
		} else {
			inv = DummySidedInventory.INSTANCE;
		}
	}
	
	@Override
	public int[] getAvailableSlots(Direction side) {return inv.getAvailableSlots(side);}
	
	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir) {return inv.canInsert(slot, stack, dir);}
	
	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {return inv.canExtract(slot, stack, dir);}
	
	@Override
	public int size() {return inv.size();}
	
	@Override
	public boolean isEmpty() {return inv.isEmpty();}
	
	@Override
	public ItemStack getStack(int slot) {return inv.getStack(slot);}
	
	@Override
	public ItemStack removeStack(int slot, int amount) {return inv.removeStack(slot, amount);}
	
	@Override
	public ItemStack removeStack(int slot) {return inv.removeStack(slot);}
	
	@Override
	public void setStack(int slot, ItemStack stack) {inv.setStack(slot, stack);}
	
	@Override
	public int getMaxCountPerStack() {return inv.getMaxCountPerStack();}
	
	@Override
	public void markDirty() {inv.markDirty();}
	
	@Override
	public boolean canPlayerUse(PlayerEntity player) {return inv.canPlayerUse(player);}
	
	@Override
	public void clear() {inv.clear();}
	
	//MASSIVE TODO: This inventory does not cost any mana!!!!! I need to implement the mana drain effect!!!!!
	//         This is hard without forge inventory stuff. I might have to rely on LBA invs or something
	//         Mainly because it's not very easy to tell when an item gets inserted or removed for real (especially insertion)
	
	public static class EnderChestSidedInventoryWrapper implements SidedInventory {
		public EnderChestSidedInventoryWrapper(EnderChestInventory wrapped) {
			this.wrapped = wrapped;
			
			//in the INCREDIBLY unlikely case that someone made a mod to change the size of the ender chest inventory or something lmao
			if(wrapped.size() != size) {
				size = wrapped.size();
				availableSlots = IntStream.range(0, size).toArray();
			}
		}
		
		private final EnderChestInventory wrapped;
		private static int size = 27;
		private static int[] availableSlots = IntStream.range(0, 27).toArray();
		
		@Override
		public int[] getAvailableSlots(Direction side) {
			return availableSlots;
		}
		
		@Override
		public boolean canInsert(int slot, ItemStack stack, Direction dir) {
			return true;
		}
		
		@Override
		public boolean canExtract(int slot, ItemStack stack, Direction dir) {
			return true;
		}
		
		@Override
		public int size() {
			return size;
		}
		
		@Override
		public boolean isEmpty() {
			return wrapped.isEmpty();
		}
		
		@Override
		public ItemStack getStack(int slot) {
			return wrapped.getStack(slot);
		}
		
		@Override
		public ItemStack removeStack(int slot, int amount) {
			return wrapped.removeStack(slot, amount);
		}
		
		@Override
		public ItemStack removeStack(int slot) {
			return wrapped.removeStack(slot);
		}
		
		@Override
		public void setStack(int slot, ItemStack stack) {
			wrapped.setStack(slot, stack);
			markDirty();
		}
		
		@Override
		public void markDirty() {
			wrapped.markDirty();
		}
		
		@Override
		public boolean canPlayerUse(PlayerEntity player) {
			return false;
		}
		
		@Override
		public void clear() {
			wrapped.clear(); //probably a bad idea...!
		}
	}
	
	public static class DummySidedInventory implements SidedInventory {
		private DummySidedInventory() {}
		
		public static final DummySidedInventory INSTANCE = new DummySidedInventory();
		
		@Override
		public int[] getAvailableSlots(Direction side) {
			return new int[0];
		}
		
		@Override
		public boolean canInsert(int slot, ItemStack stack, Direction dir) {
			return false;
		}
		
		@Override
		public boolean canExtract(int slot, ItemStack stack, Direction dir) {
			return false;
		}
		
		@Override
		public int size() {
			return 0;
		}
		
		@Override
		public boolean isEmpty() {
			return true;
		}
		
		@Override
		public ItemStack getStack(int slot) {
			return ItemStack.EMPTY;
		}
		
		@Override
		public ItemStack removeStack(int slot, int amount) {
			return ItemStack.EMPTY;
		}
		
		@Override
		public ItemStack removeStack(int slot) {
			return ItemStack.EMPTY;
		}
		
		@Override
		public void setStack(int slot, ItemStack stack) {
			//Nope
		}
		
		@Override
		public void markDirty() {
			//Nope
		}
		
		@Override
		public boolean canPlayerUse(PlayerEntity player) {
			return false;
		}
		
		@Override
		public void clear() {
			//Nope
		}
	}
}
