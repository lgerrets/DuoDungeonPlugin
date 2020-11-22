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
