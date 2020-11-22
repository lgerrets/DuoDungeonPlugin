package lgerrets.duodungeon.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lgerrets.duodungeon.ConfigManager;
import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.enchantments.CustomEnchants;
import lgerrets.duodungeon.enchantments.EnchantmentWrapper;
import lgerrets.duodungeon.utils.MyMath;
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
	
	private enum DropType {
		MELEE, BOW, HELMET, CHESTPLATE, LEGGINGS, BOOTS, 
		SUPPLIES, GOLD, 
	};
	
	// DropType => Material => score
	static private EnumMap<DropType, EnumMap<Material, Integer>> drop_base_score = new EnumMap<DropType, EnumMap<Material, Integer>>(DropType.class);
	static {
		EnumMap<Material, Integer> temp_scores = new EnumMap<Material, Integer>(Material.class);
		temp_scores.put(Material.STONE_PICKAXE, 1);
		temp_scores.put(Material.WOODEN_SWORD, 2);
		temp_scores.put(Material.STONE_SWORD, 3);
		temp_scores.put(Material.IRON_SWORD, 4);
		temp_scores.put(Material.DIAMOND_SWORD, 5);
		drop_base_score.put(DropType.MELEE, temp_scores);
		
		temp_scores = new EnumMap<Material, Integer>(Material.class);
		temp_scores.put(Material.BOW, 3);
		drop_base_score.put(DropType.BOW, temp_scores);
		
		temp_scores = new EnumMap<Material, Integer>(Material.class);
		temp_scores.put(Material.LEATHER_HELMET, 1);
		temp_scores.put(Material.IRON_HELMET, 3);
		temp_scores.put(Material.DIAMOND_HELMET, 5);
		drop_base_score.put(DropType.HELMET, temp_scores);
		
		temp_scores = new EnumMap<Material, Integer>(Material.class);
		temp_scores.put(Material.LEATHER_BOOTS, 1);
		temp_scores.put(Material.IRON_BOOTS, 3);
		temp_scores.put(Material.DIAMOND_BOOTS, 5);
		drop_base_score.put(DropType.BOOTS, temp_scores);
		
		temp_scores = new EnumMap<Material, Integer>(Material.class);
		temp_scores.put(Material.LEATHER_CHESTPLATE, 2);
		temp_scores.put(Material.CHAINMAIL_CHESTPLATE, 4);
		temp_scores.put(Material.IRON_CHESTPLATE, 5);
		temp_scores.put(Material.DIAMOND_CHESTPLATE, 7);
		drop_base_score.put(DropType.CHESTPLATE, temp_scores);
		
		temp_scores = new EnumMap<Material, Integer>(Material.class);
		temp_scores.put(Material.LEATHER_LEGGINGS, 2);
		temp_scores.put(Material.CHAINMAIL_LEGGINGS, 4);
		temp_scores.put(Material.IRON_LEGGINGS, 5);
		temp_scores.put(Material.DIAMOND_LEGGINGS, 7);
		drop_base_score.put(DropType.LEGGINGS, temp_scores);
	}
	
	static private class DropData {
		private Enchantment ench;
		private int level;
		private int drop_score;
		DropData(Enchantment ench, int level, int drop_score) {
			this.ench = ench;
			this.level = level;
			this.drop_score = drop_score;
		}
	}
	
	// DropType => (Enchantment, Level, Score) 
	static private EnumMap<DropType, Set<DropData>> enchantments_score = new EnumMap<DropType, Set<DropData>>(DropType.class);
	static {
		Set<DropData> temp_enchantments = new HashSet<DropData>();
		temp_enchantments.add(new DropData(Enchantment.DAMAGE_ALL, 1, 1));
		temp_enchantments.add(new DropData(Enchantment.DAMAGE_ALL, 3, 2));
		temp_enchantments.add(new DropData(CustomEnchants.PRINT, 1, 1));
		enchantments_score.put(DropType.MELEE, temp_enchantments);
		
		temp_enchantments = new HashSet<DropData>();
		temp_enchantments.add(new DropData(Enchantment.ARROW_FIRE, 1, 2));
		temp_enchantments.add(new DropData(Enchantment.ARROW_DAMAGE, 1, 1));
		temp_enchantments.add(new DropData(Enchantment.ARROW_DAMAGE, 2, 3));
		temp_enchantments.add(new DropData(Enchantment.ARROW_DAMAGE, 3, 5));
		enchantments_score.put(DropType.BOW, temp_enchantments);
		
		temp_enchantments = new HashSet<DropData>();
		temp_enchantments.add(new DropData(Enchantment.PROTECTION_ENVIRONMENTAL, 1, 1));
		enchantments_score.put(DropType.HELMET, temp_enchantments);
		
		temp_enchantments = new HashSet<DropData>();
		temp_enchantments.add(new DropData(Enchantment.PROTECTION_ENVIRONMENTAL, 1, 1));
		enchantments_score.put(DropType.BOOTS, temp_enchantments);
		
		temp_enchantments = new HashSet<DropData>();
		temp_enchantments.add(new DropData(Enchantment.PROTECTION_ENVIRONMENTAL, 1, 1));
		enchantments_score.put(DropType.CHESTPLATE, temp_enchantments);
		
		temp_enchantments = new HashSet<DropData>();
		temp_enchantments.add(new DropData(Enchantment.PROTECTION_ENVIRONMENTAL, 1, 1));
		enchantments_score.put(DropType.LEGGINGS, temp_enchantments);
	}
	
	static public ItemStack DrawDrop(ChestRarity rarity, int tier)
	{
		int max_score = tier*2;
		int effective_score;
		switch(rarity) {
		case COMMON:
			max_score += 1 + MyMath.RandomUInt(3);
			break;
		case RARE:
			max_score += 4 + MyMath.RandomUInt(2);
			break;
		case EPIC:
			max_score += 6;
			break;
		case LEGENDARY:
			max_score += 7;
			break;
		}
		DuoDungeonPlugin.logg("Max drop score is " + String.valueOf(max_score));
		if (max_score < 1)
		{
			DuoDungeonPlugin.logg("Hard setting max_score to 1!");
			max_score = 1; // max_score should always be >= 1
		}
		
		// draw DropType
		int min_possible_score = 999;
		DropType dropType = null;
		while (min_possible_score > max_score)
		{
			dropType = MyMath.RandomChoiceUniform(drop_base_score.keySet());
			min_possible_score = Collections.min(drop_base_score.get(dropType).values());
		}
		
		// draw item
		Material mat = null;
		ItemStack item = null;
		effective_score = 999;
		while (effective_score > max_score)
		{
			mat = MyMath.RandomChoiceUniform(drop_base_score.get(dropType).keySet());
			effective_score = drop_base_score.get(dropType).get(mat);
			item = new ItemStack(mat);
		}
		DuoDungeonPlugin.logg(item.toString());
		
		// draw enchantments
		int max_tries = 20;
		int n_tries = 0;
		while (n_tries < max_tries && effective_score < max_score)
		{
			n_tries += 1;
			DropData data = MyMath.RandomChoiceUniform(enchantments_score.get(dropType));
			
			// reject because this would make a too high item
			if (data.drop_score + effective_score > max_score)
				continue;
			// reject because enchantment and item are incompatible
			if (data.ench.equals(Enchantment.DAMAGE_ALL) && mat.equals(Material.STONE_PICKAXE))
				continue;
			// reject because the item already has this enchantment
			if (item.containsEnchantment(data.ench))
				continue;
			// ok, we add this enchantment
			if (! (data.ench instanceof EnchantmentWrapper)) {
				item.addEnchantment(data.ench, data.level);
			}
			else {
				// this enchantment is a custom one, 
				item.addUnsafeEnchantment(data.ench, data.level);
				ItemMeta meta = item.getItemMeta();
				List<String> lore = new ArrayList<String>();
				lore.add(ChatColor.GRAY + data.ench.getName() + " " + CustomEnchants.levelToString(data.level));
				meta.setLore(lore);
				item.setItemMeta(meta);
			}
			effective_score += data.drop_score;
		}
		DuoDungeonPlugin.logg(rarity.toString() + " " + String.valueOf(effective_score) + "/" + String.valueOf(max_score));
		if (effective_score != max_score)
			DuoDungeonPlugin.logg("The drop does not have the full potential score");
		return item;
	}
}
