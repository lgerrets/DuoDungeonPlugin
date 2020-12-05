package lgerrets.duodungeon.players;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.game.DuoMap;
import lgerrets.duodungeon.game.Piece;
import lgerrets.duodungeon.utils.Coords3d;
import lgerrets.duodungeon.utils.InvUtils;

public class DuoRunner extends DuoTeammate {
	
	
	private class TrackActivePiece implements Runnable {
		DuoRunner player;
		public TrackActivePiece(DuoRunner player)
		{
			super();
			this.player = player;
		}
		
        @Override
        public void run() {
        	if (DuoMap.game.IsRunning())
        	{
        		Coords3d pos = new Coords3d(player.getDuoPlayer().getPlayer().getLocation());
        		if (player.piece != null && player.piece.HasCoords3d(pos))
        			return;
    			for (Piece p : DuoMap.pieces)
    			{
    				if (p.HasCoords3d(pos))
    				{
						if (player.piece != null)
							player.piece.players.remove(player);
						player.piece = p;
						p.players.add(player);
						break;
    				}
    			}
        	}
        }
	}
	
	public Piece piece;
	private int taskId;

	public DuoRunner(DuoPlayer player) {
		super(player);
		piece = null;
		type = DuoTeam.TeamType.RUNNER;
		player.getPlayer().setGameMode(GameMode.ADVENTURE);
		taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(DuoDungeonPlugin.getInstance(), new TrackActivePiece(this), 0, 1);
		if(DuoMap.game.IsRunning())
			this.ResetRunner();
	}
	
	public void ResetRunner()
	{
		InvUtils.ChangeItemNb(getDuoPlayer().getPlayer(), -999, Material.TNT);
		InvUtils.ChangeItemNb(getDuoPlayer().getPlayer(), -999, Material.GOLD_NUGGET);
		InvUtils.ChangeItemNb(getDuoPlayer().getPlayer(), -999, Material.ARROW);
		InvUtils.ChangeItemNb(getDuoPlayer().getPlayer(), -999, Material.BREAD);
		Coords3d coords = Coords3d.FromWaypoint("runner");
		player.getPlayer().teleport(new Location(DuoMap.world, coords.x, coords.y, coords.z, -90, 0));
		player.getPlayer().setHealth(player.getPlayer().getMaxHealth());
	}
	
	@Override
	public void Unregister()
	{
		super.Unregister();
		if (this.piece != null)
			this.piece.players.remove(this);
		Bukkit.getScheduler().cancelTask(taskId);
	}
	
	static public void Reset()
	{
		for (DuoRunner runner : DuoTeam.runner_players)
		{
			runner.ResetRunner();
		}
	}
}
