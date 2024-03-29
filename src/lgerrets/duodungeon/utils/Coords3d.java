package lgerrets.duodungeon.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import com.sk89q.worldedit.math.BlockVector3;

import lgerrets.duodungeon.ConfigManager;
import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.game.DuoMap;
import lgerrets.duodungeon.utils.Index2d.Direction;

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
	
	public Coords3d(double X, double Y, double Z)
	{
		x = (int) X;
		y = (int) Y;
		z = (int) Z;
	}
	
	public Coords3d(Coords3d o)
	{
		this(o.x, o.y, o.z);
	}
	
	public Coords3d clone()
	{
		return new Coords3d(this);
	}
	
	public Coords3d(Location loc)
	{
		this(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}
	
	public Index2d toIndex2d(Coords3d origin) {
		int dx = this.x - origin.x;
		int dz = this.z - origin.z;
		return new Index2d((int) Math.floor(dx/tile_size), (int) Math.floor(dz/tile_size));
	}
	
	public Location toLocation(World world)
	{
		return new Location(world, x, y, z);
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
	
	public Coords3d diff(Coords3d o)
	{
		return this.add(- o.x, - o.y, - o.z);
	}
	
	public Coords3d add(int X, int Y, int Z)
	{
		return new Coords3d(x+X, y+Y, z+Z);
	}
	
	public Coords3d scale(double m)
	{
		return new Coords3d(x*m, y*m, z*m);
	}
	
	public Coords3d CalculateTranslation(int delta, Direction d)
	{
		int newx = x;
		int newz = z;
		if ((d == Direction.NORTH) || (d == Direction.WEST))
			delta = -delta;
		if ((d == Direction.NORTH) || (d == Direction.SOUTH))
			newz += delta;
		else
			newx += delta;
		return new Coords3d(newx, this.y, newz);
	}
	
	public String toString()
	{
		return "("+String.valueOf(x)+","+String.valueOf(y)+","+String.valueOf(z)+")";
	}
	
	public Coords3d CalculateRotation(Coords3d center, boolean orientation)
	{
		Coords3d relative = new Coords3d(this.x - center.x, this.y - center.y, this.z - center.z);
		if (!orientation)
			relative = new Coords3d(- relative.z, relative.y, relative.x);
		else
			relative = new Coords3d(relative.z, relative.y, - relative.x);
		return center.add(relative);
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


