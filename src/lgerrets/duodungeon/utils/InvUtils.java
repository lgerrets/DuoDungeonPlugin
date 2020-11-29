package lgerrets.duodungeon.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InvUtils {
	static public int ChangeItemNb(Player player, int delta, Material mat)
	{
		PlayerInventory inv = player.getInventory();
		for(int i = 0; i < inv.getSize(); i++){
			ItemStack itm = inv.getItem(i);
			if(itm != null && itm.getType().equals(mat)) {
				int amt = itm.getAmount() + delta;
				if (amt <= 0)
				{
					amt = 0;
					delta = - itm.getAmount();
				}
				itm.setAmount(amt);
				inv.setItem(i, amt > 0 ? itm : null);
				player.updateInventory();
				break;
			}
		}
		return delta;
	}
}
