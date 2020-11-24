package lgerrets.duodungeon.enchantments;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.enchantments.Enchantment;

public class CustomEnchants {
	public static final Enchantment PRINT = new EnchantmentWrapper("duoprint", "Print", 1);
	public static final Enchantment GATHERER = new EnchantmentWrapper("duogatherer", "Gatherer", 2);
	public static final Enchantment LIFESTEAL = new EnchantmentWrapper("duolifesteal", "Lifesteal", 2);
	public static final Enchantment ROBUST = new EnchantmentWrapper("duorobust", "Robust", 2);
	public static final Enchantment SPEED = new EnchantmentWrapper("duospeed", "Speed", 1);
	public static final Enchantment JUMPY = new EnchantmentWrapper("duojumpy", "Jumpy", 1);
	public static final Enchantment STRENGTH = new EnchantmentWrapper("duostrength", "Strength", 2);
	
	public static void register() {
		boolean registered = Arrays.stream(Enchantment.values()).collect(Collectors.toList()).contains(PRINT);
		
		if (!registered)
		{
			registerEnchantment(PRINT);
			registerEnchantment(GATHERER);
			registerEnchantment(LIFESTEAL);
			registerEnchantment(ROBUST);
			registerEnchantment(SPEED);
			registerEnchantment(JUMPY);
			registerEnchantment(STRENGTH);
		}
	}
	
	public static void registerEnchantment(Enchantment en)
	{
		boolean registered = true;
		try {
			Field f = Enchantment.class.getDeclaredField("acceptingNew");
			f.setAccessible(true);
			f.set(null,  true);
			Enchantment.registerEnchantment(en);
		} catch(Exception e) {
			registered = false;
			e.printStackTrace();
		}
	}
	
	public static String levelToString(int level) {
		if(level == 4)
			return "IV";
		else if (level == 9)
			return "IX";
		else {
			String ret = "";
			if (level >= 5)
			{
				level -= 5;
				ret = "V";
			}
			for (int i=0; i<level; i+=1)
				ret += "I";
			return ret;
		}
		
	}
}
