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

public class Commands implements CommandExecutor  {
	
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
    	if (label == "start")
    	{
    		
    	}
		return true;    	
    }
}
