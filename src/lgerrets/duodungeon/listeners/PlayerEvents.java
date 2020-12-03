package lgerrets.duodungeon.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import lgerrets.duodungeon.ConfigManager;
import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.game.DuoMap;
import lgerrets.duodungeon.game.DuoPlayer;
import lgerrets.duodungeon.game.DuoTeam;
import lgerrets.duodungeon.utils.Cooldown;
import lgerrets.duodungeon.utils.Index2d;
import lgerrets.duodungeon.utils.Index2d.Direction;

public class PlayerEvents implements Listener {
	
	private Cooldown builder_act_cooldown;
	private Cooldown builder_move_cooldown;
	//private Cooldown builder_resetpos_cooldown;
	
	public PlayerEvents()
	{
		super();
		builder_move_cooldown = new Cooldown(ConfigManager.DDConfig.getConfigurationSection("Controls").getInt("builder_move_cooldown"), false);
		builder_act_cooldown = new Cooldown(ConfigManager.DDConfig.getConfigurationSection("Controls").getInt("builder_act_cooldown"), false);
		//builder_resetpos_cooldown = new Cooldown(ConfigManager.DDConfig.getConfigurationSection("Controls").getInt("builder_resetpos_cooldown"), false);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(DuoDungeonPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
            	if (DuoMap.game.IsRunning())
            	{
            		builder_act_cooldown.tick();
            		builder_move_cooldown.tick();
            		//builder_resetpos_cooldown.tick();
            	}
            }
        }, 0, 1);
	}
	
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
	    		ItemStack items = p.getPlayer().getItemInHand();
	    		if (items == null)
	    			return;
	    		if(items.getType() != Material.STONE_BUTTON)
	    			return;
	        	e.setCancelled(true);
	    		if (builder_move_cooldown.isReady())
	    		{
	    			// move the unplaced piece
		        	double dx = e.getTo().getX() - e.getFrom().getX();
		        	double dz = e.getTo().getZ() - e.getFrom().getZ();
		        	Direction dir = Index2d.DeltasToDirection(dx, dz);
		        	DuoMap.game.TryMoveStruct(dir);
		            builder_move_cooldown.reset();
	    		}
	        }
    	}
    }
    
    @EventHandler
    public void onClick(PlayerInteractEvent e) {
    	if (DuoMap.game.IsRunning())
    	{
	    	DuoPlayer p = DuoPlayer.getPlayer(e.getPlayer().getUniqueId());
	    	if (p == null) {
	    		DuoDungeonPlugin.logg("Player is null");
	    		return;
	    	}
	    	if (p.getTeam().teamType == DuoTeam.TeamType.BUILDER)
	    	{
	    		ItemStack items = p.getPlayer().getItemInHand();
	    		if (items == null)
	    			return;
	    		if(items.getType() != Material.STONE_BUTTON)
	    			return;
	    		if (builder_act_cooldown.isReady())
	    		{
	    			if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)
	    				DuoMap.game.SpawnNewStruct();
	    			else if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
		    			DuoMap.game.TryRotatePiece(true); // true stands for trigo orientation
		    		else
		    			return;
		    		builder_act_cooldown.reset();
	    		}
	    	}
    	}
    }
    
    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
    	if (DuoMap.game.IsRunning())
    	{
	    	DuoPlayer p = DuoPlayer.getPlayer(e.getPlayer().getUniqueId());
	    	if (p == null) {
	    		DuoDungeonPlugin.logg("Player is null");
	    		return;
	    	}
	    	if (p.getTeam().teamType == DuoTeam.TeamType.BUILDER)
	    	{
	    		if (e.isSneaking())
	    		{
	    			DuoMap.game.EnableNextIsBomb();
	    			e.setCancelled(true);
	    		}
	    	}
    	}
    }
    
    @EventHandler
    public void onBlockDestroy(BlockBreakEvent e) {
    	Player p = e.getPlayer();
    	if (p == null)
    		return;
    	DuoPlayer player = DuoPlayer.getPlayer(p.getPlayer().getUniqueId());
    	if (player.getTeam().teamType == DuoTeam.TeamType.BUILDER)
    	{
    		e.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onBlocKPlaced(BlockPlaceEvent e) {
    	Player p = e.getPlayer();
    	if (p == null)
    		return;
    	DuoPlayer player = DuoPlayer.getPlayer(p.getPlayer().getUniqueId());
    	if (player.getTeam().teamType == DuoTeam.TeamType.BUILDER)
    	{
    		e.setCancelled(true);
    	}
    }
}
