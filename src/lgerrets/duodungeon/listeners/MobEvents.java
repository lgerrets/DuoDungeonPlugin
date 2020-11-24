package lgerrets.duodungeon.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.plugin.Plugin;

public class MobEvents {
	
	static public void register(final Plugin p)
	{
		MobListener l = new MobListener();
		Bukkit.getPluginManager().registerEvents(l, p);
	}
	
	static private class MobListener implements Listener {
		MobListener()
		{
			
		}
		
		@EventHandler
		public void onBurn(EntityCombustEvent event){
			/* Instead put a helmet (even a button, which is quite invisible)
			if (event.getEntity().getType().equals(EntityType.ZOMBIE))
				event.setCancelled(true);
			*/
		}
	}
}