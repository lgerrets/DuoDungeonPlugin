package lgerrets.duodungeon.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.regions.CuboidRegion;

import lgerrets.duodungeon.ConfigManager;
import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.game.Drops.ChestRarity;
import lgerrets.duodungeon.utils.Cooldown;
import lgerrets.duodungeon.utils.Coords3d;
import lgerrets.duodungeon.utils.Index2d;
import lgerrets.duodungeon.utils.MyMath;
import lgerrets.duodungeon.utils.WEUtils;

public class Piece {
	public enum TetrisShape
	{
		O,
		L,
		LR,
		Z,
		S,
		T,
		I,
	}
	static List<TetrisShape> all_shapes = Arrays.asList(TetrisShape.values());
	
	/*public static Map<TetrisShape, int[][]> occupations = new HashMap<>();
	static {
		int[][] tmp_o = {{1,1},
						 {1,1}};
		occupations.put(TetrisShape.O, tmp_o);
		int[][] tmp_lr = {{1,1},
				  		  {1,0},
				  		  {1,0}};
		occupations.put(TetrisShape.LR, tmp_lr);
		int[][] tmp_l = {{1,1},
				  		 {0,1},
				  		 {0,1}};
		occupations.put(TetrisShape.L, tmp_l);
		int[][] tmp_z = {{0,1},
				  		 {1,1},
				  		 {1,0}};
		occupations.put(TetrisShape.Z, tmp_z);
		int[][] tmp_s = {{1,0},
				  		 {1,1},
				  		 {0,1}};
		occupations.put(TetrisShape.S, tmp_s);
		int[][] tmp_t = {{1,0},
				  		 {1,1},
				  		 {1,0}};
		occupations.put(TetrisShape.T, tmp_t);
		int[][] tmp_i = {{1},{1},{1},{1}};
		occupations.put(TetrisShape.I, tmp_i);
	}*/
	
	private static int tile_size = DuoMap.tile_size;
	
	public static EnumMap<TetrisShape, Index2d[]> occupations = new EnumMap<TetrisShape, Index2d[]>(TetrisShape.class);
	static {
		Index2d[] tmp_o = new Index2d[] {new Index2d(0,0), new Index2d(0,1), new Index2d(1,0), new Index2d(1,1)};
		//Index2d[] tmp_o = new Index2d[] {new Index2d(0,0)};
		occupations.put(TetrisShape.O, tmp_o);

		Index2d[] tmp_lr = new Index2d[] {new Index2d(0,0), new Index2d(0,1), new Index2d(1,0), new Index2d(0,2)};
		occupations.put(TetrisShape.LR, tmp_lr);

		Index2d[] tmp_l = new Index2d[] {new Index2d(0,0), new Index2d(1,0), new Index2d(1,1), new Index2d(1,2)};
		occupations.put(TetrisShape.L, tmp_l);

		Index2d[] tmp_z = new Index2d[] {new Index2d(0,1), new Index2d(1,0), new Index2d(1,1), new Index2d(0,2)};
		occupations.put(TetrisShape.Z, tmp_z);

		Index2d[] tmp_s = new Index2d[] {new Index2d(0,0), new Index2d(0,1), new Index2d(1,1), new Index2d(1,2)};
		occupations.put(TetrisShape.S, tmp_s);

		Index2d[] tmp_t = new Index2d[] {new Index2d(0,0), new Index2d(0,1), new Index2d(1,1), new Index2d(0,2)};
		occupations.put(TetrisShape.T, tmp_t);

		Index2d[] tmp_i = new Index2d[] {new Index2d(0,0), new Index2d(0,1), new Index2d(0,2), new Index2d(0,3)};
		occupations.put(TetrisShape.I, tmp_i);
	}
	
	private static EnumMap<TetrisShape, Integer> n_templates = new EnumMap<TetrisShape, Integer>(TetrisShape.class);
	static {
		n_templates.put(TetrisShape.O, ConfigManager.DDConfig.getInt("o_pieces"));
		n_templates.put(TetrisShape.LR, ConfigManager.DDConfig.getInt("lr_pieces"));
		n_templates.put(TetrisShape.L, ConfigManager.DDConfig.getInt("l_pieces"));
		n_templates.put(TetrisShape.Z, ConfigManager.DDConfig.getInt("z_pieces"));
		n_templates.put(TetrisShape.S, ConfigManager.DDConfig.getInt("s_pieces"));
		n_templates.put(TetrisShape.T, ConfigManager.DDConfig.getInt("t_pieces"));
		n_templates.put(TetrisShape.I, ConfigManager.DDConfig.getInt("i_pieces"));
	}
	private static int template_separator = ConfigManager.DDConfig.getInt("piece_separation");
		
