package lgerrets.duodungeon.game;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.BoundingBox;
import org.bukkit.Material;
import org.bukkit.Sound;

import lgerrets.duodungeon.ConfigManager;
import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.players.DuoBuilder;
import lgerrets.duodungeon.players.DuoRunner;
import lgerrets.duodungeon.players.DuoTeam;
import lgerrets.duodungeon.players.DuoTeammate;
import lgerrets.duodungeon.utils.Coords3d;
import lgerrets.duodungeon.utils.Index2d;
import lgerrets.duodungeon.utils.Index2d.Direction;
import lgerrets.duodungeon.utils.MyMath;
import lgerrets.duodungeon.utils.WEUtils;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;

public class DuoMap {
	// misc
	public ArrayList<Piece> pieces; // these arrays are static because we want to access them from bukkit runnables
	public ArrayList<Checkpoint> checkpoints; // these arrays are static because we want to access them from bukkit runnables
	private boolean is_running;
	static public DuoMap game = new DuoMap(false);

	// building stuff
	static public Coords3d dungeon_origin;
	static private Coords3d pastebin;
	static public int tile_size = ConfigManager.DDConfig.getInt("tile_size");
	static public int floor_level = ConfigManager.DDConfig.getInt("floor_level");
	static public int max_height = ConfigManager.DDConfig.getInt("max_height");
	static public World world = Bukkit.getWorld(ConfigManager.DDConfig.getString("world"));
	static public com.sk89q.worldedit.world.World WEWorld = new BukkitWorld(world);
	static public int not_placed_height = 10;
	
	// game config
	static public int piece_lifetime_active = ConfigManager.DDConfig.getConfigurationSection("Game").getInt("piece_lifetime_active");
	static public int struct_spawn_dist = ConfigManager.DDConfig.getConfigurationSection("Game").getInt("struct_spawn_dist");
	
	// game running stats
	public int tier;
	
	// builder gameplay
	static int builder_maxdx_from_controledstruct = ConfigManager.DDConfig.getConfigurationSection("Controls").getInt("builder_maxdx_from_controledstruct");
	
	// moving stuff
	private StructureType moving_type; // can be PIECE or BOMB
	private Structure moving_struct; // can be PIECE or BOMB
	private Piece moving_piece = null;
	private boolean next_is_bomb = false;
	private Bomb moving_bomb = null;
	
	// maps
	final public int XMax = 100;
	final public int ZMax = 21;
	private BoundingBox bbox;
	private StructureType[][] map;
	private int[][] square5;
	private boolean[][] already_decreased_tile_from_square5;
	public int max_placed_x;
	private int[][] neighbour_pieces;
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
		
	static public void InitializeDungeon()
	{
		new DuoMap(true);
	}
	
	public enum StructureType {
	    FREE(0),
	    PIECE(1),
	    CHECKPOINT(2),
	    START(2),
	    BORDER(3),
	    EMPTY(4),
	    BOMB(5),
	    HOSTILE(6),
	    PEACEFUL(7),
	    PIECE_UP(8),
	    PEACEFUL_INDESTRUCTIBLE_INVISIBLE(9)
	    ;

	    private final int id;
	    StructureType(int id) { this.id = id; }
	    public int getValue() { return id; }
	}
	
