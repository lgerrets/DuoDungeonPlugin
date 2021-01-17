package lgerrets.duodungeon.game;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;

import lgerrets.duodungeon.ConfigManager;
import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.game.Drops.ChestRarity;
import lgerrets.duodungeon.game.Drops.DropType;
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
        			cp.mobs = DuoMap.world.getNearbyEntities(cp.bbox, new Structure.IsMob());
        			for (Mob mob : cp.mobs)
        				mob.setAI(false);
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
	private Coords3d merchant_pos;
	private Merchant merchant;
	private Collection<Mob> mobs;
	private BoundingBox bbox;
	
	public Checkpoint(Index2d occupation)
	{
		map_occupation00 = occupation;
		clone_from = new Index2d[] { 
				new Index2d(0,0), new Index2d(1,0), new Index2d(2,0),
				new Index2d(0,1), new Index2d(1,1), new Index2d(2,1),
				new Index2d(0,2), new Index2d(1,2), new Index2d(2,2),
				}; 	// !!! IN MANY PLACES IN Checkpoint.java, THIS IS ASSUMED TO BE A SQUARE
		n_tiles = clone_from.length;
		map_occupation = new Index2d[n_tiles];
		for (int idx=0; idx<n_tiles; idx+=1)
			map_occupation[idx] = map_occupation00.add(clone_from[idx]);
		this.SetMapOccupation(map_occupation, map_occupation00);
		structure_type = StructureType.CHECKPOINT;
		this.UpdateMap(structure_type);
		active = false;
		
		// z-surround the checkpoint with indestructible invisible obstacle tiles
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
		
		// init bbox
		bbox = BoundingBox.of(Coords3d.Index2dToLocation(map_occupation[0], DuoMap.dungeon_origin), 
				Coords3d.Index2dToLocation(map_occupation[map_occupation.length-1].add(1,1), DuoMap.dungeon_origin).add(-1,DuoMap.max_height,-1));
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
		
		// play sound
		Location loc = this.placed_pos.add(0,DuoMap.floor_level,0).toLocation(DuoMap.world);
		double volume = 1.0;
		for (DuoRunner runner : DuoTeam.runner_players)
		{
			Player p = runner.getDuoPlayer().getPlayer();
			p.playSound(loc, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, (float) volume, 1);
		}
		
		// find pos of villager merchant
		ArrayList<Coords3d> vill_pos = new ArrayList<Coords3d>();
		for (Index2d tile_idx : map_occupation) // loop through all tiles of template
		{
			Coords3d tile_origin = Coords3d.Index2dToCoords3d(tile_idx, DuoMap.dungeon_origin); // tile origin
			vill_pos.addAll(SearchBlock(tile_origin, Material.GOLD_BLOCK));
		}
		if (vill_pos.size() != 1)
			DuoDungeonPlugin.err("Found " + String.valueOf(vill_pos.size()) + " gold blocks in a checkpoint, but I expected 1.");
		merchant_pos = vill_pos.get(0);
		DuoDungeonPlugin.logg(merchant_pos);
		
		// summon merchant
		merchant = new Merchant(merchant_pos.toLocation(DuoMap.world).add(0.5,1.,0.5));
		for (int i_recipe=0; i_recipe<5; i_recipe+=1) {
			ItemStack result = Drops.DrawDrop(ChestRarity.LEGENDARY, DuoMap.game.tier, DropType.UNSPECIFIED, DropType.MONEY);
			ItemStack item1 = Drops.DrawDrop(ChestRarity.LEGENDARY, DuoMap.game.tier, DropType.MONEY, DropType.UNSPECIFIED);
			item1.setAmount(item1.getAmount());
			merchant.AddRecipe(result, item1);
		}
	}
	
	public boolean HasCoords3d(Coords3d coords)
	{
		return bbox.contains(coords.x, coords.y, coords.z);
	}
}
