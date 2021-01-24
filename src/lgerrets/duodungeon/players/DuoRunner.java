package lgerrets.duodungeon.players;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.game.DuoMap;
import lgerrets.duodungeon.game.Piece;
import lgerrets.duodungeon.utils.Coords3d;
import lgerrets.duodungeon.utils.InvUtils;

public class DuoRunner extends DuoTeammate {
	
	static {
	    Bukkit.getScheduler().scheduleSyncRepeatingTask(DuoDungeonPlugin.getInstance(), new Runnable() {
	        @Override
	        public void run() {
	        	if (DuoMap.game.IsRunning())
	        	{
	        		for (DuoRunner player : DuoTeam.runner_players) {
		        		Coords3d pos = new Coords3d(player.getDuoPlayer().getPlayer().getLocation());
		        		if (player.piece != null && player.piece.HasCoords3d(pos))
		        			return;
		    			for (Piece p : DuoMap.game.pieces)
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
	    }, 0, 1);
	}
	
	public Piece piece;

	public DuoRunner(DuoPlayer player) {
		super(player);
		piece = null;
		type = DuoTeam.TeamType.RUNNER;
		player.getPlayer().setGameMode(GameMode.ADVENTURE);
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
		Player p = player.getPlayer();
		p.teleport(new Location(DuoMap.world, coords.x, coords.y, coords.z, -90, 0));
		super.ResetTeammate();
		p.setSaturation(10);
	}
	
	@Override
	public void Unregister()
	{
		super.Unregister();
		if (this.piece != null)
			this.piece.players.remove(this);
	}
	
	static public void Reset()
	{
		for (DuoRunner runner : DuoTeam.runner_players)
		{
			runner.ResetRunner();
		}
	}
}
