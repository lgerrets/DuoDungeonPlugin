package lgerrets.duodungeon.utils;

public class Index2d {
	
	public enum Direction
	{
		NORTH, // north = z-
		EAST, // east = x+
		SOUTH, // south = z+
		WEST, // west = x-
	}
	
	// (0,0) is at the north-west corner
	public int x; // x++ = go east
	public int z; // z++ = go south
	
	public Index2d(int X, int Z)
	{
		x = X;
		z = Z;
	}
	
	public Index2d CalculateTranslation(int delta, Direction d)
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
	
	public Index2d CalculateRotation(Index2d center, boolean orientation)
	{
		Index2d relative = new Index2d(this.x-center.x, this.z-center.z);
		if (!orientation)
			relative = new Index2d(-relative.z, relative.x);
		else
			relative = new Index2d(relative.z, -relative.x);
		return center.add(relative);
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
	
	public String toString()
	{
		return "(" + String.valueOf(this.x) + "," + String.valueOf(this.z) + ")";
	}
	
	static public Direction DeltasToDirection(double dx, double dz)
	{
		Direction d;
    	if (MyMath.Abs(dx) > MyMath.Abs(dz))
    	{
    		if (dx > 0)
    			d = Direction.EAST;
    		else
    			d = Direction.WEST;
    	}
    	else
    	{
    		if (dz > 0)
    			d = Direction.SOUTH;
    		else
    			d = Direction.NORTH;
    	}
    	return d;
	}
}
