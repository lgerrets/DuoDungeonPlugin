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
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

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
		SUPPLIES, MONEY, 
	};
	
	// DropType => Material => score
	static private EnumMap<DropType, EnumMap<Material, Double>> drop_base_score = new EnumMap<DropType, EnumMap<Material, Double>>(DropType.class);
	static {
		EnumMap<Material, Double> temp_scores = new EnumMap<Material, Double>(Material.class);
		temp_scores.put(Material.STONE_PICKAXE,  1.0);
		temp_scores.put(Material.WOODEN_SWORD, 2.0);
		temp_scores.put(Material.STONE_SWORD, 3.0);
		temp_scores.put(Material.IRON_SWORD, 4.0);
		temp_scores.put(Material.DIAMOND_SWORD, 5.0);
		drop_base_score.put(DropType.MELEE, temp_scores);
		
		temp_scores = new EnumMap<Material, Double>(Material.class);
		temp_scores.put(Material.BOW, 4.0);
		drop_base_score.put(DropType.BOW, temp_scores);
		
		temp_scores = new EnumMap<Material, Double>(Material.class);
		temp_scores.put(Material.LEATHER_HELMET, 1.0);
		temp_scores.put(Material.IRON_HELMET, 3.0);
		temp_scores.put(Material.DIAMOND_HELMET, 5.0);
		drop_base_score.put(DropType.HELMET, temp_scores);
		
		temp_scores = new EnumMap<Material, Double>(Material.class);
		temp_scores.put(Material.LEATHER_BOOTS, 1.0);
		temp_scores.put(Material.IRON_BOOTS, 3.0);
		temp_scores.put(Material.DIAMOND_BOOTS, 5.0);
		drop_base_score.put(DropType.BOOTS, temp_scores);
		
		temp_scores = new EnumMap<Material, Double>(Material.class);
		temp_scores.put(Material.LEATHER_CHESTPLATE, 2.0);
		temp_scores.put(Material.CHAINMAIL_CHESTPLATE, 4.0);
		temp_scores.put(Material.IRON_CHESTPLATE, 5.0);
		temp_scores.put(Material.DIAMOND_CHESTPLATE, 7.0);
		drop_base_score.put(DropType.CHESTPLATE, temp_scores);
		
		temp_scores = new EnumMap<Material, Double>(Material.class);
		temp_scores.put(Material.LEATHER_LEGGINGS, 2.0);
		temp_scores.put(Material.CHAINMAIL_LEGGINGS, 4.0);
		temp_scores.put(Material.IRON_LEGGINGS, 5.0);
		temp_scores.put(Material.DIAMOND_LEGGINGS, 7.0);
		drop_base_score.put(DropType.LEGGINGS, temp_scores);
		
		// scores for **supplies** are special, because the amount of supplies must depend on score, but also some supplies should be unlocked only later in the game
		// as for equipments, a supply is unlocked if score>=drop_base_score
		// /!\ regarding the quantity: quantity = (score - drop_base_score/2)/supply_scores
		// also, supplies are obtainable only from COMMON chests
		temp_scores = new EnumMap<Material, Double>(Material.class);
		//temp_scores.put(Material.BREAD, 0.5);
		//temp_scores.put(Material.ARROW, 3.0);
		//temp_scores.put(Material.TNT, 2.0);
		temp_scores.put(Material.SPLASH_POTION, 3.0);
		drop_base_score.put(DropType.SUPPLIES, temp_scores);
		
		// scores for **money** are special, because the amount of money must depend on score
		// /!\ regarding the quantity: quantity = (score - drop_base_score)/supply_scores
		// also, supplies are obtainable only from COMMON chests
		// (in the future, more moneys could come in, and unlocked only later in the game, as with supplies)
		temp_scores = new EnumMap<Material, Double>(Material.class);
		temp_scores.put(Material.GOLD_NUGGET, -1.0);
		drop_base_score.put(DropType.MONEY, temp_scores);
	}
	
	static private class CountableDropData {
		private double per_unit_score;
		CountableDropData(double per_unit_score) {
			this.per_unit_score = per_unit_score;
		}
	}
	
	// regroups SUPPLY and MONEY
	static private EnumMap<Material, CountableDropData> countable_scores = new EnumMap<Material, CountableDropData>(Material.class);
	static {
		// supplies
		countable_scores.put(Material.BREAD, new CountableDropData(0.3));
		countable_scores.put(Material.ARROW, new CountableDropData(0.15));
		countable_scores.put(Material.TNT, new CountableDropData(0.5));
		countable_scores.put(Material.SPLASH_POTION, new CountableDropData(1.0));
		// money
		countable_scores.put(Material.GOLD_NUGGET, new CountableDropData(0.2));
	}
	
	static private class EnchantedDropData {
		private Enchantment ench;
		private int level;
		private double drop_score;
		EnchantedDropData(Enchantment ench, int level, double drop_score) {
			this.ench = ench;
			this.level = level;
			this.drop_score = drop_score;
		}
	}
	
	// DropType => (Enchantment, Level, Score) 
	static private EnumMap<DropType, Set<EnchantedDropData>> enchantments_score = new EnumMap<DropType, Set<EnchantedDropData>>(DropType.class);
	static {
		Set<EnchantedDropData> temp_enchantments = new HashSet<EnchantedDropData>();
		temp_enchantments.add(new EnchantedDropData(Enchantment.DAMAGE_ALL, 1, 1));
		temp_enchantments.add(new EnchantedDropData(Enchantment.DAMAGE_ALL, 3, 2));
		temp_enchantments.add(new EnchantedDropData(Enchantment.DAMAGE_ALL, 5, 4));
		temp_enchantments.add(new EnchantedDropData(CustomEnchants.LIFESTEAL, 1, 2));
		temp_enchantments.add(new EnchantedDropData(CustomEnchants.LIFESTEAL, 5, 5));
		enchantments_score.put(DropType.MELEE, temp_enchantments);
		
		temp_enchantments = new HashSet<EnchantedDropData>();
		temp_enchantments.add(new EnchantedDropData(Enchantment.ARROW_FIRE, 1, 2));
		temp_enchantments.add(new EnchantedDropData(Enchantment.ARROW_DAMAGE, 1, 1));
		temp_enchantments.add(new EnchantedDropData(Enchantment.ARROW_DAMAGE, 3, 3));
		temp_enchantments.add(new EnchantedDropData(Enchantment.ARROW_DAMAGE, 5, 5));
		enchantments_score.put(DropType.BOW, temp_enchantments);
		
		temp_enchantments = new HashSet<EnchantedDropData>();
		temp_enchantments.add(new EnchantedDropData(Enchantment.PROTECTION_ENVIRONMENTAL, 1, 1));
		temp_enchantments.add(new EnchantedDropData(CustomEnchants.GATHERER, 1, 2));
		temp_enchantments.add(new EnchantedDropData(CustomEnchants.GATHERER, 2, 3));
		enchantments_score.put(DropType.HELMET, temp_enchantments);
		
		temp_enchantments = new HashSet<EnchantedDropData>();
		temp_enchantments.add(new EnchantedDropData(Enchantment.PROTECTION_ENVIRONMENTAL, 1, 1));
		enchantments_score.put(DropType.BOOTS, temp_enchantments);
		
		temp_enchantments = new HashSet<EnchantedDropData>();
		temp_enchantments.add(new EnchantedDropData(Enchantment.PROTECTION_ENVIRONMENTAL, 1, 1));
		temp_enchantments.add(new EnchantedDropData(CustomEnchants.GATHERER, 1, 2));
		enchantments_score.put(DropType.CHESTPLATE, temp_enchantments);
		
		temp_enchantments = new HashSet<EnchantedDropData>();
		temp_enchantments.add(new EnchantedDropData(Enchantment.PROTECTION_ENVIRONMENTAL, 1, 1));
		enchantments_score.put(DropType.LEGGINGS, temp_enchantments);
	}
	/*CustomEnchants.GATHERER
	CustomEnchants.LIFESTEAL
	CustomEnchants.ROBUST
	CustomEnchants.SPEED
	CustomEnchants.JUMPY
	CustomEnchants.STRENGTH*/
	
	static public ItemStack DrawDrop(ChestRarity rarity, int tier)
	{
		double max_score = tier*2;
		double effective_score;
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
		double min_possible_score = 999;
		DropType dropType = null;
		while (min_possible_score > max_score) // eg. do not draw from the bow bucket if max_score is too low
		{
			dropType = MyMath.RandomChoiceUniform(drop_base_score.keySet());
			min_possible_score = Collections.min(drop_base_score.get(dropType).values());
			if (dropType == DropType.MONEY || dropType == DropType.SUPPLIES)
			{
				if (rarity != ChestRarity.COMMON) // we do not want supplies nor money in RARE+ chests
					min_possible_score = 999;
			}
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
		
		// apply gatherer effect (AFTER checking conditions of unlocked items)
		if (dropType == DropType.MONEY || dropType == DropType.SUPPLIES)
		{
			int gatherer_bonus = DuoTeam.HasEnchant(DuoTeam.TeamType.RUNNER, CustomEnchants.GATHERER);
			max_score += gatherer_bonus;
		}		
		
		// draw enchantments
		switch (dropType) {
		case BOOTS:
		case BOW:
		case CHESTPLATE:
		case HELMET:
		case LEGGINGS:
		case MELEE:
			int max_tries = 20;
			int n_tries = 0;
			while (n_tries < max_tries && effective_score < max_score)
			{
				n_tries += 1;
				EnchantedDropData data = MyMath.RandomChoiceUniform(enchantments_score.get(dropType));
				
				// reject because this would make a too high item
				if (data.drop_score + effective_score > max_score)
					continue;
				// reject because enchantment and item are incompatible
				if ((data.ench.equals(Enchantment.DAMAGE_ALL) || data.ench.equals(CustomEnchants.LIFESTEAL)) && mat.equals(Material.STONE_PICKAXE))
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
			break;
		case SUPPLIES:
			int quantity_supplies = (int) ((max_score - drop_base_score.get(dropType).get(mat)/2)/countable_scores.get(mat).per_unit_score);
			item.setAmount(quantity_supplies);
			effective_score = quantity_supplies*countable_scores.get(mat).per_unit_score + drop_base_score.get(dropType).get(mat)/2;
			if (mat.equals(Material.SPLASH_POTION))
			{
                PotionMeta potionmeta = (PotionMeta) item.getItemMeta();
                potionmeta.setMainEffect(PotionEffectType.HEAL);
                potionmeta.addCustomEffect(new PotionEffect(PotionEffectType.HEAL, 1, 2), true);
                potionmeta.addCustomEffect(new PotionEffect(PotionEffectType.ABSORPTION, 30*20, 1), true);
                potionmeta.setDisplayName("§9Potion of Restauration");
                item.setItemMeta(potionmeta);
			}
			break;
		case MONEY:
			int quantity_gold = (int) ((max_score - drop_base_score.get(dropType).get(mat))/countable_scores.get(mat).per_unit_score);
			item.setAmount(quantity_gold);
			effective_score = quantity_gold*countable_scores.get(mat).per_unit_score + drop_base_score.get(dropType).get(mat);
			break;
		default:
			break;
		}
		DuoDungeonPlugin.logg(item.toString() + " " + item.getItemMeta().getEnchants().toString());

		DuoDungeonPlugin.logg(rarity.toString() + " " + String.valueOf(effective_score) + "/" + String.valueOf(max_score));
		if (effective_score != max_score)
			DuoDungeonPlugin.logg("The drop does not have the full potential score");
		return item;
	}
}
