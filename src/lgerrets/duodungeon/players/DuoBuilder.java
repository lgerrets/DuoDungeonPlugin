package lgerrets.duodungeon.players;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldedit.regions.CuboidRegion;

import lgerrets.duodungeon.ConfigManager;
import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.game.DuoMap;
import lgerrets.duodungeon.game.Piece;
import lgerrets.duodungeon.game.Structure;
import lgerrets.duodungeon.utils.Coords3d;
import lgerrets.duodungeon.utils.Index2d;
import lgerrets.duodungeon.utils.InvUtils;
import lgerrets.duodungeon.utils.MyMath;
import lgerrets.duodungeon.utils.WEUtils;

public class DuoBuilder extends DuoTeammate {
	private double combo;
	private double goldCpt; // everytime this reaches goldCptMax, award 1 gold
	static private double goldCptMax = 80;
	static public int bomb_count = 3;
	static private int builder_tetris_grid_gui_dist = ConfigManager.DDConfig.getConfigurationSection("ambience").getInt("builder_tetris_grid_gui_dist");
	
	static {
	    Bukkit.getScheduler().scheduleSyncRepeatingTask(DuoDungeonPlugin.getInstance(), new Runnable() {
	        @Override
	        public void run() {
	        	if (DuoMap.game.IsRunning())
	        	{
	        		Player p;
	        		for (DuoBuilder player : DuoTeam.builder_players) {
	        			// generate gold
	        			player.generatePassiveGold();
	        			player.updateCombo(-0.001);
	        			
	        			// render builder's GUI (tetris grid)
	        			p = player.getDuoPlayer().getPlayer();
	        			Location coords_player = p.getLocation();
	        			Index2d id2_player = (new Coords3d(coords_player)).toIndex2d(DuoMap.dungeon_origin);
	        			double x,y,z,tile_x,tile_z;
	        			Coords3d tile_origin;
	        			y = coords_player.getY() - builder_tetris_grid_gui_dist;
	        			Coords3d reference_point = DuoMap.dungeon_origin;
	        			reference_point = reference_point.add(0, DuoMap.floor_level, 0);
	        			double thales_ratio = ((double) y - reference_point.y) / ((double) coords_player.getY() - reference_point.y);
	        			for (int idx=MyMath.Max(0, id2_player.x-20); idx<=MyMath.Min(DuoMap.game.XMax-1, id2_player.x+20); idx+=1) {
	        				if (MyMath.Mod(idx, 2) == 0)
	        					continue;
	    					for (int idz=MyMath.Max(0, id2_player.z-8); idz<=MyMath.Min(DuoMap.game.ZMax-1, id2_player.z+8); idz+=1) {
		        				if (MyMath.Mod(idz, 2) == 0)
		        					continue;
	    						tile_origin = Coords3d.Index2dToCoords3d(new Index2d(idx, idz), reference_point);
	    						tile_x = tile_origin.x; // + DuoMap.tile_size/2;
	    						tile_z = tile_origin.z; // + DuoMap.tile_size/2;
	    						x = thales_ratio*(coords_player.getX() - tile_x) + tile_x;
	    						z = thales_ratio*(coords_player.getZ() - tile_z) + tile_z;
    							//p.spawnParticle(Particle.VILLAGER_HAPPY, x, y, z, 1, 0, 1, 0); // x,y,z,speed,nb ?
    							p.spawnParticle(Particle.VILLAGER_HAPPY, x, y, z, 1); // x,y,z,speed,nb ?
	    					}
	    				}
	        		}
	        	}
	        }
	    }, 0, 10);
	}
	
	public DuoBuilder(DuoPlayer player) {
		super(player);
		type = DuoTeam.TeamType.BUILDER;
		if(DuoMap.game.IsRunning())
			this.ResetBuilder();
	}
	
	@Override
	public void Unregister()
	{
		player.getPlayer().removePotionEffect(PotionEffectType.SPEED);
		super.Unregister();
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
		InvUtils.ChangeItemNb(getDuoPlayer().getPlayer(), -999, Material.NETHER_STAR);
		p.setGameMode(GameMode.SURVIVAL);
		ItemStack buttons = new ItemStack(Material.STONE_BUTTON);
		ItemMeta meta = buttons.getItemMeta();
		meta.setDisplayName("TETRIS");
		buttons.setItemMeta(meta);
		InvUtils.addItems(p, 64, buttons);
		InvUtils.addItems(p, 3, Material.TNT);
		InvUtils.addItems(p, 3, Material.NETHER_STAR);
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
	
	static public void UseThunder(Location loc)
	{
		int delta = 0;
		for (DuoBuilder builder : DuoTeam.builder_players)
		{
			delta = InvUtils.ChangeItemNb(builder.getDuoPlayer().getPlayer(), -1, Material.NETHER_STAR);
		}
		if (delta < 0)
		{
			DuoMap.world.strikeLightningEffect(loc);
			Collection<Entity> mobs = DuoMap.world.getNearbyEntities(loc, 3, 3, 3, new Structure.IsMob());
			for (Entity ent : mobs)
			{
				Mob mob = (Mob) ent;
				if (!(mob instanceof Villager))
					mob.damage(8);
			}
		}
	}
}
