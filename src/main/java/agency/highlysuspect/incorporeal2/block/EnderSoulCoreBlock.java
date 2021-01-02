package agency.highlysuspect.incorporeal2.block;

import agency.highlysuspect.incorporeal2.block.entity.EnderSoulCoreBlockEntity;
import agency.highlysuspect.incorporeal2.block.entity.SoulCoreBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EnderSoulCoreBlock extends Block implements BlockEntityProvider {
	public EnderSoulCoreBlock(Settings settings) {
		super(settings);
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return new EnderSoulCoreBlockEntity();
	}
	
	//TODO split this off into an abstract SoulCoreBlock since this stuff is shared by all cores.
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if(!world.isClient()) {
			BlockEntity be = world.getBlockEntity(pos);
			if(be instanceof SoulCoreBlockEntity) {
				((SoulCoreBlockEntity) be).onUse(player);
			}
		}
		
		return ActionResult.SUCCESS;
	}
}
