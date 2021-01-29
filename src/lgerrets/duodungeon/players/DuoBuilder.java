package lgerrets.duodungeon.players;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
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
import lgerrets.duodungeon.game.DuoMap.StructureType;
import lgerrets.duodungeon.game.Piece;
import lgerrets.duodungeon.game.Structure;
import lgerrets.duodungeon.game.Teleporter;
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
	// placing Teleporters
	private boolean placing_teleporter_entrance;
	private Index2d teleporter_entrance;
	
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
	        			double thales_ratio = ((double) builder_tetris_grid_gui_dist) / ((double) coords_player.getY() - (double) reference_point.y);
	        			int range = 8;
	        			int granularity = 1; // DuoMap.tile_size;
	        			for (int idx=MyMath.Max(0, id2_player.x-range); idx<=MyMath.Min(DuoMap.game.XMax-1, id2_player.x+range); idx+=1)
	        			{
	        				for (int idz=MyMath.Max(0, id2_player.z-range); idz<=MyMath.Min(DuoMap.game.ZMax-1, id2_player.z+range); idz+=1)
	        				{
	        					tile_origin = Coords3d.Index2dToCoords3d(new Index2d(idx, idz), reference_point);
	    						tile_x = tile_origin.x;
	    						tile_z = tile_origin.z;
	    						
	    						x = thales_ratio*(tile_x - (double) coords_player.getX()) + coords_player.getX();
	        					for (double dz=0 ; dz < DuoMap.tile_size ; dz+=granularity)
	        					{
		    						z = thales_ratio*(tile_z + dz - (double) coords_player.getZ()) + coords_player.getZ();
	    							p.spawnParticle(Particle.VILLAGER_HAPPY, x, y, z, 1);
	        					}
	        					
	        					z = thales_ratio*(tile_z - (double) coords_player.getZ()) + coords_player.getZ();
	        					for (double dx=0 ; dx < DuoMap.tile_size ; dx+=granularity)
	        					{
		    						x = thales_ratio*(tile_x + dx - (double) coords_player.getX()) + coords_player.getX();
	    							p.spawnParticle(Particle.VILLAGER_HAPPY, x, y, z, 1);
	        					}
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
		InvUtils.ChangeItemNb(getDuoPlayer().getPlayer(), -999, Material.FLINT);
		p.setGameMode(GameMode.SURVIVAL);
		ItemStack buttons = new ItemStack(Material.STONE_BUTTON);
		ItemMeta meta = buttons.getItemMeta();
		meta.setDisplayName("TETRIS");
		buttons.setItemMeta(meta);
		InvUtils.addItems(p, 64, buttons);
		InvUtils.addItems(p, 3, Material.TNT);
		InvUtils.addItems(p, 3, Material.NETHER_STAR);
		InvUtils.addItems(p, 1, Material.FLINT);
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
		
		placing_teleporter_entrance = true;
		teleporter_entrance = null; // should be null if placing_teleporter_entrance is true
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
	
	static public DuoBuilder GetBuilder(Player p)
	{
		DuoBuilder builder = null;
		for (DuoBuilder b : DuoTeam.builder_players)
		{
			if (b.getDuoPlayer().getPlayer() == p)
			{
				builder = b;
				break;
			}
		}
		return builder;
	}
	
	public void UseThunder(Location loc)
	{
		int delta = InvUtils.ChangeItemNb(this.getDuoPlayer().getPlayer(), -1, Material.NETHER_STAR);
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
	
	public void TryPlaceTeleporter(Location loc)
	{
		Index2d idx = new Index2d(DuoMap.dungeon_origin, new Coords3d(loc));
		if (DuoMap.game.GetMap(idx.x, idx.z, StructureType.EMPTY) != StructureType.PEACEFUL)
			return;
		Player player = this.getDuoPlayer().getPlayer();
		if (placing_teleporter_entrance)
		{
			placing_teleporter_entrance = false;
			teleporter_entrance = idx;
			player.playSound(loc, Sound.ENTITY_ENDER_DRAGON_HURT, 20.f, 1);
		}
		else
		{
			if (teleporter_entrance.equals(idx))
			{
				return;
			}
			Teleporter exit = new Teleporter(idx, false, null);
			Teleporter entrance = new Teleporter(teleporter_entrance, true, exit);
			InvUtils.ChangeItemNb(player, -1, Material.FLINT);
			player.playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 20.f, 1);
			placing_teleporter_entrance = true;
			teleporter_entrance = null;
		}
	}
}