	public DuoMap(boolean is_running)
	{
		game = this;
		this.is_running = is_running;
		if (is_running)
		{
			map = new StructureType[XMax][ZMax];
			/*{
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
			}; //15*21*/
			bbox = BoundingBox.of(Coords3d.Index2dToLocation(new Index2d(0,0), dungeon_origin),
					Coords3d.Index2dToLocation(new Index2d(XMax+1,ZMax+1), dungeon_origin).add(-1,max_height,-1));
			DuoDungeonPlugin.logg(bbox);
			checkpoints = new ArrayList<Checkpoint>();
			for (int x=0; x<XMax; x+=1)
			{
				StructureType type;
				int z;
				for (z=0; z<ZMax; z+=1)
				{
					if (z == 0 || z == ZMax-1 || x == 0 || x == XMax-1)
						type = StructureType.BORDER;
					else if (x == 1)
						type = StructureType.START;
					/*else if (x == XMax-2)
						type = StructureType.CHECKPOINT;
					else if (x == 7 && (z==1 || z==13))
						type = StructureType.PEACEFUL;*/
					else
						type = StructureType.FREE;
					this.SetMap(x, z, type);
				}
			}
			// place checkpoints (must be done before random asteroids because we want to surround (on z) checkpoints with PEACEFUL_INDESTRUCTIBLE_INVISIBLE (if there is no wall)
			for (int x=0; x<XMax; x+=1)
			{
				if (MyMath.Mod(x, ConfigManager.DDConfig.getConfigurationSection("Game").getInt("x_delta_checkpoints")) == 0 && x > 1 && x < XMax - 1)
				{
					int z = MyMath.RandomUInt(ZMax-5) + 1;
					Checkpoint checkpoint = new Checkpoint(new Index2d(x, z));
					checkpoints.add(checkpoint);
					checkpoint.InitialClone(pastebin, 0);
				}
			}
			// place asteroids
			for (int x=0; x<XMax; x+=1)
			{
				if (MyMath.Mod(x, ConfigManager.DDConfig.getConfigurationSection("Game").getInt("x_delta_asteroids")) == 0 && x > 1 && x < XMax - 1)
				{
					int z = -1;
					StructureType type = null;
					while (this.GetMap(x, z, StructureType.BORDER) != StructureType.FREE)
					{
						z = MyMath.RandomUInt(ZMax-3) + 1;
						type = StructureType.PEACEFUL;
					}
					this.SetMap(x, z, type);
				}
			}
			square5 = new int[XMax][ZMax]; // java initializes all values to 0
			already_decreased_tile_from_square5 = new boolean[XMax][ZMax];
			neighbour_pieces = new int[XMax][ZMax];
			pieces = new ArrayList<Piece>();
			ClearArea();
			moving_type = StructureType.PIECE_UP;
			next_is_bomb = false;
			max_placed_x = 1;
			tier = 1;
			SpawnNewStruct();
			DuoBuilder.Reset();
			DuoRunner.Reset();
		}
	}
	
	public boolean IsRunning()
	{
		return is_running;
	}
	
	public void SpawnNewStruct()
	{
		if (this.moving_type != StructureType.PIECE_UP)
			return; // cannot spawn a struct while a bomb is in play
		if (moving_piece != null)
		{
			if(!this.CanPlacePiece())
				return;
			
			int n_tiles = moving_piece.map_occupation.length;
			
			// update max_placed_x
			for (int idx=0; idx<n_tiles; idx+=1)
			{
				max_placed_x = MyMath.Max(max_placed_x, moving_piece.map_occupation[idx].x);
			}
			
			// put down the piece (ie y -= not_placed_height)
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
			
			// spawn chest, mobs..., update square5 map & occupation
			moving_piece.PlacePiece(dungeon_origin);
		}
		
		if(this.next_is_bomb)
		{
			this.moving_piece = null;
			next_is_bomb = false;
			
			moving_bomb = Bomb.SpawnBomb(map);
			moving_struct = moving_bomb;
			
			moving_bomb.InitialClone(pastebin, not_placed_height);
		}
		else
		{
			Piece piece = Piece.SpawnPiece(map);
			pieces.add(piece);
			
			moving_piece = piece;
			moving_struct = piece;
	
			piece.InitialClone(pastebin, not_placed_height);

			DuoDungeonPlugin.logg(this.toString());
		}
		moving_type = moving_struct.structure_type;
	}
	
