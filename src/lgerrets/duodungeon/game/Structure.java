package lgerrets.duodungeon.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.util.BoundingBox;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;

import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.game.DuoMap.StructureType;
import lgerrets.duodungeon.utils.Coords3d;
import lgerrets.duodungeon.utils.Index2d;
import lgerrets.duodungeon.utils.WEUtils;

public abstract class Structure {
	protected static int tile_size = DuoMap.tile_size;
	public Index2d[] map_occupation;
	public Index2d[] clone_from;
	protected int n_tiles;
	public Index2d map_occupation00;
	public StructureType structure_type;
	
	public static class IsMob<Entity> implements Predicate<Entity> {
		@Override
		public boolean test(Entity o) {
			return (o instanceof Mob);
		}
	}

	public void Delete()
	{
		// physically clean the cuboid
		for (int i_tile=0; i_tile < n_tiles; i_tile+=1)
		{
			Coords3d tile_origin = Coords3d.Index2dToCoords3d(map_occupation[i_tile], DuoMap.dungeon_origin);
			CuboidRegion region = new CuboidRegion(DuoMap.WEWorld, tile_origin.toBlockVector3(), tile_origin.add(tile_size-1,DuoMap.max_height+DuoMap.not_placed_height,tile_size-1).toBlockVector3());
			WEUtils.FillRegion(DuoMap.WEWorld, region, Material.AIR.createBlockData());
			Collection<Entity> mobs = DuoMap.world.getNearbyEntities(new BoundingBox(tile_origin.x, tile_origin.y, tile_origin.z,
					tile_origin.x+tile_size, tile_origin.y+DuoMap.max_height, tile_origin.z+tile_size), new IsMob());
			for (Entity mob : mobs)
				mob.remove();
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
	
	public void InitialClone(Coords3d pastebin, int add_y)
	{
		BlockVector3[] piece_from = new BlockVector3[n_tiles];
		BlockVector3[] pastebins = new BlockVector3[n_tiles];
		BlockVector3[] piece_dest = new BlockVector3[n_tiles];
		Coords3d template_origin = this.GetTemplateOrigin();
		for (int idx=0; idx<n_tiles; idx+=1)
		{
			piece_from[idx] = Coords3d.Index2dToBlockVector3(this.clone_from[idx], template_origin);
			pastebins[idx] = (new Coords3d(pastebin.x, pastebin.y, pastebin.z + idx*tile_size)).toBlockVector3();
			piece_dest[idx] = Coords3d.Index2dToBlockVector3(this.map_occupation[idx], DuoMap.dungeon_origin.add(0,add_y,0));
		}
		
		DuoMap.game.MoveTiles(piece_from, pastebins, false, 0);
		DuoMap.game.MoveTiles(pastebins, piece_dest, true, 0);
	}
	
	abstract public Coords3d GetTemplateOrigin();

}
