package lgerrets.duodungeon;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class DuoDungeonPlugin extends JavaPlugin {
	private static JavaPlugin instance;
	public static JavaPlugin getInstance()
	{
		return instance;
	}
	
	private static ConfigManager configMgr;
	
    // Fired when plugin is first enabled
    @Override
    public void onEnable() {
    	instance = this;
    	configMgr = new ConfigManager(this);
    	DummyClass dummyClass = new DummyClass();
    	Bukkit.getPluginManager().registerEvents(dummyClass, DuoDungeonPlugin.getInstance());
    }
    
    // Fired when plugin is disabled
    @Override
    public void onDisable() {

    }
}