	public void ClearArea()
	{
		DuoDungeonPlugin.shout("Clearing the dungeon. Expect some lag...");
		Collection<Entity> entities = world.getNearbyEntities(bbox);
		for (Entity ent : entities)
		{
			if (!(ent instanceof Player))
				ent.remove();
		}
		CuboidRegion region;
		BlockVector3 origin = Coords3d.Index2dToBlockVector3(new Index2d(0,0), dungeon_origin);
		origin = origin.withY(Coords3d.FromWaypoint("builder").y-3);
		region = new CuboidRegion(WEWorld,  origin, origin.add(XMax*tile_size, 0, ZMax*tile_size));	
		WEUtils.FillRegion(WEWorld, region, Material.BARRIER.createBlockData());
		
		BlockVector3 pos;
		int volume = 0;
		int dy = 0;
		BlockData mat = null;
		boolean do_clear;
		boolean do_fill;
		for (int x=0 ; x<XMax ; x+=1)
		{
			for (int z=0 ; z<ZMax ; z+=1)
			{
				pos = Coords3d.Index2dToBlockVector3(new Index2d(x, z), dungeon_origin);
				do_clear = false;
				do_fill = false;
				switch(this.GetMap(x, z, DuoMap.StructureType.EMPTY))
				{
				case START:
					UpdateNeighbourPieces(x, z, 1);
					dy = floor_level;
					mat = Material.OBSIDIAN.createBlockData();
					do_fill = true;
					break;
				case BORDER:
					dy = max_height;
					mat = Material.WHITE_STAINED_GLASS.createBlockData();
					do_fill = true;
					break;
				case PEACEFUL:
					BlockVector3 clone_from = Coords3d.FromWaypoint("obstacle").toBlockVector3();
					WEUtils.CopyRegion(WEWorld, clone_from, clone_from.add(tile_size-1,max_height,tile_size-1), pos, false, 0);
					break;
				case CHECKPOINT:
					break;
				default:
					do_clear = true;
					break;
				}
				
				if (do_clear || do_fill)
				{
					region = new CuboidRegion(WEWorld, pos, pos.add(tile_size-1, max_height+not_placed_height, tile_size-1));
					WEUtils.FillRegion(WEWorld, region, Material.AIR.createBlockData());
					volume += region.getVolume();
					
					if (do_fill)
					{
						region = new CuboidRegion(WEWorld, pos, pos.add(tile_size-1, dy, tile_size-1));	
						WEUtils.FillRegion(WEWorld, region, mat);
					}
				}
			}
		}
		
		System.out.println("Cleared " + String.valueOf(volume) + " Blocks for the dungeon");
		DuoDungeonPlugin.shout("Starting the game...");
	}
	
	public void TryMoveStruct(Direction d)
	{
		// don't do anything if the builder is too far from the structure
		boolean cancel_move = false;
		if (DuoTeam.builder_players.size() > 0) {
			cancel_move = (MyMath.Abs(moving_struct.placed_pos.x - DuoTeam.builder_players.get(0).getDuoPlayer().getPlayer().getLocation().getX()) > 
					builder_maxdx_from_controledstruct); // is too far
			cancel_move = cancel_move || (MyMath.Abs(moving_struct.placed_pos.CalculateTranslation(tile_size, d).x - DuoTeam.builder_players.get(0).getDuoPlayer().getPlayer().getLocation().getX()) > 
					builder_maxdx_from_controledstruct); // would go too far
		}
		if (cancel_move) {
			for (DuoBuilder builder : DuoTeam.builder_players) {
				Player p = builder.getDuoPlayer().getPlayer();
				p.playSound(moving_struct.placed_pos.toLocation(world), Sound.ENTITY_BOAT_PADDLE_WATER, 20, 1);
			}
			return;
		}
		
		// find the types of the tiles that are in the way (ie "blocking" tiles)
		StructureType blocking = StructureType.FREE; // will be in order of priority: PEACEFUL, EMPTY, FREE
		Index2d[] newcoords = new Index2d[moving_struct.map_occupation.length];
		int idx = 0;
		for (Index2d coord : moving_struct.map_occupation) // loop through each tile of this piece
		{
			Index2d newcoord = coord.CalculateTranslation(1, d); // calculate where this tile would go
			newcoords[idx] = newcoord;
			StructureType found = this.GetMap(newcoord.x,newcoord.z, DuoMap.StructureType.EMPTY);
			if (found == StructureType.PEACEFUL)
			{
				blocking = StructureType.PEACEFUL;
				break;
			}
			else if (found != StructureType.FREE && found != StructureType.PIECE_UP) // the destination tile is occupied...
			{
				blocking = StructureType.EMPTY;
				break;
			}
			idx += 1;
		}
		
		// execute the consequences of the found "blocking" tiles
		switch (this.moving_type)
		{
		case PIECE_UP:
			switch (blocking)
			{
			case FREE:
				MoveStruct(moving_struct, newcoords, moving_struct.map_occupation00.CalculateTranslation(1,d), true, 0);
				break;
			case PEACEFUL:
				// reset piece to its initial position
				MoveStruct(moving_struct, moving_piece.map_occupation_first, moving_piece.map_occupation00_first.CalculateTranslation(1,d), true, MyMath.Mod(-moving_piece.rotation, 4));
				moving_piece.ResetPos();
				break;
			default: // do nothing
				break;
			}
			break;
		case BOMB:
			switch (blocking)
			{
			case PEACEFUL:
				// explode bomb
				MoveStruct(moving_struct, newcoords, moving_struct.map_occupation00.CalculateTranslation(1,d), true, 0);
				moving_struct.Delete();
				this.moving_type = StructureType.PIECE_UP;
				this.SpawnNewStruct();
				break;
			case FREE:
				MoveStruct(moving_struct, newcoords, moving_struct.map_occupation00.CalculateTranslation(1,d), true, 0);
				break;
			default: // do nothing
				break;
			}
			break;
		default:
			DuoDungeonPlugin.logg("WARNING: reached unimplemented default in switch");
			break;
		}
	}
	
