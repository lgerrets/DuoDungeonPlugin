package lgerrets.duodungeon.game;

import lgerrets.duodungeon.utils.Index2d;

public class Bomb extends Structure {
	public Bomb(Index2d occupation)
	{
		map_occupation00 = occupation;
		map_occupation = new Index2d[] {occupation};
		n_tiles = 1;
	}
}
