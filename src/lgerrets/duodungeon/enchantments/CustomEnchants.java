package lgerrets.duodungeon.enchantments;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.enchantments.Enchantment;

public class CustomEnchants {
	public static final Enchantment PRINT = new EnchantmentWrapper("print", "Print", 1);
	
	public static void register() {
		boolean registered = Arrays.stream(Enchantment.values()).collect(Collectors.toList()).contains(PRINT);
		
		if (!registered)
		{
			registerEnchantment(PRINT);
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
}
