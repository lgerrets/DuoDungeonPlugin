package lgerrets.duodungeon.players;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldedit.regions.CuboidRegion;

import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.game.DuoMap;
import lgerrets.duodungeon.utils.Coords3d;
import lgerrets.duodungeon.utils.InvUtils;
import lgerrets.duodungeon.utils.MyMath;
import lgerrets.duodungeon.utils.WEUtils;

public class DuoBuilder extends DuoTeammate {
	private double combo;
	private double goldCpt; // everytime this reaches goldCptMax, award 1 gold
	private int taskId;
	static private double goldCptMax = 80;
	static public int bomb_count = 3;
	
	public DuoBuilder(DuoPlayer player) {
		super(player);
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(DuoDungeonPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
            	if (DuoMap.game.IsRunning())
            	{
	            	generatePassiveGold();
	                updateCombo(-0.001);
            	}
            }
        }, 0, 1);
		type = DuoTeam.TeamType.BUILDER;
		if(DuoMap.game.IsRunning())
			this.ResetBuilder();
	}
	
	@Override
	public void Unregister()
	{
		player.getPlayer().removePotionEffect(PotionEffectType.SPEED);
		super.Unregister();
		Bukkit.getScheduler().cancelTask(taskId);
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
		Player p = player.getPlayer();
		InvUtils.ChangeItemNb(getDuoPlayer().getPlayer(), -999, Material.STONE_BUTTON);
		InvUtils.ChangeItemNb(getDuoPlayer().getPlayer(), -999, Material.TNT);
		InvUtils.ChangeItemNb(getDuoPlayer().getPlayer(), -999, Material.GOLD_NUGGET);
		p.setGameMode(GameMode.SURVIVAL);
		ItemStack buttons = new ItemStack(Material.STONE_BUTTON);
		ItemMeta meta = buttons.getItemMeta();
		meta.setDisplayName("TETRIS");
		buttons.setItemMeta(meta);
		InvUtils.addItems(p, 64, buttons);
		InvUtils.addItems(p, 3, Material.TNT);
		combo = 0.0;
		goldCpt = 0.0;
		Coords3d coords = Coords3d.FromWaypoint("builder");
		int bottom = -5;
		//WEUtils.FillRegion(DuoMap.WEWorld, new CuboidRegion(coords.add(-3,0,-3).toBlockVector3(), coords.add(3,bottom,3).toBlockVector3()), Material.AIR.createBlockData());
		//DuoMap.world.getBlockAt(coords.x, coords.y-2, coords.z).setType(Material.WHITE_STAINED_GLASS_PANE);
		//WEUtils.FillRegion(DuoMap.WEWorld, new CuboidRegion(coords.add(-3,bottom,-3).toBlockVector3(), coords.add(3,bottom,3).toBlockVector3()), Material.BARRIER.createBlockData());
		p.teleport(new Location(DuoMap.world, coords.x+0.5, coords.y, coords.z+0.5, -90, 90));
		super.ResetTeammate();
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 4, true, false));
		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true, false));
		p.setSaturation(100000);
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
			return true;
		int delta = 0;
		for (DuoBuilder builder : DuoTeam.builder_players)
		{
			delta = InvUtils.ChangeItemNb(builder.getDuoPlayer().getPlayer(), -1, Material.TNT);
		}
		return delta != 0;
	}
}