	private static EnumMap<TetrisShape, Coords3d> template_origins = new EnumMap<TetrisShape, Coords3d>(TetrisShape.class);
	static {
		template_origins.put(TetrisShape.O, Coords3d.FromWaypoint("tetris_o"));
		template_origins.put(TetrisShape.LR, Coords3d.FromWaypoint("tetris_lr"));
		template_origins.put(TetrisShape.L, Coords3d.FromWaypoint("tetris_l"));
		template_origins.put(TetrisShape.Z, Coords3d.FromWaypoint("tetris_z"));
		template_origins.put(TetrisShape.S, Coords3d.FromWaypoint("tetris_s"));
		template_origins.put(TetrisShape.T, Coords3d.FromWaypoint("tetris_t"));
		template_origins.put(TetrisShape.I, Coords3d.FromWaypoint("tetris_i"));
	}
	
	private static EnumMap<TetrisShape, Coords3d[][]> chest_pos_relative = new EnumMap<TetrisShape, Coords3d[][]>(TetrisShape.class);
	static {
		Coords3d tile_origin;
		Coords3d template_origin;
		for (TetrisShape shape_ : all_shapes) // loop through all pieces
		{
			Coords3d[][] shape_pos = new Coords3d[n_templates.get(shape_)][];
			for (int i_template=0 ; i_template<n_templates.get(shape_) ; i_template+=1) // loop through all templates
			{
				template_origin = TemplateOrigin(shape_, i_template); // origin of template
				ArrayList<Coords3d> piece_pos = new ArrayList<Coords3d>();
				for (Index2d tile_idx : occupations.get(shape_)) // loop through all tiles of template
				{
					tile_origin = Coords3d.Index2dToCoords3d(tile_idx, template_origin); // tile origin
					piece_pos.addAll(SearchBlock(tile_origin, Material.CHEST)); // search chests
				}
				shape_pos[i_template] = piece_pos.toArray(new Coords3d[piece_pos.size()]); // these are absolute coordinates of chests in this piece
				for (int i_chest=0; i_chest<shape_pos[i_template].length; i_chest+=1) // we transform each chest coords into relative coordinates
					shape_pos[i_template][i_chest] = shape_pos[i_template][i_chest].add(-template_origin.x, -template_origin.y, -template_origin.z);
			}
			chest_pos_relative.put(shape_, shape_pos);
		}
	}

	private int rotation;
	public TetrisShape shape;
	public Index2d[] map_occupation;
	public Index2d map_occupation00;
	public Index2d[] clone_from;
	private int rndTemplate;
	private int rndChest;
	private ChestRarity rndRarity;
	private int n_tiles;
	private Coords3d[] my_chest_pos_relative;
	public boolean is_placed;
	public boolean is_active;
	public Cooldown lifetime_cooldown;
	public ArrayList<DuoRunner> players;
	
	public Piece(TetrisShape tetris_shape, Index2d map_occupation00)
	{
		shape = tetris_shape;
		n_tiles = occupations.get(tetris_shape).length;
		map_occupation = new Index2d[n_tiles];
		this.map_occupation00 = map_occupation00;
		for (int i=0; i<n_tiles; i+=1)
			map_occupation[i] = occupations.get(tetris_shape)[i].add(map_occupation00);
		clone_from = occupations.get(shape);
		// TODO: random init rotation
		rndTemplate = MyMath.RandomUInt(n_templates.get(shape));
		rndChest = MyMath.RandomUInt(chest_pos_relative.get(shape)[rndTemplate].length);
		rndRarity = MyMath.RandomChoice(Drops.rarity_drops.entrySet());
		my_chest_pos_relative = chest_pos_relative.get(shape)[rndTemplate].clone();
		is_placed = false;
		players = new ArrayList<DuoRunner>();
		lifetime_cooldown = new Cooldown(ConfigManager.DDConfig.getConfigurationSection("Game").getInt("piece_lifetime"), false);
	}
	
	public void Delete()
	{
		PlaySound(Sound.BLOCK_STONE_BREAK, 5, 1);
		DuoMap.pieces.remove(this);
		for (DuoRunner runner : players)
		{
			runner.piece = null;
		}
		for (int i_tile=0; i_tile < n_tiles; i_tile+=1)
		{
			Coords3d tile_origin = Coords3d.Index2dToCoords3d(map_occupation[i_tile], DuoMap.dungeon_origin);
			CuboidRegion region = new CuboidRegion(DuoMap.WEWorld, tile_origin.toBlockVector3(), tile_origin.add(tile_size,DuoMap.max_height,tile_size).toBlockVector3());
			WEUtils.FillRegion(DuoMap.WEWorld, region, Material.AIR.createBlockData());
		}
	}
	
