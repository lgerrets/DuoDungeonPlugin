package lgerrets.duodungeon.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import lgerrets.duodungeon.ConfigManager;
import lgerrets.duodungeon.game.DungeonMap;

public class Commands implements CommandExecutor  {
	
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
    	if (args.length == 0)
    		return false;
    	if (args[0].equalsIgnoreCase("start"))
    	{
    		DungeonMap.InitializeDungeon();
    	}
    	else if (args[0].equalsIgnoreCase("spawn"))
    	{
    		DungeonMap.game.SpawnNewPiece();
    	}
    	else
    	{
    		return false;
    	}
		return true;    	
    }
}
