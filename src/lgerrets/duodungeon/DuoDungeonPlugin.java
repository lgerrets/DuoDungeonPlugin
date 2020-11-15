package lgerrets.duodungeon;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

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
    
    public static WorldEditPlugin getWorldEdit()
    {
    	return (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
    }
    
    // Fired when plugin is disabled
    @Override
    public void onDisable() {

    }
}
