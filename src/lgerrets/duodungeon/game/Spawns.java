package lgerrets.duodungeon.game;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

import lgerrets.duodungeon.enchantments.CustomEnchants;
import lgerrets.duodungeon.game.Drops.ChestRarity;
import lgerrets.duodungeon.players.DuoTeam;
import lgerrets.duodungeon.utils.MyMath;

public class Spawns {
	
	static private EnumMap<EntityType, Double> mob_score = new EnumMap<EntityType, Double>(EntityType.class);
	static {
		mob_score.put(EntityType.ZOMBIE, 1.0);
		mob_score.put(EntityType.SKELETON, 2.0);
		mob_score.put(EntityType.SPIDER, 2.0);
		mob_score.put(EntityType.CREEPER, 3.0);
		mob_score.put(EntityType.CAVE_SPIDER, 4.0);
	}
	
	static private EnumMap<EntityType, Double> mob_health = new EnumMap<EntityType, Double>(EntityType.class);
	static {
		mob_health.put(EntityType.ZOMBIE, 6.0);
		mob_health.put(EntityType.SKELETON, 4.0);
		mob_health.put(EntityType.SPIDER, 4.0);
		mob_health.put(EntityType.CREEPER, 6.0);
		mob_health.put(EntityType.CAVE_SPIDER, 4.0);
	}
	
	private enum BonusType {
		// stuff
		MELEE, CHESTPLATE,
		// health
		HEALTH,
	};
	
	static private class StuffData {
		private double score;
		private Material mat;
		StuffData(Material mat, double drop_score) {
			this.mat = mat;
			this.score = drop_score;
		}
	}
	
	static private EnumMap<BonusType, Set<StuffData>> stuff_score = new EnumMap<BonusType, Set<StuffData>>(BonusType.class);
	static {
		Set<StuffData> temp_stuff = new HashSet<StuffData>();
		temp_stuff.add(new StuffData(Material.STONE_SHOVEL, 2.0));
		stuff_score.put(BonusType.MELEE, temp_stuff);
		
		temp_stuff = new HashSet<StuffData>();
		temp_stuff.add(new StuffData(Material.LEATHER_CHESTPLATE, 2.0));
		stuff_score.put(BonusType.CHESTPLATE, temp_stuff);
		
	}
	
	static private EnumMap<BonusType, Double> stuff_score_mins = new EnumMap<BonusType, Double>(BonusType.class);
	static {
		double min;
		for (BonusType bonus : stuff_score.keySet())
		{
			min = 999;
			for (StuffData data : stuff_score.get(bonus))
				min = MyMath.Min(min,  data.score);
			stuff_score_mins.put(bonus, min);
		}
	}
	
	public static void DrawMob(Location loc, double d)
	{
		// draw EntityType
		EntityType type = null;
		double effective_score = 999.0;
		while (effective_score > d)
		{
			type = MyMath.RandomChoiceUniform(mob_score.keySet());
			effective_score = mob_score.get(type);
		}
		
		Mob mob = (Mob) DuoMap.world.spawnEntity(loc, type);
		if (type == EntityType.ZOMBIE)
		{
			Zombie z = (Zombie) mob;
			z.setBaby(false);
		}
		mob.getEquipment().setHelmet(new ItemStack(Material.STONE_BUTTON));
		
		int drawn_health_bonus = 0;
		
		for (int n_draws=0; n_draws<5; n_draws+=1)
		{
			// draw BonusType
			BonusType bonus = null;
			boolean done = false;
			int n_draw_bonus_tries = 0;
			while (!done)
			{
				bonus = BonusType.HEALTH;
				n_draw_bonus_tries += 1;
				if (n_draw_bonus_tries > 999)
					break;
				bonus = MyMath.RandomChoiceUniform(BonusType.values());
				if (bonus == BonusType.MELEE && type != EntityType.ZOMBIE)
					continue;
				if (stuff_score.containsKey(bonus) && stuff_score_mins.get(bonus) + effective_score > d)
					continue;
				done = true;
			}
			
			// draw StuffData
			switch(bonus)
			{
			case MELEE:
			case CHESTPLATE:
				double delta_stuff_score = 999;
				StuffData stuff = null;
				while(effective_score + delta_stuff_score > d) {
					stuff = MyMath.RandomChoiceUniform(stuff_score.get(type));
					delta_stuff_score = stuff.score;
				}
				switch(bonus)
				{
				case MELEE:
					mob.getEquipment().setItemInMainHand(new ItemStack(stuff.mat));
					break;
				case CHESTPLATE:
					mob.getEquipment().setChestplate(new ItemStack(stuff.mat));
					break;
				default:
					break;
				}
				break;
			case HEALTH:
				double score_delta = (2.0 + drawn_health_bonus)/2; // thresholds are 1 / 2.5 / 4.5 ...
				if (effective_score + score_delta <= d)
				{
					effective_score += score_delta;
					drawn_health_bonus += 1;
				}
				break;
			default:
				break;
			}
		}
		
		mob.setHealth(mob_health.get(type) + 4.0*drawn_health_bonus);		
	}
}
