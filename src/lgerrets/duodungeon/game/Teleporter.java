package lgerrets.duodungeon.game;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.game.DuoMap.StructureType;
import lgerrets.duodungeon.players.DuoRunner;
import lgerrets.duodungeon.utils.Coords3d;
import lgerrets.duodungeon.utils.Index2d;

public class Teleporter extends Structure {
	static Coords3d teleporter_origin = Coords3d.FromWaypoint("teleporter");

	boolean is_entrance; // otherwise exit
	Teleporter associated_exit;
	private BoundingBox bbox;
	
	public Teleporter(Index2d occupation, boolean is_entrance, Teleporter associated_exit)
	{
		map_occupation00 = occupation;
		clone_from = new Index2d[] { 
				new Index2d(0,0)
				};
		n_tiles = clone_from.length;
		map_occupation = new Index2d[n_tiles];
		for (int idx=0; idx<n_tiles; idx+=1)
			map_occupation[idx] = map_occupation00.add(clone_from[idx]);
		this.SetMapOccupation(map_occupation, map_occupation00);
		structure_type = StructureType.TELEPORTER;
		this.UpdateMap(structure_type);

		// init bbox
		bbox = BoundingBox.of(Coords3d.Index2dToLocation(map_occupation[0], DuoMap.dungeon_origin), 
				Coords3d.Index2dToLocation(map_occupation[map_occupation.length-1].add(1,1), DuoMap.dungeon_origin).add(-1,DuoMap.max_height,-1));
		
		this.is_entrance = is_entrance;
		this.associated_exit = associated_exit; // should be null, except if is_entrance is true
		
		DuoMap.game.teleporters.add(this);
		this.InitialClone(DuoMap.pastebin, 0);
	}
	
	@Override
	public Coords3d GetTemplateOrigin() {
		return teleporter_origin;
	}
	
	public boolean HasCoords3d(Coords3d coords)
	{
		return bbox.contains(coords.x, coords.y, coords.z);
	}
	
	static public void TryTeleport(DuoRunner runner)
	{
		Player p = runner.getDuoPlayer().getPlayer();
		for (Teleporter tp : DuoMap.game.teleporters)
		{
			if (tp.is_entrance)
			{
				Location from = p.getLocation();
				if(tp.HasCoords3d(new Coords3d(from)))
				{
					Location entrance_loc = Coords3d.Index2dToLocation(tp.map_occupation00, DuoMap.game.dungeon_origin);
					Location exit_loc = Coords3d.Index2dToLocation(tp.associated_exit.map_occupation00, DuoMap.game.dungeon_origin);
					Location delta = from.add(- entrance_loc.getX(), - entrance_loc.getY(), - entrance_loc.getZ());
					Location dest = exit_loc.add(delta);
					dest.setYaw(from.getYaw());
					dest.setPitch(from.getPitch());
					p.teleport(dest);
					p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.f, 1);
					break;
				}
			}
		}
	}
}
