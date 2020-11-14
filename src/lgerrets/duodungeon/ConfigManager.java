package lgerrets.duodungeon;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigManager {
	public static YamlConfiguration DDConfig = null;
	private static File mainConfigFile = null;
	
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
		
		int save = 0;

        save += setDefaultIfNotSet(DDConfig, "verbose", 0);

		ConfigurationSection gameConfig;
		if(!DDConfig.isConfigurationSection("Game"))
		{
			gameConfig = DDConfig.createSection("Game");
			save += 1;
		}
		else 
			gameConfig = DDConfig.getConfigurationSection("Game");
		
		if(save > 0)
			saveMainConfig();
	}
	
	public static int setDefaultIfNotSet(ConfigurationSection section, String path, Object value)
	{
		if(section != null)
		{
			if(!section.isSet(path))
			{
				section.set(path, value);
				return 1;
			}
		}
		return 0;
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
