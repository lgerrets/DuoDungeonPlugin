package lgerrets.duodungeon.utils;

import java.util.Random;

public class MyMath {
	private static Random randomizer = new Random();

	public static int Abs(int x)
	{
		if (x < 0)
			return -x;
		return x;
	}
	
	public static int RandomUInt(int max)
	{
		return Abs(randomizer.nextInt() % max);
	}
}
