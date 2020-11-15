package lgerrets.duodungeon.utils;

import com.sk89q.worldedit.math.BlockVector3;

public class Coords3d {
	public int x; // x++ = go east
	public int y;
	public int z; // z++ = go south
	
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
}


