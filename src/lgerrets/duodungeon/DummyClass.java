package lgerrets.duodungeon;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class DummyClass implements Listener {
	
	public DummyClass()
	{
		
	}

    @EventHandler(priority=EventPriority.HIGHEST)
    public void woodBreak(BlockBreakEvent event)
    {
    	Material mat = event.getBlock().getType();
    	if (mat == Material.DIRT && ConfigManager.DDConfig.getInt("verbose") > 0)
    	{
    		// Bukkit.broadcastMessage("Broadcast: A dirt was broken...");
    		for (Player p : Bukkit.getOnlinePlayers())
    			p.sendMessage("A dirt was broken!");
    	}
    }
}
