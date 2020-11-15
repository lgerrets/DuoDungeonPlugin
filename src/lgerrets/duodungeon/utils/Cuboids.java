package lgerrets.duodungeon.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Cuboids {
	/*
	static void Copy(World world, Coords3d u0, Coords3d u1, Coords3d v0, boolean cut)
	{
		int dx, dy, dz;
		if (u0.x < u1.x)
			dx = 1;
		else
			dx = -1;
		if (u0.y < u1.y)
			dy = 1;
		else
			dy = -1;
		if (u0.z < u1.z)
			dz = 1;
		else
			dz = -1;
		int x2 = v0.x;
		int y2 = v0.y;
		int z2 = v0.z;
		x2 = v0.x;
		Block block;
		for (int x=u0.x; ((x<u1.x) ^ (dx==-1)) || (x==u1.x); x+=dx)
		{
			y2 = v0.y;
			for (int y=u0.y; ((y<u1.y) ^ (dy==-1)) || (y==u1.y); y+=dy)
			{
				z2 = v0.z;
				for (int z=u0.z; ((z<u1.z) ^ (dz==-1)) || (z==u1.z); z+=dz)
				{
					block = world.getBlockAt(x, y, z);
					Location loc = new Location(world, (double) x, (double) y, (double) z);
					block.
					z2 += dz;
				}
				y2 += dy;
			}
			x2 += dx;
		}
	}*/
}
