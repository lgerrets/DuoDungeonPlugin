package lgerrets.duodungeon.game;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.utils.MyMath;

public class DuoBuilder extends DuoTeammate {
	private double combo;
	private double goldCpt; // everytime this reaches goldCptMax, award 1 gold
	static private double goldCptMax = 80;
	
	public DuoBuilder(DuoPlayer player) {
		super(player);
		player.getPlayer().setGameMode(GameMode.ADVENTURE);
		combo = 0.0;
		goldCpt = 0.0;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(DuoDungeonPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
            	if (DuoMap.game.IsRunning())
            	{
	            	generatePassiveGold();
	                updateCombo(-0.001);
            	}
            }
        }, 0, 1);
	}
	
	public void updateCombo(double delta) {
		combo += delta;
		combo = MyMath.Min(combo, 1.0);
		combo = MyMath.Max(combo, 0.0);
		player.getPlayer().setExp((float) combo);
	}
	
	public void generatePassiveGold() {
		goldCpt += combo*combo;
		if (goldCpt >= goldCptMax) {
			goldCpt -= goldCptMax;
			player.getPlayer().getInventory().addItem(new ItemStack(Material.GOLD_NUGGET));
		}
	}
}