	public void MoveStruct(Structure struct, Index2d[] destination, Index2d map_occupation00, boolean cut, int rotation)
	{
		int n_tiles = struct.map_occupation.length;
		BlockVector3[] struct_from = new BlockVector3[n_tiles];
		BlockVector3[] pastebins = new BlockVector3[n_tiles];
		BlockVector3[] piece_dest = new BlockVector3[n_tiles];
		for (int idx=0; idx<n_tiles; idx+=1)
		{
			struct_from[idx] = Coords3d.Index2dToBlockVector3(struct.map_occupation[idx], dungeon_origin.add(0,not_placed_height,0));
			pastebins[idx] = (new Coords3d(pastebin.x, pastebin.y, pastebin.z + idx*tile_size)).toBlockVector3();
			piece_dest[idx] = Coords3d.Index2dToBlockVector3(destination[idx], dungeon_origin.add(0,not_placed_height,0));
		}
		
		MoveTiles(struct_from, pastebins, cut, 0);
		MoveTiles(pastebins, piece_dest, true, rotation);
		
		struct.UpdateMap(StructureType.FREE);
		struct.SetMapOccupation(destination, map_occupation00);
		struct.UpdateMap(this.moving_type);
		struct.PlaySoundMoving();
		//DuoDungeonPlugin.logg(DuoMap.game.toString());
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
				StructureType found = GetMap(newcoords[idx_other].x, newcoords[idx_other].z, DuoMap.StructureType.EMPTY);
				if (found != StructureType.FREE && found != StructureType.PIECE_UP)
				{
					canMove = false; // found a collision ...
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
		
		piece.UpdateMap(StructureType.FREE);
		piece.updateRotation(orientation);
		piece.SetMapOccupation(map_occupation, map_occupation00);
		piece.UpdateMap(StructureType.PIECE_UP);
		DuoDungeonPlugin.logg(DuoMap.game.toString());
	}
	
	public void MoveTiles(BlockVector3[] from, BlockVector3[] dest, boolean cut, int rotation)
	{
		int n_tiles = from.length;
		for (int idx=0; idx<n_tiles; idx+=1)
		{
			WEUtils.CopyRegion(WEWorld, from[idx], from[idx].add(BlockVector3.at(tile_size-1, max_height, tile_size-1)), dest[idx], cut, rotation);
		}
	}
	
	public void SetMap(int x, int z, StructureType value)
	{
		if (this.IsOutOfBounds(x, z))
			DuoDungeonPlugin.err(String.valueOf(x) + " " + String.valueOf(z));
		else
			map[x][z] = value;
	}
	
	public StructureType GetMap(int x, int z, StructureType default_if_oob)
	{
		if (this.IsOutOfBounds(x, z))
			return default_if_oob;
		else
			return map[x][z];
	}
	
	public boolean IsOutOfBounds(int x, int z)
	{
		return x < 0 || x >= XMax || z < 0 || z >= ZMax;			
	}
	
	public String toString()
	{
		String ret = "";
		for (int x = XMax-1; x >= 0 ; x-=1)
		{
			for (int z = 0 ; z < ZMax ; z+=1)
			{
				ret += String.valueOf(this.GetMap(x,z, DuoMap.StructureType.EMPTY).getValue());
			}
			ret += "\n";
		}
		
		ret += "\n\n" + this.Square5MapToString();

		return ret;
	}
	
	public String Square5MapToString()
	{
		String ret = "";
		for (int x = XMax-1; x >= 0 ; x-=1)
		{
			for (int z = 0 ; z < ZMax ; z+=1)
			{
				ret += String.valueOf(square5[x][z]);
			}
			ret += "\n";
		}
		return ret;
	}
	
	public void PlaceTileAt(int x_tile, int z_tile)
	{
		this.SetMap(x_tile, z_tile, DuoMap.StructureType.PIECE);
		UpdateNeighbourPieces(x_tile, z_tile, 1);
		for(int x_square5_center=MyMath.Max(x_tile-(square5_size-1)/2, 0); x_square5_center<=MyMath.Min(x_tile+(square5_size-1)/2,square5.length); x_square5_center+=1)
		{
			for(int z_square5_center=MyMath.Max(z_tile-(square5_size-1)/2, 0); z_square5_center<=MyMath.Min(z_tile+(square5_size-1)/2,square5.length); z_square5_center+=1)
			{
				square5[x_square5_center][z_square5_center] += 1;
				if (square5[x_square5_center][z_square5_center] == square5_size*square5_size)
				{
					for(int x_tile2=MyMath.Max(x_square5_center-(square5_size-1)/2, 0); x_tile2<=MyMath.Min(x_square5_center+(square5_size-1)/2,square5.length); x_tile2+=1)
					{
						for(int z_tile2=MyMath.Max(z_square5_center-(square5_size-1)/2, 0); z_tile2<=MyMath.Min(z_square5_center+(square5_size-1)/2,square5.length); z_tile2+=1)
						{
							RemoveTileFromSquare5(x_tile2, z_tile2, false);
						}
					}
					// stun all mobs
					ApplySuperStun();
				}
			}
		}
	}
	
	public void RemoveTileFromSquare5(int x, int z, boolean removed_because_piece_destroyed)
	{
		if(already_decreased_tile_from_square5[x][z]) {
			if(removed_because_piece_destroyed)
			{
				already_decreased_tile_from_square5[x][z] = false;
				return;
			}
			else
				DuoDungeonPlugin.logg("WARNING: this case should never happen: trying to decrease square5 twice");
		}
		else {
			if(!removed_because_piece_destroyed)
				already_decreased_tile_from_square5[x][z] = true;
		}
			
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
		PlaySound(Sound.BLOCK_ANVIL_PLACE, 0);
		for (LivingEntity e : entities)
		{
			if (e instanceof Monster)
			{
				e.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, square5_effectduration, 15));
				e.setArrowCooldown(square5_effectduration);
			}
		}
	}
	
	private void PlaySound(Sound sound, float pitch) {
		for (DuoTeammate player : DuoTeam.GetAllPlayers())
		{
			Player p = player.getDuoPlayer().getPlayer();
			p.playSound(p.getLocation(), sound, (float) 1.0, pitch);
		}
	}

	public void UpdateNeighbourPieces(int x, int z, int delta)
	{
		this.neighbour_pieces[x-1][z] += delta;
		this.neighbour_pieces[x+1][z] += delta;
		this.neighbour_pieces[x][z-1] += delta;
		this.neighbour_pieces[x][z+1] += delta;
	}
	
	public boolean CanPlacePiece()
	{
		for (Index2d idx : moving_piece.map_occupation)
		{
			if(neighbour_pieces[idx.x][idx.z] > 0)
				return true;
		}
		return false;
	}
	
	public void EnableNextIsBomb()
	{
		this.next_is_bomb = DuoBuilder.DecreaseBombCount();
	}
	
	public boolean IsNextBomb()
	{
		return this.next_is_bomb;
	}
}
