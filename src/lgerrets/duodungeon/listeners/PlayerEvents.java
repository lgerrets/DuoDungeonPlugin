package lgerrets.duodungeon.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.game.DuoMap;
import lgerrets.duodungeon.game.DuoPlayer;
import lgerrets.duodungeon.game.DuoTeam;
import lgerrets.duodungeon.utils.Index2d;
import lgerrets.duodungeon.utils.Index2d.Direction;

public class PlayerEvents implements Listener {
    @EventHandler
    public void onMove(org.bukkit.event.player.PlayerMoveEvent e) {
    	if (DuoMap.game.IsRunning())
    	{
	    	DuoPlayer p = DuoPlayer.getPlayer(e.getPlayer().getUniqueId());
	    	if (p == null) {
	    		DuoDungeonPlugin.logg("Player is null");
	    		return;
	    	}
	        if (p.getTeam().teamType == DuoTeam.TeamType.BUILDER && (e.getFrom().getZ() != e.getTo().getZ() || e.getFrom().getX() != e.getTo().getX())) {
	            // move the unplaced piece
	        	double dx = e.getTo().getX() - e.getFrom().getX();
	        	double dz = e.getTo().getZ() - e.getFrom().getZ();
	        	Direction dir = Index2d.DeltasToDirection(dx, dz);
	        	DuoMap.game.TryMovePiece(dir);
	            e.setCancelled(true);
	        }
    	}
    }
    
    @EventHandler
    public void onLeftClick(PlayerInteractEvent e) {
    	if (DuoMap.game.IsRunning())
    	{
	    	DuoPlayer p = DuoPlayer.getPlayer(e.getPlayer().getUniqueId());
	    	if (p == null) {
	    		DuoDungeonPlugin.logg("Player is null");
	    		return;
	    	}
	    	if (p.getTeam().teamType == DuoTeam.TeamType.BUILDER)
	    	{
	    		//DuoMap.game.SpawnNewPiece();
	    		DuoMap.game.TryRotatePiece(true);
	    	}
    	}
    }
}
