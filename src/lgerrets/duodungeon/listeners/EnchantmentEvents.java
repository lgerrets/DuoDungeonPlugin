package lgerrets.duodungeon.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.enchantments.CustomEnchants;
import lgerrets.duodungeon.utils.MyMath;

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
		
		@EventHandler
		public void onKillMob(EntityDamageByEntityEvent event)
		{
			Entity damager_ent = event.getDamager();
			Entity damaged_ent = event.getEntity();
			if (!(damager_ent instanceof LivingEntity))
				return;
			if (!(damaged_ent instanceof LivingEntity))
				return;
			double damage = event.getFinalDamage();
			LivingEntity damager = (LivingEntity) damager_ent;
			LivingEntity damaged = (LivingEntity) damaged_ent;
			if (damager instanceof Player)
			{
				Player player = (Player) damager;
				ItemStack item = player.getInventory().getItemInMainHand();
				if(item != null && item.hasItemMeta() && item.getItemMeta().hasEnchant(CustomEnchants.LIFESTEAL) &&
						damaged.getHealth() - damage <= 0)
				{
					int lifesteal_lvl = item.getItemMeta().getEnchantLevel(CustomEnchants.LIFESTEAL);
					player.setHealth(MyMath.Min(player.getMaxHealth(), player.getHealth() + lifesteal_lvl));
				}
			}

			Vector vel = damaged.getVelocity();
			Bukkit.getScheduler().scheduleSyncDelayedTask(DuoDungeonPlugin.getInstance(), new Runnable()
            {
				public void run() {
					// if (player.isValid())
					damaged.setVelocity(vel);
				}
            }
            , 1L);
		}
	}
	

}