	public Coords3d GetTemplateOrigin()
	{
		return TemplateOrigin(shape, rndTemplate);
	}
	
	static public Coords3d TemplateOrigin(TetrisShape shape, int i_template)
	{
		return template_origins.get(shape).add(0, 0, i_template*template_separator);
	}
	
	public Piece() // OLD
	{
		
	}
	
	public void SetMapOccupation(Index2d[] map_occupation_, Index2d map_occupation00)
	{
		map_occupation = map_occupation_;
		this.map_occupation00 = map_occupation00;
		DuoDungeonPlugin.logg(DuoMap.game.toString());
		DuoDungeonPlugin.logg(this.my_chest_pos_relative[0].toString());
		DuoDungeonPlugin.logg(this.map_occupation00.toString());
	}
	
	public static Piece SpawnPiece(int[][] map)
	{
		TetrisShape shape = RndTetrisShape();
		//shape = TetrisShape.O;
		boolean found = false;
		Index2d idx = new Index2d(0,0);
		int tries = 0;
		while (!found)
		{
			tries += 1;
			if (tries > 100)
			{
				DuoDungeonPlugin.logg("Unable to place piece, dungeon is too full... Will likely crash!");
				return null;
			}
			idx.x = MyMath.RandomUInt(map.length);
			idx.z = MyMath.RandomUInt(map[0].length);
			found = MapIsFreeForTetrisShape(map, idx, shape);
		}
		DuoDungeonPlugin.logg("Spawning piece " + shape.toString());
		Piece piece = new Piece(shape, idx);
		return piece;
	}
	
	public int[][] InitUpdateMap(int[][] map)
	{
		for (Index2d idx : map_occupation)
			map[idx.x][idx.z] = 1;
		return map;
	}
	
	public void PlacePiece(Coords3d map_origin)
	{
		Coords3d chest_pos_abs;
		// remove all but 1 chest
		DuoDungeonPlugin.logg(this.my_chest_pos_relative[0].toString());
		for (int i_chest=0; i_chest<my_chest_pos_relative.length; i_chest+=1)
		{
			if (i_chest != rndChest)
			{
				chest_pos_abs = Coords3d.Index2dToCoords3d(map_occupation00, map_origin).add(my_chest_pos_relative[i_chest]);
				// TODO: take rotation into account
				DuoMap.world.getBlockAt(chest_pos_abs.x, chest_pos_abs.y, chest_pos_abs.z).setType(Material.AIR);
			}
		}
		
		// fill the chest
		chest_pos_abs = Coords3d.Index2dToCoords3d(map_occupation00, map_origin).add(my_chest_pos_relative[rndChest]);
		// TODO: take rotation into account
		Block block = DuoMap.world.getBlockAt(chest_pos_abs.x, chest_pos_abs.y, chest_pos_abs.z);
		if (block.getType() != Material.CHEST)
			DuoDungeonPlugin.logg("WARNING: block " + chest_pos_abs.toString() + " is not a chest: "+block.getType().toString());
		Chest chest = (Chest) block.getState();
		ItemStack[] content = new ItemStack[1];
		content[0] = Drops.DrawDrop(rndRarity, 0);
		chest.getInventory().setContents(content);
		
		// create a beacon, and place colored stained glass
		Material mat = Material.WHITE_STAINED_GLASS;
		boolean do_spawn_beacon = false;
		switch(rndRarity)
		{
		case COMMON:
			break;
		case RARE:
			do_spawn_beacon = true;
			mat = Material.BLUE_STAINED_GLASS;
			break;
		case EPIC:
			do_spawn_beacon = true;
			mat = Material.PURPLE_STAINED_GLASS;
			break;
		case LEGENDARY:
			do_spawn_beacon = true;
			mat = Material.ORANGE_STAINED_GLASS;
			break;
		default:
			break;				
		}
		if (do_spawn_beacon)
		{
			WEUtils.FillRegionExcept(DuoMap.world, chest_pos_abs.add(0, 1, 0), chest_pos_abs.add(0, DuoMap.max_height, 0), mat, Material.AIR);
			DuoMap.world.getBlockAt(chest_pos_abs.x, chest_pos_abs.y-2, chest_pos_abs.z).setType(Material.BEACON);
			DuoMap.world.getBlockAt(chest_pos_abs.x, chest_pos_abs.y-1, chest_pos_abs.z).setType(mat);
			CuboidRegion region = new CuboidRegion(DuoMap.WEWorld, chest_pos_abs.add(-1, -3, -1).toBlockVector3(), chest_pos_abs.add(1,-3,1).toBlockVector3());
			WEUtils.FillRegion(DuoMap.WEWorld, region, Material.IRON_BLOCK.createBlockData());
		}
		
		// spawn mobs
		int n_to_spawn_mobs = 3;
		ArrayList<Coords3d> spawnables = new ArrayList<Coords3d>();
		for (int i_tile=0; i_tile<n_tiles; i_tile+=1)
		{
			Coords3d tile_origin = Coords3d.Index2dToCoords3d(map_occupation[i_tile], map_origin);
			for(int x=tile_origin.x; x<tile_origin.x+tile_size ; x+=1)
			{
				for(int z=tile_origin.z; z<tile_origin.z+tile_size ; z+=1)
				{
					for (int y=tile_origin.y; y<tile_origin.y+DuoMap.max_height; )
					{
						if (DuoMap.world.getBlockAt(x, y+1, z).getType().equals(Material.AIR) && DuoMap.world.getBlockAt(x, y+2, z).getType().equals(Material.AIR))
						{
							if(!DuoMap.world.getBlockAt(x, y, z).getType().equals(Material.AIR))
							{
								spawnables.add(new Coords3d(x,y+1,z));
							}
							y += 3;
						}
						else
							y += 1;
					}
				}
			}
		}
		Integer[] rnd_coords = MyMath.RandomUInts(n_to_spawn_mobs, spawnables.size(), true);
		for(int n_spawned_mobs=0; n_spawned_mobs<n_to_spawn_mobs; n_spawned_mobs+=1)
		{
			Zombie z = (Zombie) DuoMap.world.spawnEntity(spawnables.get(rnd_coords[n_spawned_mobs]).toLocation(DuoMap.world), EntityType.ZOMBIE);
			z.getEquipment().setHelmet(new ItemStack(Material.STONE_BUTTON));
		}
		
		// update builder's combo
		for (DuoBuilder builder : DuoTeam.builder_players)
			builder.updateCombo(0.2);
		
		// update square5 map (superstun)
		for (int i_tile=0; i_tile<n_tiles; i_tile+=1)
		{
			DuoMap.game.PlaceTileAt(map_occupation[i_tile].x, map_occupation[i_tile].z);
		}
		
		is_placed = true;
	}
	
