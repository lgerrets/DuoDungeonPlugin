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
		setDefaultIfNotSet(DDConfig, "max_height", 20);
		setDefaultIfNotSet(DDConfig, "world", "world");

		ConfigurationSection gameConfig = setDefaultSectionIfNotSet(DDConfig, "Game");
		setDefaultIfNotSet(gameConfig, "rare_drops", 0.2);
		setDefaultIfNotSet(gameConfig, "epic_drops", 0.05);
		setDefaultIfNotSet(gameConfig, "legendary_drops", 0.02);
		setDefaultIfNotSet(gameConfig, "superstun_duration", 100);
		setDefaultIfNotSet(gameConfig, "superstun_squaresize", 5); // (if even, will be made odd)
		setDefaultIfNotSet(gameConfig, "piece_lifetime", 400);
		
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
		
		ConfigurationSection controls = setDefaultSectionIfNotSet(DDConfig, "Controls");
		setDefaultIfNotSet(controls, "builder_move_cooldown", 5);
		setDefaultIfNotSet(controls, "builder_act_cooldown", 3);
		//setDefaultIfNotSet(controls, "builder_resetpos_cooldown", 20);
		
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
