package lgerrets.duodungeon.game;

import org.bukkit.Bukkit;

import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.game.DuoMap.StructureType;
import lgerrets.duodungeon.players.DuoRunner;
import lgerrets.duodungeon.players.DuoTeam;
import lgerrets.duodungeon.utils.Coords3d;
import lgerrets.duodungeon.utils.Index2d;

public class Checkpoint extends Structure {
	static Coords3d checkpoint_origin = Coords3d.FromWaypoint("checkpoint");
	
	static private class TrackActiveCheckpoint implements Runnable {
		public TrackActiveCheckpoint()
		{
			super();
		}
		
        @Override
        public void run() {
        	if (DuoMap.game.IsRunning())
        	{
        		for (Checkpoint cp : DuoMap.game.checkpoints)
        		{
        			if (!cp.active) {
		        		for (DuoRunner runner : DuoTeam.runner_players)
		        		{
		        			if(cp.HasCoords3d(new Coords3d(runner.getDuoPlayer().getPlayer().getLocation())))
		        				cp.Activate();
		        		}
        			}
        		}
        	}
        }
	}
	
	static {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(DuoDungeonPlugin.getInstance(), new TrackActiveCheckpoint(), 0, 1);
	}
	
	private boolean active;
	
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
		active = false;
		
		Index2d[] put_invisible = new Index2d[] {
				new Index2d(0,-1), new Index2d(1,-1), new Index2d(2,-1),
				new Index2d(0,3), new Index2d(1,3), new Index2d(2,3),
		};
		for (Index2d idx : put_invisible)
		{
			idx = idx.add(map_occupation00);
			if (DuoMap.game.GetMap(idx.x, idx.z, StructureType.EMPTY) == StructureType.FREE)
			{
				DuoMap.game.SetMap(idx.x, idx.z, StructureType.PEACEFUL_INDESTRUCTIBLE_INVISIBLE);
			}
		}
	}

	public Coords3d GetTemplateOrigin() {
		return checkpoint_origin;
	}
	
	public void Activate()
	{
		active = true;
		for (int idx=0; idx<n_tiles; idx+=1)
			DuoMap.game.UpdateNeighbourPieces(map_occupation[idx].x, map_occupation[idx].z, 1);
		DuoMap.game.tier += 1;
	}
}
