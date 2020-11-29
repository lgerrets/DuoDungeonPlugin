package lgerrets.duodungeon.game;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.utils.Coords3d;
import lgerrets.duodungeon.utils.InvUtils;

public class DuoRunner extends DuoTeammate {
	
	
	private static class TrackActivePiece implements Runnable {
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
    			for (Piece p : DuoMap.pieces)
    			{
    				if (p.HasCoords3d(new Coords3d(player.getDuoPlayer().getPlayer().getLocation())))
    				{
    					if (player.piece != p)
    					{
    						if (player.piece != null)
    							player.piece.players.remove(this);
    						player.piece = p;
    						p.players.add(player);
    						break;
    					}
    				}
    			}
        	}
        }
	}
	
	public Piece piece;

	public DuoRunner(DuoPlayer player) {
		super(player);
		piece = null;
		
        Bukkit.getScheduler().scheduleSyncRepeatingTask(DuoDungeonPlugin.getInstance(), new TrackActivePiece(this), 0, 1);
	}
	
	public void ResetRunner()
	{
		InvUtils.ChangeItemNb(getDuoPlayer().getPlayer(), -999, Material.TNT);
		InvUtils.ChangeItemNb(getDuoPlayer().getPlayer(), -999, Material.GOLD_NUGGET);
		InvUtils.ChangeItemNb(getDuoPlayer().getPlayer(), -999, Material.ARROW);
		InvUtils.ChangeItemNb(getDuoPlayer().getPlayer(), -999, Material.BREAD);
	}
	
	static public void Reset()
	{
		for (DuoRunner runner : DuoTeam.runner_players)
		{
			runner.ResetRunner();
		}
	}
}
