package lgerrets.duodungeon;

import java.util.ArrayList;
import java.util.EnumMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import lgerrets.duodungeon.enchantments.CustomEnchants;
import lgerrets.duodungeon.listeners.Commands;
import lgerrets.duodungeon.listeners.EnchantmentEvents;
import lgerrets.duodungeon.listeners.MobEvents;
import lgerrets.duodungeon.listeners.PlayerEvents;
import lgerrets.duodungeon.players.DuoBuilder;
import lgerrets.duodungeon.players.DuoNoteamer;
import lgerrets.duodungeon.players.DuoPlayer;
import lgerrets.duodungeon.players.DuoRunner;
import lgerrets.duodungeon.players.DuoTeam;
import lgerrets.duodungeon.players.DuoTeammate;
import lgerrets.duodungeon.players.DuoTeam.TeamType;

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
    
    public static void err(Object obj)
    {
    	shout("ERROR!!!");
    	System.out.println("Error!!!");
    	System.out.println(obj);
    }
    
    public static void shout(String msg)
    {
    	Bukkit.broadcastMessage(msg);
    }
}
