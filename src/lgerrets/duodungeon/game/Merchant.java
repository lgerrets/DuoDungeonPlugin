package lgerrets.duodungeon.game;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Merchant {
	
	private Villager vill;
	private ArrayList<MerchantRecipe> recipes;
	
	public Merchant(Location loc) {
		vill = (Villager) DuoMap.world.spawnEntity(loc, EntityType.VILLAGER);
		vill.setProfession(Profession.ARMORER);
		vill.setVillagerLevel(5);
		vill.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 999999*20, 255));
		recipes = new ArrayList<MerchantRecipe>();
		vill.setRecipes(recipes);
	}
	
	public void AddRecipe(ItemStack result, ItemStack item1) {
		MerchantRecipe recipe = new MerchantRecipe(result, 99999);
		recipe.addIngredient(item1);
		recipes.add(recipe);
		vill.setRecipes(recipes);
	}
	
	public void AddRecipe(ItemStack result, ItemStack item1, ItemStack item2) {
		MerchantRecipe recipe = new MerchantRecipe(result, 99999);
		recipe.addIngredient(item1);
		recipe.addIngredient(item2);
		recipes.add(recipe);
		vill.setRecipes(recipes);
	}
}
