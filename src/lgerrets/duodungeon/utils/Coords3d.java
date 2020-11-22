package lgerrets.duodungeon.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import com.sk89q.worldedit.math.BlockVector3;

import lgerrets.duodungeon.ConfigManager;
import lgerrets.duodungeon.game.DuoMap;

public class Coords3d {
	public int x; // x++ = go east
	public int y;
	public int z; // z++ = go south
	static int tile_size = DuoMap.tile_size;
	static World world = DuoMap.world;
	
	public Coords3d()
	{
		this(0, 0, 0);
	}
	
	public Coords3d(int X, int Y, int Z)
	{
		x = X;
		y = Y;
		z = Z;
	}
	
	public BlockVector3 toBlockVector3()
	{
		return BlockVector3.at((int) x, (int) y, (int) z);
	}
	
	public static Coords3d FromWaypoint(String waypoint_name)
	{
		Coords3d coords = new Coords3d();
		ConfigurationSection section = ConfigManager.DDConfig.getConfigurationSection("Waypoints").getConfigurationSection(waypoint_name);
		coords.x = section.getInt("X");
		coords.y = section.getInt("Y");
		coords.z = section.getInt("Z");
		return coords;
	}
	
	public Coords3d add(Coords3d o)
	{
		return this.add(o.x, o.y, o.z);
	}
	
	public Coords3d add(int X, int Y, int Z)
	{
		return new Coords3d(x+X, y+Y, z+Z);
	}
	
	public String toString()
	{
		return "("+String.valueOf(x)+","+String.valueOf(y)+","+String.valueOf(z)+")";
	}
	
	static public Coords3d Index2dToCoords3d(Index2d idx, Coords3d origin)
	{
		return new Coords3d(origin.x + tile_size*idx.x,
				origin.y,
				origin.z + tile_size*idx.z);
	}
	
	static public Location Index2dToLocation(Index2d coords, Coords3d origin)
	{
		return new Location(world,
				origin.x + tile_size*coords.x,
				origin.y,
				origin.z + tile_size*coords.z);
	}
	
	static public BlockVector3 Index2dToBlockVector3(Index2d coords, Coords3d origin)
	{
		return BlockVector3.at((int) origin.x + tile_size*coords.x,
				(int) origin.y,
				(int) origin.z + tile_size*coords.z);
	}
	
	static public Coords3d[] CalculateExtremaCorners(Coords3d a, Coords3d b)
	{
		Coords3d[] ret = new Coords3d[2];
		ret[0] = new Coords3d(MyMath.Min(a.x, b.x), MyMath.Min(a.y, b.y), MyMath.Min(a.z, b.z));
		ret[1] = new Coords3d(MyMath.Max(a.x, b.x), MyMath.Max(a.y, b.y), MyMath.Max(a.z, b.z));
		return ret;
	}
}


