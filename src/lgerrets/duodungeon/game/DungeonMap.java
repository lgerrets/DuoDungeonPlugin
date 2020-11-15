package lgerrets.duodungeon.game;

import java.util.ArrayDeque;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import lgerrets.duodungeon.ConfigManager;
import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.utils.Coords3d;
import lgerrets.duodungeon.utils.Index2d;
import lgerrets.duodungeon.utils.Index2d.Direction;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;

public class DungeonMap {
	
	private int[][] map;
	private Coords3d dungeon_origin;
	private Coords3d pastebin;
	private int tile_size;
	private World world;
	private com.sk89q.worldedit.world.World WEWorld;
	private ArrayDeque<Piece> pieces;
	
	public DungeonMap()
	{
		map = new int[][] {
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
		};
		ConfigurationSection origin_wp = ConfigManager.DDConfig.getConfigurationSection("Waypoints").getConfigurationSection("dungeon_origin");
		dungeon_origin = new Coords3d((int)origin_wp.get("X"),
							  (int)origin_wp.get("Y"),
							  (int)origin_wp.get("Z"));
		ConfigurationSection pastebin_wp = ConfigManager.DDConfig.getConfigurationSection("Waypoints").getConfigurationSection("pastebin");
		pastebin = new Coords3d((int)origin_wp.get("X"),
								(int)origin_wp.get("Y"),
								(int)origin_wp.get("Z"));
		tile_size = (int)ConfigManager.DDConfig.get("tile_size");
		world = Bukkit.getWorld((String)ConfigManager.DDConfig.get("World"));
		WEWorld = (com.sk89q.worldedit.world.World) world;
	}
	
	public void SpawnNewPiece()
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
			piece_from[idx] = Index2dToBlockVector3(piece.map_occupation[idx], Piece.tetris_o_origin);
			pastebins[idx] = (new Coords3d(pastebin.x, pastebin.y, pastebin.z + idx*tile_size)).toBlockVector3();
			piece_dest[idx] = Index2dToBlockVector3(destination[idx], dungeon_origin);
		}
		
		MoveTiles(piece_from, pastebins, false);
		MoveTiles(pastebins, piece_dest, true);
	}
	
	public void TryMovePiece(Piece piece, Direction d)
	{
		boolean canMove = true;
		Index2d[] newcoords = new Index2d[piece.map_occupation.length];
		int idx = 0;
		for (Index2d coord : piece.map_occupation) // loop through each tile of this piece
		{
			Index2d newcoord = coord.CalculateRelative(1, d); // calculate where this tile would go
			newcoords[idx] = newcoord;
			if (map[newcoord.x][newcoord.z] > 0) // the destination tile is occupied...
			{
				canMove = false;
				for (Index2d other : piece.map_occupation)
					if (other == newcoord) // ... actually it is occupied by a tile of this piece
					{
						canMove = true;
						break;
					}
				break;
			}
			idx += 1;
		}
		
		if (canMove)
		{
			MovePiece(piece, newcoords, true);
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
			piece_from[idx] = Index2dToBlockVector3(piece.map_occupation[idx], dungeon_origin);
			pastebins[idx] = (new Coords3d(pastebin.x, pastebin.y, pastebin.z + idx*tile_size)).toBlockVector3();
			piece_dest[idx] = Index2dToBlockVector3(destination[idx], dungeon_origin);
		}
		
		MoveTiles(piece_from, pastebins, cut);
		MoveTiles(pastebins, piece_dest, true);
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
			CuboidRegion region = new CuboidRegion((com.sk89q.worldedit.world.World) world, from[idx], BlockVector3.at(from[idx].getBlockX()+tile_size, from[idx].getBlockY()+20, from[idx].getBlockZ()+tile_size));
			BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

			try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(WEWorld, -1)) {
			    ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
			        editSession, region, clipboard, region.getMinimumPoint()
			    );
			    // configure here
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
	
	public Location Index2dToLocation(Index2d coords, Coords3d origin)
	{
		return new Location(world,
				origin.x + tile_size*coords.x,
				origin.y,
				origin.z + tile_size*coords.z);
	}
	
	public BlockVector3 Index2dToBlockVector3(Index2d coords, Coords3d origin)
	{
		return BlockVector3.at((int) origin.x + tile_size*coords.x,
				(int) origin.y,
				(int) origin.z + tile_size*coords.z);
	}
}
