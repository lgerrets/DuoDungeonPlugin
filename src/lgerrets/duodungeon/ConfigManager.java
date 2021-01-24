package lgerrets.duodungeon;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import lgerrets.duodungeon.utils.Cooldown;

public class ConfigManager {
	public static YamlConfiguration DDConfig = null;
	private static File mainConfigFile = null;
	private static int do_save = 0;
	
	public ConfigManager(Plugin p)
	{
		load(p);
	}
	
	public static void load(Plugin p)
	{
		mainConfigFile = new File(p.getDataFolder().getAbsolutePath());
		if(!mainConfigFile.exists() || !mainConfigFile.isDirectory())
			mainConfigFile.mkdir();
		mainConfigFile = new File(p.getDataFolder().getAbsolutePath(), "DDConfig.yml");
		if(!mainConfigFile.exists())
			try {
				mainConfigFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		DDConfig = YamlConfiguration.loadConfiguration(mainConfigFile);

		setDefaultIfNotSet(DDConfig, "verbose", 0);
		setDefaultIfNotSet(DDConfig, "tile_size", 6);
		setDefaultIfNotSet(DDConfig, "max_height", 30);
		setDefaultIfNotSet(DDConfig, "floor_level", 5);
		setDefaultIfNotSet(DDConfig, "world", "world");

		ConfigurationSection gameConfig = setDefaultSectionIfNotSet(DDConfig, "Game");
		setDefaultIfNotSet(gameConfig, "rare_drops", 0.2);
		setDefaultIfNotSet(gameConfig, "epic_drops", 0.05);
		setDefaultIfNotSet(gameConfig, "legendary_drops", 0.02);
		setDefaultIfNotSet(gameConfig, "superstun_duration", 100);
		setDefaultIfNotSet(gameConfig, "superstun_squaresize", 5); // (if even, will be made odd)
		setDefaultIfNotSet(gameConfig, "piece_lifetime", 800);
		setDefaultIfNotSet(gameConfig, "piece_lifetime_active", 400);
		setDefaultIfNotSet(gameConfig, "piece_onactive_set_minlifetime", 200);
		setDefaultIfNotSet(gameConfig, "npieces_decrease_lifetime", 10); // when this number of pieces are still alive, placing a new piece will decrease all current pieces' lifetimes by ...
		setDefaultIfNotSet(gameConfig, "npieces_decrease_lifetime_by", 100); // ... this much
		setDefaultIfNotSet(gameConfig, "struct_spawn_dist", 8);
		setDefaultIfNotSet(gameConfig, "x_delta_asteroids", 2);
		setDefaultIfNotSet(gameConfig, "x_delta_checkpoints", 20);
		
		ConfigurationSection waypoints = setDefaultSectionIfNotSet(DDConfig, "Waypoints");
		setDefaultWaypointIfNotSet(waypoints, "pastebin"); // origin of a free pastebin area (north-west corner) which extends to the south (z+)
		setDefaultWaypointIfNotSet(waypoints, "dungeon_origin");
		setDefaultIfNotSet(DDConfig, "piece_separation", 20); // in the north-south (z+) direction, pieces are placed every piece_separation
		setDefaultIfNotSet(DDConfig, "o_pieces", 3);
		setDefaultIfNotSet(DDConfig, "lr_pieces", 1);
		setDefaultIfNotSet(DDConfig, "l_pieces", 1);
		setDefaultIfNotSet(DDConfig, "z_pieces", 1);
		setDefaultIfNotSet(DDConfig, "s_pieces", 1);
		setDefaultIfNotSet(DDConfig, "t_pieces", 1);
		setDefaultIfNotSet(DDConfig, "i_pieces", 1);
		
		setDefaultWaypointIfNotSet(waypoints, "tetris_o"); // all o-shaped pieces are placed starting from this position (y-bottom north-west corner) and then to the south (seperated by a margin of air blocks, see <piece_separation>)
		setDefaultWaypointIfNotSet(waypoints, "tetris_lr"); // etc...
		setDefaultWaypointIfNotSet(waypoints, "tetris_l");
		setDefaultWaypointIfNotSet(waypoints, "tetris_z");
		setDefaultWaypointIfNotSet(waypoints, "tetris_s");
		setDefaultWaypointIfNotSet(waypoints, "tetris_t");
		setDefaultWaypointIfNotSet(waypoints, "tetris_i");
		setDefaultWaypointIfNotSet(waypoints, "bomb");
		setDefaultWaypointIfNotSet(waypoints, "obstacle");
		setDefaultWaypointIfNotSet(waypoints, "checkpoint");
		setDefaultWaypointIfNotSet(waypoints, "builder");
		setDefaultWaypointIfNotSet(waypoints, "runner");
		
		ConfigurationSection controls = setDefaultSectionIfNotSet(DDConfig, "Controls");
		setDefaultIfNotSet(controls, "builder_move_cooldown", 5);
		setDefaultIfNotSet(controls, "builder_act_cooldown", 3);
		setDefaultIfNotSet(controls, "builder_maxdx_from_controledstruct", 130);
		
		ConfigurationSection ambience = setDefaultSectionIfNotSet(DDConfig, "ambience");
		setDefaultIfNotSet(ambience, "volume_piece_near", 0.5);
		setDefaultIfNotSet(ambience, "volume_piece_active", 1.0);
		setDefaultIfNotSet(ambience, "ticks_piece_disappear_sound", 50);
		setDefaultIfNotSet(ambience, "builder_tetris_grid_gui_dist", 10);
		
		if(do_save > 0)
			saveMainConfig();
	}
	
	public static ConfigurationSection setDefaultSectionIfNotSet(ConfigurationSection section, String path)
	{
		ConfigurationSection subsection;
		if(!section.isConfigurationSection(path))
		{
			subsection = section.createSection(path);
			do_save += 1;
		}
		else 
			subsection = section.getConfigurationSection(path);
		return subsection;
	}
	
	public static void setDefaultIfNotSet(ConfigurationSection section, String path, Object value)
	{
		if(section != null)
		{
			if(!section.isSet(path))
			{
				section.set(path, value);
				do_save += 1;
			}
		}
	}
	
	public static void setDefaultWaypointIfNotSet(ConfigurationSection section, String path)
	{
		if(section != null)
		{
			ConfigurationSection wp = setDefaultSectionIfNotSet(section, path);
			setDefaultIfNotSet(wp, "X", 0);
			setDefaultIfNotSet(wp, "Y", 0);
			setDefaultIfNotSet(wp, "Z", 0);
		}
	}
	
	public static YamlConfiguration getConfig()
	{
		return DDConfig;
	}
	
	public static void saveMainConfig()
	{
		if(DDConfig != null)
		{
			try
			{
				DDConfig.save(mainConfigFile);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