	private static boolean MapIsFreeForTetrisShape(int[][] map, Index2d origin, TetrisShape shape_)
	{
		for (Index2d offset : occupations.get(shape_))
		{
			Index2d idx = offset.add(origin);
			if ((idx.x >= map.length) || (idx.z >= map[0].length))
				return false;
			if (map[idx.x][idx.z] > 0)
				return false;
		}
		return true;
	}
	
	private static TetrisShape RndTetrisShape()
	{
		int idx = MyMath.RandomUInt(all_shapes.size());
		return all_shapes.get(idx);
	}
	
	private static ArrayList<Coords3d> SearchBlock(Coords3d origin, Material mat)
	{
		ArrayList<Coords3d> founds = new ArrayList<Coords3d>(); 
		for(int x=origin.x; x<origin.x+tile_size ; x+=1)
		{
			for (int y=origin.y; y<origin.y+DuoMap.max_height; y+=1)
			{
				for(int z=origin.z; z<origin.z+tile_size ; z+=1)
				{
					if (DuoMap.world.getBlockAt(x, y, z).getType().equals(mat))
						founds.add(new Coords3d(x,y,z));
				}
			}
		}
		return founds;
	}

	public void updateRotation(boolean orientation) {
		Coords3d rotation_center = new Coords3d(0,0,0);
		Coords3d translation = new Coords3d(0,0,DuoMap.tile_size-1); // dirty hack
		for (int i_chest=0; i_chest<my_chest_pos_relative.length; i_chest+=1)
		{
			my_chest_pos_relative[i_chest] = my_chest_pos_relative[i_chest].CalculateRotation(rotation_center, orientation).add(translation);
		}
	}
	
	public boolean HasCoords3d(Coords3d coords)
	{
		for (int i_tile=0; i_tile < n_tiles; i_tile+=1)
		{
			Coords3d tile_origin = Coords3d.Index2dToCoords3d(map_occupation[i_tile], DuoMap.dungeon_origin);
			if (coords.x >= tile_origin.x && coords.z >= tile_origin.z && 
					coords.x < tile_origin.x + DuoMap.tile_size && coords.z < tile_origin.z + DuoMap.tile_size)
				return true;
		}
		return false;
	}
	
	public void PlaySound(Sound s, int volume, int pitch)
	{
		for (DuoRunner runner : players)
		{
			Player p = runner.getDuoPlayer().getPlayer();
			p.playSound(p.getLocation().add(0, -10, 0), s, volume, pitch);
		}
	}
}
