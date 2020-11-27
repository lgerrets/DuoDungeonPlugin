package lgerrets.duodungeon.game;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;

import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.utils.Coords3d;

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
		player.getPlayer().setGameMode(GameMode.ADVENTURE);
		piece = null;
		
        Bukkit.getScheduler().scheduleSyncRepeatingTask(DuoDungeonPlugin.getInstance(), new TrackActivePiece(this), 0, 1);
	}
}
