package lgerrets.duodungeon.utils;

public class Index2d {
	
	public enum Direction
	{
		NORTH, // north
		EAST, // east
		SOUTH, // south
		WEST, // west
	}
	
	// (0,0) is at the north-west corner
	public int x; // x++ = go east
	public int z; // z++ = go south
	
	public Index2d(int X, int Z)
	{
		x = X;
		z = Z;
	}
	
	public Index2d CalculateRelative(int delta, Direction d)
	{
		int newx = x;
		int newz = z;
		if ((d == Direction.NORTH) || (d == Direction.WEST))
			delta = -delta;
		if ((d == Direction.NORTH) || (d == Direction.SOUTH))
			newz += delta;
		else
			newx += delta;
		return new Index2d(newx, newz);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof Index2d)
			return (((Index2d)obj).x == this.x) && (((Index2d)obj).z == this.z);
		else return false;
	}
	
	public Index2d add(Index2d obj)
	{
		return new Index2d(x+obj.x, z+obj.z);
	}
}
