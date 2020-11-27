package lgerrets.duodungeon.game;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.Material;
import org.bukkit.Sound;

import lgerrets.duodungeon.ConfigManager;
import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.utils.Coords3d;
import lgerrets.duodungeon.utils.Index2d;
import lgerrets.duodungeon.utils.Index2d.Direction;
import lgerrets.duodungeon.utils.MyMath;
import lgerrets.duodungeon.utils.WEUtils;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;

public class DuoMap {
	
	private int[][] map;
	private int[][] square5;
	static public Coords3d dungeon_origin;
	static private Coords3d pastebin;
	static public int tile_size = ConfigManager.DDConfig.getInt("tile_size");
	static public int max_height = ConfigManager.DDConfig.getInt("max_height");;
	static public World world = Bukkit.getWorld(ConfigManager.DDConfig.getString("world"));
	static public com.sk89q.worldedit.world.World WEWorld = new BukkitWorld(world);
	static private int not_placed_height = 10;
	public static ArrayList<Piece> pieces;
	private boolean is_running;
	private Piece moving_piece = null;
	static private int square5_size = ConfigManager.DDConfig.getConfigurationSection("Game").getInt("superstun_squaresize");
	static {
		if (square5_size % 2 == 0)
			square5_size += 1;
	}
	static private int square5_effectduration = ConfigManager.DDConfig.getConfigurationSection("Game").getInt("superstun_duration"); // in ticks
	
	static {
		ConfigurationSection origin_wp = ConfigManager.DDConfig.getConfigurationSection("Waypoints").getConfigurationSection("dungeon_origin");
		dungeon_origin = new Coords3d(origin_wp.getInt("X"),
							  origin_wp.getInt("Y"),
							  origin_wp.getInt("Z"));
		ConfigurationSection pastebin_wp = ConfigManager.DDConfig.getConfigurationSection("Waypoints").getConfigurationSection("pastebin");
		pastebin = new Coords3d(pastebin_wp.getInt("X"),
								pastebin_wp.getInt("Y"),
								pastebin_wp.getInt("Z"));
	}
	
	static public DuoMap game = new DuoMap(false);
	
	static public void InitializeDungeon()
	{
		game = new DuoMap(true);
	}
	
