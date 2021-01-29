package lgerrets.duodungeon.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import lgerrets.duodungeon.ConfigManager;
import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.game.DuoMap;
import lgerrets.duodungeon.game.Teleporter;
import lgerrets.duodungeon.players.DuoBuilder;
import lgerrets.duodungeon.players.DuoPlayer;
import lgerrets.duodungeon.players.DuoRunner;
import lgerrets.duodungeon.players.DuoTeam;
import lgerrets.duodungeon.utils.Cooldown;
import lgerrets.duodungeon.utils.Index2d;
import lgerrets.duodungeon.utils.Index2d.Direction;
import lgerrets.duodungeon.utils.InvUtils;

public class PlayerEvents implements Listener {
	
	private Cooldown builder_act_cooldown;
	private Cooldown builder_move_cooldown;
	//private Cooldown builder_resetpos_cooldown;
	static private Set<Material> transparents = new HashSet<Material>();
	static {
		transparents.add(Material.AIR);
		transparents.add(Material.BARRIER);
	}
	
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
	    		if(items.getType() == Material.STONE_BUTTON)
	    		{
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
	    		else if(items.getType() == Material.NETHER_STAR)
	    		{
	    			if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)
	    			{
	    				Player player = p.getPlayer();
	    				Block targetBlock = player.getTargetBlock(transparents, 200);
	    				if (targetBlock.getType() != Material.AIR) // we are not aiming at void or out of range
	    					DuoBuilder.GetBuilder(e.getPlayer()).UseThunder(targetBlock.getLocation());
	    			}
	    			else
	    				return;
	    		}
	    		else if(items.getType() == Material.FLINT)
	    		{
	    			if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)
	    			{
	    				Player player = p.getPlayer();
	    				Block targetBlock = player.getTargetBlock(transparents, 200);
	    				if (targetBlock.getType() != Material.AIR) // we are not aiming at void or out of range
	    					DuoBuilder.GetBuilder(e.getPlayer()).TryPlaceTeleporter(targetBlock.getLocation());
	    			}
	    			else
	    				return;
	    		}
	    		else
	    			return;
	    	}
    	}
    }
    
    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
    	if (DuoMap.game.IsRunning())
    	{
    		Player player = e.getPlayer();
	    	DuoPlayer p = DuoPlayer.getPlayer(player.getUniqueId());
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
	    	else if (p.getTeam().teamType == DuoTeam.TeamType.RUNNER)
	    	{
	    		if (e.isSneaking());
	    		{
	    			Teleporter.TryTeleport(DuoRunner.GetRunner(player));
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
    public void onBlockPlaced(BlockPlaceEvent e) {
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
    public void onItemPickedFromChest(InventoryClickEvent e) {
    	if (DuoMap.game.IsRunning())
    	{
	        Inventory inv = e.getClickedInventory();
	        if (inv == null)
	            return;
	        //if (e.getCurrentItem() == null || e.getCurrentItem().getAmount() == 0)
	        //	return;
	        DuoDungeonPlugin.logg(e.getCursor());
	        InventoryHolder source = inv.getHolder();
	        if (source instanceof Chest)
	        {
	        	if(e.getCursor().getType() != Material.AIR)
	        	{
	        		e.setCancelled(true); // cancelling because we don't players to put items into chests
	        		return;
	        	}
	        	else { // we deal with the fact that the player is taking an item. TODO we would not catch a player taking items by double clicking a stackable item in his own inventory!!!
		            Chest chest = (Chest) source;
		            //BlockData data = chest.getBlockData();
		            DuoDungeonPlugin.logg(source.toString());
		            ItemStack taking = e.getCurrentItem();
		            chest.getBlockInventory().clear(); // we clear the chest's inventory
		            Player p = (Player) e.getWhoClicked();
		            InvUtils.addItems(p, taking); // ... but still give the one item the player was trying to take
		            // chest.getBlockInventory().setItem(e.getSlot(), taking);
		            //setContents new ItemStack[1]
		            /*for (int idx=0; idx<inv.getSize(); idx+=1)
		                inv.setItem(idx,  null);*/
		            //chest.update();
		            DuoMap.game.world.getBlockAt(chest.getLocation()).setType(Material.COBBLESTONE);
	        	}
	        }
	        else if(source instanceof Player)
	        {
	        	Inventory main_inv = e.getInventory();
	        	InventoryHolder dest = main_inv.getHolder();
	        	if (dest instanceof Chest)
	        	{
	        		DuoDungeonPlugin.logg("player " + source.toString());
	        		DuoDungeonPlugin.logg(e.getAction());
	            	if(e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY)
	            	{
	            		e.setCancelled(true); // cancelling because we don't players to put items into chests
	            		return;
	            	}
	        	}
	        }
    	}
    }
}

