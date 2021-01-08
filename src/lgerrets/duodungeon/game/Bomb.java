package lgerrets.duodungeon.game;

import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.game.DuoMap.StructureType;
import lgerrets.duodungeon.game.Piece.TetrisShape;
import lgerrets.duodungeon.utils.Coords3d;
import lgerrets.duodungeon.utils.Index2d;
import lgerrets.duodungeon.utils.MyMath;

public class Bomb extends Structure {
	static Coords3d bomb_origin = Coords3d.FromWaypoint("bomb");

	public Bomb(Index2d occupation)
	{
		map_occupation00 = occupation;
		map_occupation = new Index2d[] {occupation};
		n_tiles = 1;
		clone_from = new Index2d[] {new Index2d(0,0) };
		structure_type = StructureType.BOMB;
		this.UpdateMap(structure_type);
	}
	
	public static Bomb SpawnBomb(StructureType[][] map)
	{
		boolean found = false;
		int tries = 0;
		Index2d bomb_occupation = new Index2d();
		while (!found)
		{
			tries += 1;
			if (tries > 100)
			{
				DuoDungeonPlugin.logg("Unable to place piece, dungeon is too full... Will likely crash!");
				return null;
			}
			bomb_occupation.x = DuoMap.game.max_placed_x + DuoMap.struct_spawn_dist;
			bomb_occupation.z = MyMath.RandomUInt(map[0].length);
			found = map[bomb_occupation.x][bomb_occupation.z] == StructureType.FREE;
		}
		return new Bomb(bomb_occupation);
	}

	public Coords3d GetTemplateOrigin() {
		return bomb_origin;
	}
}
