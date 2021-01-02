package agency.highlysuspect.incorporeal2.flower;

import agency.highlysuspect.incorporeal2.Init;
import agency.highlysuspect.incorporeal2.item.IncorporeticItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import vazkii.botania.api.subtile.TileEntitySpecialFlower;
import vazkii.botania.common.block.BlockFloatingSpecialFlower;
import vazkii.botania.common.block.BlockSpecialFlower;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class IncorporeticFlowers {
	public static final FlowerCollection<SubTileFunny> FUNNY = new FlowerCollection<>("funny", StatusEffects.UNLUCK, 1200, SubTileFunny::kingSize, SubTileFunny::funSize);
	
	public static void forEach(Consumer<FlowerCollection<?>> action) {
		action.accept(FUNNY);
	}
	
	private static class BlockSpecialFlower2 extends BlockSpecialFlower {
		//It's protected for some reason.
		public BlockSpecialFlower2(StatusEffect stewEffect, int stewDuration, Settings props, Supplier<? extends TileEntitySpecialFlower> teProvider) {
			super(stewEffect, stewDuration, props, teProvider);
		}
	}
	
	public static class FlowerCollection<T extends TileEntitySpecialFlower> {
		//Copy from ModSubtiles which has them private lol
		public static final AbstractBlock.Settings FLOWER_PROPS = AbstractBlock.Settings.copy(Blocks.POPPY);
		public static final AbstractBlock.Settings FLOATING_PROPS = ModBlocks.FLOATING_PROPS;
		
		public FlowerCollection(String name, StatusEffect status, int duration, Supplier<T> kingSize, Supplier<T> funSize) {
			this.name = name;
			
			this.regular = new BlockSpecialFlower2(status, duration, FLOWER_PROPS, kingSize);
			this.chibi = new BlockSpecialFlower2(status, duration, FLOWER_PROPS, funSize);
			this.regularFloating = new BlockFloatingSpecialFlower(FLOATING_PROPS, kingSize);
			this.chibiFloating = new BlockFloatingSpecialFlower(FLOATING_PROPS, funSize);
			
			this.regularId = Init.id(name);
			this.regularFloatingId = Init.id("floating_" + name);
			this.chibiId = Init.id(name + "_chibi");
			this.chibiFloatingId = Init.id("floating_" + name + "_chibi");
			
			this.kingSizeType = BlockEntityType.Builder.create(kingSize, regular, regularFloating).build(null);
			this.funSizeType = BlockEntityType.Builder.create(funSize, chibi, chibiFloating).build(null);
		}
		
		public final String name;
		
		public final BlockSpecialFlower regular;
		public final BlockSpecialFlower chibi;
		public final BlockFloatingSpecialFlower regularFloating;
		public final BlockFloatingSpecialFlower chibiFloating;
		
		public final Identifier regularId;
		public final Identifier regularFloatingId;
		public final Identifier chibiId;
		public final Identifier chibiFloatingId;
		
		public final BlockEntityType<T> kingSizeType;
		public final BlockEntityType<T> funSizeType;
		
		public void registerBlocks() {
			Registry.register(Registry.BLOCK, regularId, regular);
			Registry.register(Registry.BLOCK, regularFloatingId, regularFloating);
			Registry.register(Registry.BLOCK, chibiId, chibi);
			Registry.register(Registry.BLOCK, chibiFloatingId, chibiFloating);
		}
		
		public void registerItems() {
			Registry.register(Registry.ITEM, regularId, new ItemBlockSpecialFlower(regular, IncorporeticItems.ITEM_SETTINGS));
			Registry.register(Registry.ITEM, regularFloatingId, new ItemBlockSpecialFlower(regularFloating, IncorporeticItems.ITEM_SETTINGS));
			Registry.register(Registry.ITEM, chibiId, new ItemBlockSpecialFlower(chibi, IncorporeticItems.ITEM_SETTINGS));
			Registry.register(Registry.ITEM, chibiFloatingId, new ItemBlockSpecialFlower(chibiFloating, IncorporeticItems.ITEM_SETTINGS));
		}
		
		public void registerBlockEntityTypes() {
			Registry.register(Registry.BLOCK_ENTITY_TYPE, regularId, kingSizeType);
			Registry.register(Registry.BLOCK_ENTITY_TYPE, chibiId, funSizeType);
		}
	}
}
