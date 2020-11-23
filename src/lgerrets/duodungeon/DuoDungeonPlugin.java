package lgerrets.duodungeon;

import java.util.ArrayList;
import java.util.EnumMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import lgerrets.duodungeon.enchantments.CustomEnchants;
import lgerrets.duodungeon.game.DuoBuilder;
import lgerrets.duodungeon.game.DuoNoteamer;
import lgerrets.duodungeon.game.DuoPlayer;
import lgerrets.duodungeon.game.DuoRunner;
import lgerrets.duodungeon.game.DuoTeam;
import lgerrets.duodungeon.game.DuoTeammate;
import lgerrets.duodungeon.game.DuoTeam.TeamType;
import lgerrets.duodungeon.listeners.Commands;
import lgerrets.duodungeon.listeners.EnchantmentEvents;
import lgerrets.duodungeon.listeners.MobEvents;
import lgerrets.duodungeon.listeners.PlayerEvents;

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
    	DuoPlayer.RegisterListener(this);
    	PlayerEvents playerEvents = new PlayerEvents();
		Bukkit.getPluginManager().registerEvents(playerEvents, this);
		CustomEnchants.register();
		EnchantmentEvents.register(this);
		MobEvents.register(this);
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
