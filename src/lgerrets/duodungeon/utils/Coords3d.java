package lgerrets.duodungeon.utils;

import org.bukkit.configuration.ConfigurationSection;

import com.sk89q.worldedit.math.BlockVector3;

import lgerrets.duodungeon.ConfigManager;

public class Coords3d {
	public int x; // x++ = go east
	public int y;
	public int z; // z++ = go south
	
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
	
	public Coords3d add(int X, int Y, int Z)
	{
		return new Coords3d(x+X, y+Y, z+Z);
	}
}