	public DuoMap(boolean is_running)
	{
		this.is_running = is_running;
		if (is_running)
		{
			map = new int[][] {
				{3,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,3},
				{3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
				{3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
				{3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
				{3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
				{3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
				{3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
				{3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
				{3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
				{3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
				{3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
				{3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
				{3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
				{3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3},
				{3,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,3},
				{3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3},
			}; //15*21
			square5 = new int[map.length][map[0].length]; // java initializes all values to 0
			pieces = new ArrayList<Piece>();
			ClearArea();
			SpawnNewPiece();
			
	        Bukkit.getScheduler().scheduleSyncRepeatingTask(DuoDungeonPlugin.getInstance(), new Runnable() {
	            @Override
	            public void run() {
	            	if (DuoMap.game.IsRunning())
	            	{
	            		ArrayList<Piece> to_delete = new ArrayList<Piece>();
	            		for (Piece piece : pieces)
	            		{
		            		if (!piece.is_placed)
		            			continue;
		            		piece.lifetime_cooldown.tick();
		            		if (!piece.is_active)
		            		{
		            			if (piece.players.size() > 0)
		            			{
		            				piece.is_active = true;
		            				piece.lifetime_cooldown.cpt = MyMath.Max(200, (int) piece.lifetime_cooldown.cpt);
		            			}
		            		}
		            		if (!piece.is_active)
		            			continue;
		            		int state = -1;
		            		if (piece.lifetime_cooldown.cpt == 80)
		            			state = 1;
		            		else if (piece.lifetime_cooldown.cpt == 60)
		            			state = 2;
		            		else if (piece.lifetime_cooldown.cpt == 40)
		            			state = 3;
		            		else if (piece.lifetime_cooldown.cpt == 20)
		            			state = 4;
		            		else if (piece.lifetime_cooldown.isReady())
		            			state = 999;
		            		if (state == 999)
		            			to_delete.add(piece);
		        			else if (state < 0) {}
		        			else
		        			{
		        				piece.PlaySound(Sound.ENTITY_ARMOR_STAND_HIT, 5, state);
		        			}
	            		}
	            		for (Piece piece : to_delete)
	            			piece.Delete();
	            		to_delete.clear();
	            	}
	            }
	        }, 0, 1);
		}
	}
	
	public boolean IsRunning()
	{
		return is_running;
	}
	
	public void SpawnNewPiece()
	{
		if (moving_piece != null)
		{
			// put down the piece (ie y -= not_placed_height)
			int n_tiles = moving_piece.map_occupation.length;
			BlockVector3[] piece_from = new BlockVector3[n_tiles];
			BlockVector3[] pastebins = new BlockVector3[n_tiles];
			BlockVector3[] piece_dest = new BlockVector3[n_tiles];
			for (int idx=0; idx<n_tiles; idx+=1)
			{
				piece_from[idx] = Coords3d.Index2dToBlockVector3(moving_piece.map_occupation[idx], dungeon_origin.add(0,not_placed_height,0));
				pastebins[idx] = (new Coords3d(pastebin.x, pastebin.y, pastebin.z + idx*tile_size)).toBlockVector3();
				piece_dest[idx] = Coords3d.Index2dToBlockVector3(moving_piece.map_occupation[idx], dungeon_origin);
			}
			
			MoveTiles(piece_from, pastebins, true, 0);
			MoveTiles(pastebins, piece_dest, true, 0);
			
			// spawn chest, mobs...
			moving_piece.PlacePiece(dungeon_origin);
		}
		
		{
	
			Piece piece = Piece.SpawnPiece(map);
			piece.InitUpdateMap(map);
			pieces.add(piece);
			
			int n_tiles = piece.map_occupation.length;
			BlockVector3[] piece_from = new BlockVector3[n_tiles];
			BlockVector3[] pastebins = new BlockVector3[n_tiles];
			BlockVector3[] piece_dest = new BlockVector3[n_tiles];
			Coords3d template_origin = piece.GetTemplateOrigin();
			for (int idx=0; idx<n_tiles; idx+=1)
			{
				piece_from[idx] = Coords3d.Index2dToBlockVector3(piece.clone_from[idx], template_origin);
				pastebins[idx] = (new Coords3d(pastebin.x, pastebin.y, pastebin.z + idx*tile_size)).toBlockVector3();
				piece_dest[idx] = Coords3d.Index2dToBlockVector3(piece.map_occupation[idx], dungeon_origin.add(0,not_placed_height,0));
			}
			
			MoveTiles(piece_from, pastebins, false, 0);
			MoveTiles(pastebins, piece_dest, true, 0);
			moving_piece = piece;
	
			DuoDungeonPlugin.logg(this.toString());
		}
	}
	
	public void ClearArea()
	{
		BlockVector3 temp_3;
		int volume = 0;
		int dy;
		BlockData mat;
		for (int x=0 ; x<map.length ; x+=1)
		{
			for (int z=0 ; z<map[0].length ; z+=1)
			{
				switch(this.GetMap(x, z, -1))
				{
				case 0:
					dy = max_height+not_placed_height;
					mat = Material.AIR.createBlockData();
					break;
				case 2:
					dy = 2;
					mat = Material.OBSIDIAN.createBlockData();
					break;
				case 3:
					dy = max_height;
					mat = Material.WHITE_STAINED_GLASS.createBlockData();
					break;
				default:
					dy = max_height;
					mat = Material.AIR.createBlockData();
					break;
				}
				
				temp_3 = Coords3d.Index2dToBlockVector3(new Index2d(x, z), dungeon_origin);
				CuboidRegion region = new CuboidRegion(WEWorld, temp_3, temp_3.add(tile_size-1, dy, tile_size-1));
				volume += region.getVolume();
				
				WEUtils.FillRegion(WEWorld, region, mat);
			}
		}
		
		System.out.println("Cleared " + String.valueOf(volume) + " Blocks for the dungeon");
	}
	
	public void TryMovePiece(Direction d)
	{
		boolean canMove = true;
		Index2d[] newcoords = new Index2d[moving_piece.map_occupation.length];
		int idx = 0;
		for (Index2d coord : moving_piece.map_occupation) // loop through each tile of this piece
		{
			Index2d newcoord = coord.CalculateTranslation(1, d); // calculate where this tile would go
			newcoords[idx] = newcoord;
			if (this.GetMap(newcoord.x,newcoord.z, 1) > 0) // the destination tile is occupied...
			{
				canMove = false;
				for (Index2d other : moving_piece.map_occupation)
				{
					if (other.equals(newcoord)) // ... actually it is occupied by a tile of this piece
					{
						canMove = true;
						break;
					}
				}
				if (!canMove)
					break;
			}
			idx += 1;
		}
		if (canMove)
		{
			MovePiece(moving_piece, newcoords, moving_piece.map_occupation00.CalculateTranslation(1,d), true);
		}
	}
	
	public void MovePiece(Piece piece, Index2d[] destination, Index2d map_occupation00, boolean cut)
	{
		int n_tiles = piece.map_occupation.length;
		BlockVector3[] piece_from = new BlockVector3[n_tiles];
		BlockVector3[] pastebins = new BlockVector3[n_tiles];
		BlockVector3[] piece_dest = new BlockVector3[n_tiles];
		for (int idx=0; idx<n_tiles; idx+=1)
		{
			piece_from[idx] = Coords3d.Index2dToBlockVector3(piece.map_occupation[idx], dungeon_origin.add(0,not_placed_height,0));
			pastebins[idx] = (new Coords3d(pastebin.x, pastebin.y, pastebin.z + idx*tile_size)).toBlockVector3();
			piece_dest[idx] = Coords3d.Index2dToBlockVector3(destination[idx], dungeon_origin.add(0,not_placed_height,0));
		}
		
		MoveTiles(piece_from, pastebins, cut, 0);
		MoveTiles(pastebins, piece_dest, true, 0);
		
		for (Index2d idx : piece.map_occupation)
		{
			this.SetMap(idx.x,idx.z,0);
		}
		for (Index2d idx : destination)
		{
			this.SetMap(idx.x,idx.z,1);
		}
		piece.SetMapOccupation(destination, map_occupation00);
	}
	
	public void TryRotatePiece(boolean orientation)
	{
		boolean canMove = false;
		Index2d center = null;
		Index2d[] newcoords = new Index2d[moving_piece.map_occupation.length];
		for (int idx_center=0; idx_center < moving_piece.map_occupation.length; idx_center+=1) // loop through each tile as a center candidate (todo: in the future, we could actually have more candidates as centers)
		{
			center = moving_piece.map_occupation[idx_center];
			canMove = true;
			for (int idx_other=0; idx_other < moving_piece.map_occupation.length; idx_other+=1) // calculate newcoords and check if there is no collision
			{
				newcoords[idx_other] = moving_piece.map_occupation[idx_other].CalculateRotation(center, orientation);
				// (we obviously do not check collision between center and itself)
				if (idx_other != idx_center && GetMap(newcoords[idx_other].x, newcoords[idx_other].z, 1) > 0)
				{
					canMove = false; // found a collision ...
					for (int maybe_same_piece=0; maybe_same_piece < moving_piece.map_occupation.length; maybe_same_piece+=1) // ... maybe it's with another tile of the current piece?
					{
						if (moving_piece.map_occupation[maybe_same_piece].equals(newcoords[idx_other]))
						{
							canMove = true; // yes, so we remove the collision flag
							break;
						}
					}
					if (!canMove) // no, it is indeed a collision with another piece
						break;
				}
			}
			if (canMove) // we cannot rotate on this center, let's try another one...
				break;
		}
		if(canMove)
		{
			Index2d map_occupation00 = moving_piece.map_occupation00.CalculateRotation(center, orientation);
			RotatePiece(moving_piece, newcoords, map_occupation00, orientation);
		}
	}
	
	public void RotatePiece(Piece piece, Index2d[] map_occupation, Index2d map_occupation00, boolean orientation)
	{
		int n_tiles = piece.map_occupation.length;
		BlockVector3[] piece_from = new BlockVector3[n_tiles];
		BlockVector3[] pastebins = new BlockVector3[n_tiles];
		BlockVector3[] piece_dest = new BlockVector3[n_tiles];
		for (int idx=0; idx<n_tiles; idx+=1)
		{
			piece_from[idx] = Coords3d.Index2dToBlockVector3(piece.map_occupation[idx], dungeon_origin.add(0,not_placed_height,0));
			pastebins[idx] = (new Coords3d(pastebin.x, pastebin.y, pastebin.z + idx*tile_size)).toBlockVector3();
			piece_dest[idx] = Coords3d.Index2dToBlockVector3(map_occupation[idx], dungeon_origin.add(0,not_placed_height,0));
		}
		
		int rotation;
		if (orientation)
			rotation = 1;
		else
			rotation = -1;
		MoveTiles(piece_from, pastebins, true, 0);
		MoveTiles(pastebins, piece_dest, true, rotation);
		
		for (Index2d idx : piece.map_occupation)
		{
			this.SetMap(idx.x,idx.z,0);
		}
		for (Index2d idx : map_occupation)
		{
			this.SetMap(idx.x,idx.z,1);
		}
		piece.updateRotation(orientation);
		piece.SetMapOccupation(map_occupation, map_occupation00);
	}
	
	public void MoveTiles(BlockVector3[] from, BlockVector3[] dest, boolean cut, int rotation)
	{
		int n_tiles = from.length;
		for (int idx=0; idx<n_tiles; idx+=1)
		{
			WEUtils.CopyRegion(WEWorld, from[idx], from[idx].add(BlockVector3.at(tile_size-1, max_height, tile_size-1)), dest[idx], cut, rotation);
		}
	}
	
	public void SetMap(int x, int z, int value)
	{
		map[x][z] = value;
	}
	
	public int GetMap(int x, int z, int default_if_oob)
	{
		if (this.IsOutOfBounds(x, z))
			return default_if_oob;
		else
			return map[x][z];
	}
	
	public boolean IsOutOfBounds(int x, int z)
	{
		return x < 0 || x >= map.length || z < 0 || z >= map[0].length;			
	}
	
	public String toString()
	{
		String ret = "";
		for (int x = map.length-1; x >= 0 ; x-=1)
		{
			for (int z = 0 ; z < map[0].length ; z+=1)
			{
				ret += String.valueOf(this.GetMap(x,z, -1));
			}
			ret += "\n";
		}
		return ret;
	}
	
	public void PlaceTileAt(int x_tile, int z_tile)
	{
		// map[x][z] is already 1 because on piece place the piece's y just drops down 
		for(int x_square5_center=MyMath.Max(x_tile-(square5_size-1)/2, 0); x_square5_center<=MyMath.Min(x_tile+(square5_size-1)/2,square5.length); x_square5_center+=1)
		{
			for(int z_square5_center=MyMath.Max(z_tile-(square5_size-1)/2, 0); z_square5_center<=MyMath.Min(z_tile+(square5_size-1)/2,square5.length); z_square5_center+=1)
			{
				square5[x_square5_center][z_square5_center] += 1;
				if (square5[x_square5_center][z_square5_center] == square5_size*square5_size)
				{
					for(int x_tile2=MyMath.Max(x_tile-(square5_size-1)/2, 0); x_tile2<=MyMath.Min(x_tile+(square5_size-1)/2,square5.length); x_tile2+=1)
					{
						for(int z_tile2=MyMath.Max(z_tile-(square5_size-1)/2, 0); z_tile2<=MyMath.Min(z_tile+(square5_size-1)/2,square5.length); z_tile2+=1)
						{
							RemoveTileFromSquare5(x_tile2, z_tile2);
						}
					}
					// stun all mobs
					ApplySuperStun();
				}
			}
		}
	}
	
	public void RemoveTileAt(int x, int z)
	{
		map[x][z] = 0;
		RemoveTileFromSquare5(x,z);
	}
	
	public void RemoveTileFromSquare5(int x, int z)
	{
		for(int xx=MyMath.Max(x-(square5_size-1)/2, 0); xx<=MyMath.Min(x+(square5_size-1)/2,square5.length); xx+=1)
		{
			for(int zz=MyMath.Max(z-(square5_size-1)/2, 0); zz<=MyMath.Min(z+(square5_size-1)/2,square5.length); zz+=1)
			{
				square5[xx][zz] -= 1;
			}
		}
	}
	
	public void ApplySuperStun()
	{
		DuoDungeonPlugin.logg("SUPERSTUN!");
		List<LivingEntity> entities = world.getLivingEntities();
		for (LivingEntity e : entities)
		{
			if (e instanceof Monster)
			{
				e.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, square5_effectduration, 15));
				e.setArrowCooldown(square5_effectduration);
			}
		}
	}
}
