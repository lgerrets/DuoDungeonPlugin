package lgerrets.duodungeon.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Loc {
	private Number x,y,z;
	private float pitch,yaw;
	private String world;
	
	public Loc(String world, Number x, Number y, Number z)
	{
		this(world,x,y,z,0,0);
	}
	
	public Loc(String world, Number x, Number y, Number z, float pitch, float yaw)
	{
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
	}
	
	public Location toLocation()
	{
		return new Location(Bukkit.getWorld(world),x.doubleValue(),y.doubleValue(),z.doubleValue(),yaw,pitch);
	}
}
