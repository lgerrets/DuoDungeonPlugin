package lgerrets.duodungeon.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.game.DungeonMap;
import lgerrets.duodungeon.game.DuoPlayer;
import lgerrets.duodungeon.game.DuoTeam;
import lgerrets.duodungeon.utils.Index2d;
import lgerrets.duodungeon.utils.Index2d.Direction;

public class PlayerEvents implements Listener {
    @EventHandler
    public void onMove(org.bukkit.event.player.PlayerMoveEvent e) {
    	if (DungeonMap.game.IsRunning())
    	{
	    	DuoPlayer p = DuoPlayer.getPlayer(e.getPlayer().getUniqueId());
	        if (p.getTeam().teamType == DuoTeam.TeamType.BUILDER && (e.getFrom().getZ() != e.getTo().getZ() || e.getFrom().getX() != e.getTo().getX())) {
	            // move the unplaced piece
	        	double dx = e.getTo().getX() - e.getFrom().getX();
	        	double dz = e.getTo().getZ() - e.getFrom().getZ();
	        	Direction dir = Index2d.DeltasToDirection(dx, dz);
	        	DungeonMap.game.TryMovePiece(dir);
	            e.setCancelled(true);
	        }
    	}
    }
}
