package twilightforest.biomes;

import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenBirchTree;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;
import twilightforest.TwilightForestMod;
import twilightforest.entity.EntityTFKobold;
import twilightforest.entity.passive.EntityTFMobileFirefly;
import twilightforest.world.ChunkGeneratorTFBase;
import twilightforest.world.TFWorld;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TFBiomeBase extends Biome {

	protected final WorldGenBigMushroom bigMushroomGen;
	protected final WorldGenBirchTree birchGen;
	protected final List<SpawnListEntry> undergroundMonsterList;

	public TFBiomeBase(BiomeProperties props) {
		super(props);

		bigMushroomGen = new WorldGenBigMushroom();
		birchGen = new WorldGenBirchTree(false, false);

		// remove normal monster spawns
		spawnableMonsterList.clear();
		// remove squids
		spawnableWaterCreatureList.clear();
		// custom creature list.
		spawnableCreatureList.clear();
		spawnableCreatureList.add(new SpawnListEntry(twilightforest.entity.passive.EntityTFBighorn.class, 12, 4, 4));
		spawnableCreatureList.add(new SpawnListEntry(twilightforest.entity.passive.EntityTFBoar.class, 10, 4, 4));
		spawnableCreatureList.add(new SpawnListEntry(net.minecraft.entity.passive.EntityChicken.class, 10, 4, 4));
		spawnableCreatureList.add(new SpawnListEntry(twilightforest.entity.passive.EntityTFDeer.class, 15, 4, 5));
		spawnableCreatureList.add(new SpawnListEntry(net.minecraft.entity.passive.EntityWolf.class, 5, 4, 4));

		spawnableCreatureList.add(new SpawnListEntry(twilightforest.entity.passive.EntityTFTinyBird.class, 15, 4, 8));
		spawnableCreatureList.add(new SpawnListEntry(twilightforest.entity.passive.EntityTFSquirrel.class, 10, 2, 4));
		spawnableCreatureList.add(new SpawnListEntry(twilightforest.entity.passive.EntityTFBunny.class, 10, 4, 5));
		spawnableCreatureList.add(new SpawnListEntry(twilightforest.entity.passive.EntityTFRaven.class, 10, 1, 2));

		undergroundMonsterList = new ArrayList<SpawnListEntry>();

		undergroundMonsterList.add(new SpawnListEntry(EntitySpider.class, 10, 4, 4));
		undergroundMonsterList.add(new SpawnListEntry(EntityZombie.class, 10, 4, 4));
		undergroundMonsterList.add(new SpawnListEntry(EntitySkeleton.class, 10, 4, 4));
		undergroundMonsterList.add(new SpawnListEntry(EntityCreeper.class, 1, 4, 4));
		undergroundMonsterList.add(new SpawnListEntry(EntitySlime.class, 10, 4, 4));
		undergroundMonsterList.add(new SpawnListEntry(EntityEnderman.class, 1, 1, 4));
		undergroundMonsterList.add(new SpawnListEntry(EntityTFKobold.class, 10, 4, 8));

		this.spawnableCaveCreatureList.clear();
		this.spawnableCaveCreatureList.add(new Biome.SpawnListEntry(EntityBat.class, 10, 8, 8));
		this.spawnableCaveCreatureList.add(new SpawnListEntry(EntityTFMobileFirefly.class, 10, 8, 8));

		getTFBiomeDecorator().setTreesPerChunk(10);
		getTFBiomeDecorator().setGrassPerChunk(2);
	}

	@Override
	public float getSpawningChance() {
		// okay, 20% more animals
		return 0.12F;
	}

	@Override
	public BiomeDecorator createBiomeDecorator() {
		return new TFBiomeDecorator();
	}

	protected TFBiomeDecorator getTFBiomeDecorator() {
		return (TFBiomeDecorator) this.decorator;
	}

	@Override
	public WorldGenAbstractTree getRandomTreeFeature(Random random) {
		if (random.nextInt(5) == 0) {
			return birchGen;
		}
		if (random.nextInt(10) == 0) {
			return new WorldGenBigTree(false);
		} else {
			return TREE_FEATURE;
		}
	}

	@Override
	public WorldGenerator getRandomWorldGenForGrass(Random random) {
		if (random.nextInt(4) == 0) {
			return new WorldGenTallGrass(BlockTallGrass.EnumType.FERN);
		} else {
			return new WorldGenTallGrass(BlockTallGrass.EnumType.GRASS);
		}
	}

	@Override
	public void genTerrainBlocks(World world, Random rand, ChunkPrimer primer, int x, int z, double noiseVal) {
		this.genTwilightBiomeTerrain(world, rand, primer, x, z, noiseVal);
	}

	// Copy of super's generateBiomeTerrain, relevant edits noted.
	protected void genTwilightBiomeTerrain(World world, Random rand, ChunkPrimer primer, int x, int z, double noiseVal) {
		int i = TFWorld.SEALEVEL; // TF - set sea level to 31
		IBlockState iblockstate = this.topBlock;
		IBlockState iblockstate1 = this.fillerBlock;
		IBlockState stoneReplacement = getStoneReplacementState(); // TF - Replace stone
		int j = -1;
		int k = (int) (noiseVal / 3.0D + 3.0D + rand.nextDouble() * 0.25D);
		int l = x & 15;
		int i1 = z & 15;
		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
		boolean generateBedrock = shouldGenerateBedrock(world); // TF - conditional bedrock gen

		for (int j1 = 255; j1 >= 0; --j1) {
			// TF - conditional bedrock gen
			if (generateBedrock && j1 <= rand.nextInt(5)) {
				primer.setBlockState(i1, j1, l, BEDROCK);
			} else {
				IBlockState iblockstate2 = primer.getBlockState(i1, j1, l);

				// TF - use block check for air
				if (iblockstate2.getBlock() == Blocks.AIR) {
					// j = -1; TF - commented out? todo 1.9
				} else if (iblockstate2.getBlock() == Blocks.STONE) {
					// TF - Replace stone
					if (stoneReplacement != null) {
						primer.setBlockState(i1, j1, l, stoneReplacement);
					}

					if (j == -1) {
						if (k <= 0) {
							iblockstate = AIR;
							iblockstate1 = STONE;
						} else if (j1 >= i - 4 && j1 <= i + 1) {
							iblockstate = this.topBlock;
							iblockstate1 = this.fillerBlock;
						}

						// TF - use block check for air
						if (j1 < i && (iblockstate == null || iblockstate.getBlock() == Blocks.AIR)) {
							if (this.getTemperature(blockpos$mutableblockpos.setPos(x, j1, z)) < 0.15F) {
								iblockstate = ICE;
							} else {
								iblockstate = WATER;
							}
						}

						j = k;

						if (j1 >= i - 1) {
							primer.setBlockState(i1, j1, l, iblockstate);
						} else if (j1 < i - 7 - k) {
							iblockstate = AIR;
							iblockstate1 = STONE;
							primer.setBlockState(i1, j1, l, GRAVEL);
						} else {
							primer.setBlockState(i1, j1, l, iblockstate1);
						}
					} else if (j > 0) {
						--j;
						primer.setBlockState(i1, j1, l, iblockstate1);

						if (j == 0 && iblockstate1.getBlock() == Blocks.SAND) {
							j = rand.nextInt(4) + Math.max(0, j1 - 63);
							iblockstate1 = iblockstate1.getValue(BlockSand.VARIANT) == BlockSand.EnumType.RED_SAND ? RED_SANDSTONE : SANDSTONE;
						}
					}
				}
			}
		}
	}

	private static boolean shouldGenerateBedrock(World world) {
		IChunkProvider provider = world.getChunkProvider();
		if (provider instanceof ChunkProviderServer) {
			IChunkGenerator generator = ((ChunkProviderServer) provider).chunkGenerator;
			if (generator instanceof ChunkGeneratorTFBase) {
				return ((ChunkGeneratorTFBase) generator).shouldGenerateBedrock();
			}
		}
		return true;
	}

	/**
	 * Return a block if you want it to replace stone in the terrain generation
	 */
	@Nullable
	public IBlockState getStoneReplacementState() {
		return null;
	}

	/**
	 * Does the player have the achievement needed to be in this biome?
	 */
	public boolean doesPlayerHaveRequiredAchievement(EntityPlayer player) {
		for (ResourceLocation advancementLocation : getRequiredAdvancements()) {
			if (!TwilightForestMod.proxy.doesPlayerHaveAdvancement(player, advancementLocation)) {
				return false;
			}
		}
		return true;
	}

	protected ResourceLocation[] getRequiredAdvancements() {
		return new ResourceLocation[0];
	}

	/**
	 * Do something bad to a player in the wrong biome.
	 */
	public void enforceProgression(EntityPlayer player, World world) {}

	/**
	 * Returns the list of underground creatures.
	 */
	public List<SpawnListEntry> getUndergroundSpawnableList() {
		return this.undergroundMonsterList;
	}
}
