package lgerrets.duodungeon.game;

import java.util.ArrayList;

import org.bukkit.Material;

import com.sk89q.worldedit.regions.CuboidRegion;

import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.utils.Coords3d;
import lgerrets.duodungeon.utils.Index2d;
import lgerrets.duodungeon.utils.WEUtils;

public class Structure {
	protected static int tile_size = DuoMap.tile_size;
	public Index2d[] map_occupation;
	protected int n_tiles;
	public Index2d map_occupation00;

	public void Delete()
	{
		for (int i_tile=0; i_tile < n_tiles; i_tile+=1)
		{
			Coords3d tile_origin = Coords3d.Index2dToCoords3d(map_occupation[i_tile], DuoMap.dungeon_origin);
			CuboidRegion region = new CuboidRegion(DuoMap.WEWorld, tile_origin.toBlockVector3(), tile_origin.add(tile_size-1,DuoMap.max_height+DuoMap.not_placed_height,tile_size-1).toBlockVector3());
			WEUtils.FillRegion(DuoMap.WEWorld, region, Material.AIR.createBlockData());
		}
		for (int i_tile=0; i_tile<n_tiles; i_tile+=1)
		{
			RemoveTileAt(map_occupation[i_tile].x, map_occupation[i_tile].z);
		}
	}
	
	public void RemoveTileAt(int x, int z)
	{
		DuoMap.game.SetMap(x, z, DuoMap.StructureType.FREE);
	}
	
	public void SetMapOccupation(Index2d[] map_occupation_, Index2d map_occupation00)
	{
		map_occupation = map_occupation_;
		this.map_occupation00 = map_occupation00;
		DuoDungeonPlugin.logg(this.map_occupation00.toString());
	}
	
	public void UpdateMap(DuoMap.StructureType type)
	{
		for (Index2d idx : map_occupation)
			DuoMap.game.SetMap(idx.x, idx.z, type);
	}
	
	protected static ArrayList<Coords3d> SearchBlock(Coords3d origin, Material mat)
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
	
	protected boolean HasCoords3d(Coords3d coords)
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

}
