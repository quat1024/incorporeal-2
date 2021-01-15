package agency.highlysuspect.incorporeal2.block.entity;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProviderBlockEntity;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.compat.FixedInventoryVanillaWrapper;
import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import alexiil.mc.lib.attributes.item.impl.DelegatingFixedItemInv;
import alexiil.mc.lib.attributes.item.impl.EmptyFixedItemInv;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.botania.api.corporea.*;
import vazkii.botania.common.impl.corporea.AbstractCorporeaNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnderSoulCoreBlockEntity extends SoulCoreBlockEntity implements AttributeProviderBlockEntity, FixedItemInv {
	public EnderSoulCoreBlockEntity() {
		super(IncorporeticBlockEntityTypes.ENDER_SOUL_CORE_TYPE);
	}
	
	@Override
	public int getMaxMana() {
		return 5000;
	}
	
	private final FixedItemInv inv = new ManaDrainingWrapperInv(this);
	
	@Override
	public void addAllAttributes(AttributeList<?> list) {
		list.offer(inv);
	}
	
	public static final class NodeDetector implements ICorporeaNodeDetector {
		@Override
		public ICorporeaNode getNode(World world, ICorporeaSpark spark) {
			BlockEntity be = world.getBlockEntity(spark.getAttachPos());
			if(be instanceof EnderSoulCoreBlockEntity) {
				return ((EnderSoulCoreBlockEntity) be).new CorporeaNode(world, spark.getAttachPos(), spark);
			} else return null;
		}
	}
	
	//non-static inner class
	public class CorporeaNode extends AbstractCorporeaNode {
		public CorporeaNode(World world, BlockPos pos, ICorporeaSpark spark) {
			super(world, pos, spark);
		}
		
		@Override
		public List<ItemStack> countItems(ICorporeaRequest request) {
			return doIt(request, Simulation.SIMULATE);
		}
		
		@Override
		public List<ItemStack> extractItems(ICorporeaRequest request) {
			return doIt(request, Simulation.ACTION);
		}
		
		private List<ItemStack> doIt(ICorporeaRequest request, Simulation sim) {
			ArrayList<ItemStack> builder = new ArrayList<>();
			
			//TODO: This is a lazy copy of botania's VanillaCorporeaNode, fudged a bit to handle LBA inventories (see extractstack)
			// There is probably a prettier way to do this
			for (int i = inv.getSlotCount() - 1; i >= 0; i--) {
				ItemStack stackAt = inv.getInvStack(i);
				if (request.getMatcher().test(stackAt)) {
					request.trackFound(stackAt.getCount());
					
					int rem = Math.min(stackAt.getCount(), request.getStillNeeded() == -1 ? stackAt.getCount() : request.getStillNeeded());
					if (rem > 0) {
						request.trackSatisfied(rem);
						
						if (sim.isAction()) {
							ItemStack copy = stackAt.copy();
							builder.addAll(breakDownBigStack(inv.extractStack(i, ConstantItemFilter.ANYTHING, ItemStack.EMPTY, rem, Simulation.ACTION)));
							getSpark().onItemExtracted(copy);
							request.trackExtracted(rem);
						} else {
							ItemStack copy = stackAt.copy();
							copy.setCount(rem);
							builder.add(copy);
						}
					}
				}
			}
			
			return builder;
		}
	}
	
	//non-static inner class
	public class ManaDrainingWrapperInv extends DelegatingFixedItemInv {
		public ManaDrainingWrapperInv(FixedItemInv delegate) {
			super(delegate);
		}
		
		@Override
		public ItemStack insertStack(int slot, ItemStack stack, Simulation simulation) {
			if(simulation.isAction()) drainMana(stack.getCount() * 5);
			return super.insertStack(slot, stack, simulation);
		}
		
		@Override
		public ItemStack extractStack(int slot, ItemFilter filter, ItemStack mergeWith, int maxCount, Simulation simulation) {
			maxCount = Math.min(maxCount, (getMana() / 5) + 1);
			
			ItemStack result = super.extractStack(slot, filter, mergeWith, maxCount, simulation);
			if(simulation.isAction()) drainMana(result.getCount() * 5);
			return result;
		}
	}
	
	private Optional<EnderChestInventory> findEchest() {
		if(getMana() > 0)	return findPlayer().map(PlayerEntity::getEnderChestInventory);
		else return Optional.empty();
	}
	
	//FixedItemInv (this is where the partial delegation to the enderchest happens)
	//I know about DelegatingFixedItemInv, but i might need to switch the underlying inventory when mana runs out/player logs out/whatever
	
	@Override
	public ItemStack getInvStack(int slot) {
		return findEchest().map(e -> e.getStack(slot)).orElse(ItemStack.EMPTY);
	}
	
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return findPlayer().isPresent();
	}
	
	@Override
	public boolean setInvStack(int slot, ItemStack to, Simulation simulation) {
		Optional<EnderChestInventory> echest = findEchest();
		if(echest.isPresent()) {
			if(simulation.isAction()) {
				echest.get().setStack(slot, to);
			}
			return true;
		} else return false;
	}
	
	@Override
	public int getSlotCount() {
		return 27;
	}
}