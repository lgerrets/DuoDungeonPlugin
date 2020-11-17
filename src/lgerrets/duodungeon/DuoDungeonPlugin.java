package lgerrets.duodungeon;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import lgerrets.duodungeon.listeners.Commands;

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
    	this.getCommand("dd").setExecutor(new Commands());
    }
    
    public static WorldEditPlugin getWorldEdit()
    {
    	return (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
    }
    
    // Fired when plugin is disabled
    @Override
    public void onDisable() {

    }
    
    public static void logg(Object obj)
    {
    	System.out.println(obj);
    }
}
