package lgerrets.duodungeon.utils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

import lgerrets.duodungeon.DuoDungeonPlugin;

import java.util.Random;
import java.util.Set;

public class MyMath {
	private static Random randomizer = new Random();

	public static int Abs(int x)
	{
		if (x < 0)
			return -x;
		return x;
	}
	
	public static double Abs(double x)
	{
		if (x < 0)
			return -x;
		return x;
	}
	
	public static int Min(int a, int b)
	{
		if (a < b)
			return a;
		else
			return b;
	}
	
	public static int Max(int a, int b)
	{
		if (a > b)
			return a;
		else
			return b;
	}
	
	public static int RandomUInt(int max)
	{
		return Abs(randomizer.nextInt() % max);
	}
	
	public static Integer[] RandomUInts(int nb, int max, boolean distincts)
	{
		if ((max < nb) && distincts)
		{
			DuoDungeonPlugin.logg("WARNING: Forcing distincts=false in RandomUInts");
			distincts = false;
		}
		ArrayList<Integer> rnds = new ArrayList<Integer>();
		int size = 0;
		while (size < nb)
		{
			Integer rnd = RandomUInt(max);
			if (! (distincts && rnds.contains(rnd)))
			{
				rnds.add(rnd);
				size += 1;
			}
		}
		return rnds.toArray(new Integer[nb]);
	}
	
	public static Double RandomFloat()
	{
		return randomizer.nextDouble();
	}
	
	public static <T> T RandomChoice(Set<Entry<T, Double>> probs)
	{
		Double rnd = RandomFloat();
		Double thresh = 0.0;
		for (Map.Entry<T,Double> entry : probs)
		{
			thresh += entry.getValue();
			if (rnd < thresh)
				return entry.getKey();
		}
		return null;
	}
}
