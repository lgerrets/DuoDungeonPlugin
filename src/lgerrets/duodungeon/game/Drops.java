package lgerrets.duodungeon.game;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lgerrets.duodungeon.ConfigManager;
import lgerrets.duodungeon.enchantments.CustomEnchants;
import net.md_5.bungee.api.ChatColor;

public class Drops {
	
	public enum ChestRarity { COMMON, RARE, EPIC, LEGENDARY };
	public static EnumMap<ChestRarity, Double> rarity_drops = new EnumMap<ChestRarity, Double>(ChestRarity.class);
	static {
		ConfigurationSection game_config = ConfigManager.DDConfig.getConfigurationSection("Game");
		rarity_drops.put(ChestRarity.RARE, game_config.getDouble("rare_drops"));
		rarity_drops.put(ChestRarity.EPIC, game_config.getDouble("epic_drops"));
		rarity_drops.put(ChestRarity.LEGENDARY, game_config.getDouble("legendary_drops"));
		rarity_drops.put(ChestRarity.COMMON, 1.0 - rarity_drops.get(ChestRarity.RARE)
				- rarity_drops.get(ChestRarity.EPIC) - rarity_drops.get(ChestRarity.LEGENDARY));
	}
	
	static public ItemStack DrawDrop(ChestRarity rarity, int tier)
	{
		ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
		item.addUnsafeEnchantment(CustomEnchants.PRINT, 1);
		
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "Print I");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
}
