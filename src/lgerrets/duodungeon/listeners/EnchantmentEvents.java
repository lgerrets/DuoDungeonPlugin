package lgerrets.duodungeon.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.enchantments.CustomEnchants;

public class EnchantmentEvents {
	
	static public void register(final Plugin p)
	{
		EnchantmentsListener l = new EnchantmentsListener();
		Bukkit.getPluginManager().registerEvents(l, p);
	}
	
	static private class EnchantmentsListener implements Listener {
		EnchantmentsListener()
		{
			
		}
		
		@EventHandler
		public void onBlockBreak(BlockBreakEvent event)
		{
			ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
			if(item == null)
				return;
			if (!item.hasItemMeta())
				return;
			if (!item.getItemMeta().hasEnchant(CustomEnchants.PRINT))
				return;
			// if (event.getPlayer().getFirstEmpty() == -1)
			DuoDungeonPlugin.logg("Triggered enchantment");
		}
	}
	

}
