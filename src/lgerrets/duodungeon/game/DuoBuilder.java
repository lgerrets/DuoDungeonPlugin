package lgerrets.duodungeon.game;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.utils.InvUtils;
import lgerrets.duodungeon.utils.MyMath;

public class DuoBuilder extends DuoTeammate {
	private double combo;
	private double goldCpt; // everytime this reaches goldCptMax, award 1 gold
	static private double goldCptMax = 80;
	static public int bomb_count = 3;
	
	public DuoBuilder(DuoPlayer player) {
		super(player);
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
	
	public void ResetBuilder()
	{
		InvUtils.ChangeItemNb(getDuoPlayer().getPlayer(), -999, Material.TNT);
		InvUtils.ChangeItemNb(getDuoPlayer().getPlayer(), -999, Material.GOLD_NUGGET);
		player.getPlayer().setGameMode(GameMode.ADVENTURE);
		ItemStack tnts = new ItemStack(Material.TNT);
		tnts.setAmount(3);
		player.getPlayer().getInventory().addItem(tnts);
		combo = 0.0;
		goldCpt = 0.0;
	}
	
	static public void Reset()
	{
		for (DuoBuilder builder : DuoTeam.builder_players)
		{
			builder.ResetBuilder();
		}
	}
	
	static public boolean DecreaseBombCount()
	{
		if (DuoMap.game.IsNextBomb())
			return false;
		int delta = 0;
		for (DuoBuilder builder : DuoTeam.builder_players)
		{
			delta = InvUtils.ChangeItemNb(builder.getDuoPlayer().getPlayer(), -1, Material.TNT);
		}
		return delta != 0;
	}
}
