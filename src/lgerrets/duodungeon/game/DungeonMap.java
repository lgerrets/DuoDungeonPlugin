package lgerrets.duodungeon.game;

import java.util.ArrayDeque;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.Material;

import lgerrets.duodungeon.ConfigManager;
import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.game.Piece.TetrisShape;
import lgerrets.duodungeon.utils.Coords3d;
import lgerrets.duodungeon.utils.Index2d;
import lgerrets.duodungeon.utils.Index2d.Direction;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.block.BlockReplace;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.BlockPattern;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;

public class DungeonMap {
	
	private int[][] map;
	static private Coords3d dungeon_origin;
	static private Coords3d pastebin;
	static public int tile_size = ConfigManager.DDConfig.getInt("tile_size");
	static public int max_height = ConfigManager.DDConfig.getInt("max_height");;
	static public World world = Bukkit.getWorld(ConfigManager.DDConfig.getString("world"));
	static private com.sk89q.worldedit.world.World WEWorld = new BukkitWorld(world);
	private ArrayDeque<Piece> pieces;
	private boolean is_running;
	private Piece moving_piece = null;
	
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
	
	static public DungeonMap game = new DungeonMap(false);
	
	static public void InitializeDungeon()
	{
		game = new DungeonMap(true);
	}
	
	public DungeonMap(boolean is_running)
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
			pieces = new ArrayDeque<Piece>();
			ClearArea();
			SpawnNewPiece();
		}
	}
	
	public boolean IsRunning()
	{
		return is_running;
	}
	
	public void SpawnNewPiece()
	{
		if (moving_piece != null)
			moving_piece.PlacePiece(dungeon_origin);
	
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
			piece_dest[idx] = Coords3d.Index2dToBlockVector3(piece.map_occupation[idx], dungeon_origin);
		}
		
		MoveTiles(piece_from, pastebins, false);
		MoveTiles(pastebins, piece_dest, true);
		moving_piece = piece;

		DuoDungeonPlugin.logg(this.ToString());
	}
	
	public void SpawnNewPieceOld()
	{
		Piece piece = new Piece();
		pieces.add(piece);
		Index2d[] map_occupation_ = {new Index2d(0,0), new Index2d(0,1), new Index2d(1,0), new Index2d(1,1)};
		piece.SetMapOccupation(map_occupation_);
		Index2d[] destination = {new Index2d(2,2), new Index2d(2,3), new Index2d(3,2), new Index2d(3,3)};
			
		int n_tiles = piece.map_occupation.length;
		BlockVector3[] piece_from = new BlockVector3[n_tiles];
		BlockVector3[] pastebins = new BlockVector3[n_tiles];
		BlockVector3[] piece_dest = new BlockVector3[n_tiles];
		for (int idx=0; idx<n_tiles; idx+=1)
		{
			//piece_from[idx] = Index2dToBlockVector3(piece.map_occupation[idx], Piece.template_origins.get(TetrisShape.O));
			pastebins[idx] = (new Coords3d(pastebin.x, pastebin.y, pastebin.z + idx*tile_size)).toBlockVector3();
			piece_dest[idx] = Coords3d.Index2dToBlockVector3(destination[idx], dungeon_origin);
		}
		
		MoveTiles(piece_from, pastebins, false);
		MoveTiles(pastebins, piece_dest, true);
	}
	
	public void ClearArea()
	{
		BlockVector3 dungeon_origin_3 = dungeon_origin.toBlockVector3();
		BlockVector3 temp_3;
		BlockArrayClipboard clipboard;
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
					dy = max_height;
					mat = Material.AIR.createBlockData();
					break;
				case 2:
					dy = 0;
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
				clipboard = new BlockArrayClipboard(region);
				volume += region.getVolume();
				
				try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(WEWorld, -1)) { // get the edit session and use -1 for max blocks for no limit, this is a try with resources statement to ensure the edit session is closed after use
					editSession.setBlocks(region, BukkitAdapter.adapt(mat));
				} catch (WorldEditException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
			Index2d newcoord = coord.CalculateRelative(1, d); // calculate where this tile would go
			newcoords[idx] = newcoord;
			if (this.GetMap(newcoord.x,newcoord.z, 1) > 0) // the destination tile is occupied...
			{
				canMove = false;
				DuoDungeonPlugin.logg("can't move");
				for (Index2d other : moving_piece.map_occupation)
				{
					if (other.equals(newcoord)) // ... actually it is occupied by a tile of this piece
					{
						canMove = true;
						DuoDungeonPlugin.logg("can move!");
						break;
					}
				}
				if (!canMove)
					break;
			}
			idx += 1;
		}
    	DuoDungeonPlugin.logg(canMove);
		if (canMove)
		{
			MovePiece(moving_piece, newcoords, true);
		}
	}
	
	public void MovePiece(Piece piece, Index2d[] destination, boolean cut)
	{
		int n_tiles = piece.map_occupation.length;
		BlockVector3[] piece_from = new BlockVector3[n_tiles];
		BlockVector3[] pastebins = new BlockVector3[n_tiles];
		BlockVector3[] piece_dest = new BlockVector3[n_tiles];
		for (int idx=0; idx<n_tiles; idx+=1)
		{
			piece_from[idx] = Coords3d.Index2dToBlockVector3(piece.map_occupation[idx], dungeon_origin);
			pastebins[idx] = (new Coords3d(pastebin.x, pastebin.y, pastebin.z + idx*tile_size)).toBlockVector3();
			piece_dest[idx] = Coords3d.Index2dToBlockVector3(destination[idx], dungeon_origin);
		}
		
		MoveTiles(piece_from, pastebins, cut);
		MoveTiles(pastebins, piece_dest, true);
		
		for (Index2d idx : piece.map_occupation)
		{
			this.SetMap(idx.x,idx.z,0);
		}
		for (Index2d idx : destination)
		{
			this.SetMap(idx.x,idx.z,1);
		}
		piece.SetMapOccupation(destination);
	}
	
	public void MoveTiles(BlockVector3[] from, BlockVector3[] dest, boolean cut)
	{
		int n_tiles = from.length;
		for (int idx=0; idx<n_tiles; idx+=1)
		{
			/*
			DuoDungeonPlugin.getWorldEdit().getWorldEdit().newEditSession(world);
			//DuoDungeonPlugin.getWorldEdit().getWorldEdit().getEditSessionFactory().getEditSession(() world, -1);
			*/
			CuboidRegion region = new CuboidRegion(WEWorld, from[idx], BlockVector3.at(from[idx].getBlockX()+tile_size-1, from[idx].getBlockY()+max_height, from[idx].getBlockZ()+tile_size-1));
			BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

			try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(WEWorld, -1)) {
			    ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
			        editSession, region, clipboard, region.getMinimumPoint()
			    );
			    // configure here
			    if (cut)
			    	forwardExtentCopy.setSourceFunction(new BlockReplace(editSession, BukkitAdapter.adapt(Material.AIR.createBlockData())));
			    Operations.complete(forwardExtentCopy);
			} catch (WorldEditException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(WEWorld, -1)) {
			    Operation operation = new ClipboardHolder(clipboard)
			            .createPaste(editSession)
			            .to(dest[idx])
			            // configure here
			            .build();
			    Operations.complete(operation);
			} catch (WorldEditException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	
	public String ToString()
	{
		String ret = "";
		for (int x = 0; x < map.length ; x+=1)
		{
			for (int z = 0 ; z < map[0].length ; z+=1)
			{
				ret += String.valueOf(this.GetMap(x,z, -1));
			}
			ret += "\n";
		}
		return ret;
	}
}
