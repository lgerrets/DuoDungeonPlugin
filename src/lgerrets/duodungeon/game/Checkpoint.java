package lgerrets.duodungeon.game;

import lgerrets.duodungeon.game.DuoMap.StructureType;
import lgerrets.duodungeon.utils.Coords3d;
import lgerrets.duodungeon.utils.Index2d;

public class Checkpoint extends Structure {
	static Coords3d checkpoint_origin = Coords3d.FromWaypoint("checkpoint");
	
	public Checkpoint(Index2d occupation)
	{
		map_occupation00 = occupation;
		clone_from = new Index2d[] { 
				new Index2d(0,0), new Index2d(1,0), new Index2d(2,0),
				new Index2d(0,1), new Index2d(1,1), new Index2d(2,1),
				new Index2d(0,2), new Index2d(1,2), new Index2d(2,2),
				};
		n_tiles = clone_from.length;
		map_occupation = new Index2d[n_tiles];
		for (int idx=0; idx<n_tiles; idx+=1)
			map_occupation[idx] = map_occupation00.add(clone_from[idx]);
		structure_type = StructureType.CHECKPOINT;
		this.UpdateMap(structure_type);
	}

	public Coords3d GetTemplateOrigin() {
		return checkpoint_origin;
	}
}
