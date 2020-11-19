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
import lgerrets.duodungeon.game.DuoPlayer;
import lgerrets.duodungeon.game.DuoTeam;
import lgerrets.duodungeon.utils.Index2d;
import lgerrets.duodungeon.utils.Index2d.Direction;
import lgerrets.duodungeon.utils.MyMath;

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
    	else if (args[0].equalsIgnoreCase("team"))
    	{
    		String team;
    		if (args.length != 2)
    			team = "none";
    		team = args[1];
    		if(!(sender instanceof Player))
    			return false;
    		DuoPlayer p = DuoPlayer.getPlayer(((Player) sender).getUniqueId());
    		if (team.equalsIgnoreCase("builder"))
    		{
    			p.setTeam(DuoTeam.teams.get(DuoTeam.TeamType.BUILDER));
    		}
    		else if (team.equalsIgnoreCase("runner"))
    		{
    			p.setTeam(DuoTeam.teams.get(DuoTeam.TeamType.RUNNER));
    		}
    		else
    		{
    			p.setTeam(DuoTeam.teams.get(DuoTeam.TeamType.NONE));
    		}
    	}
    	else
    	{
    		return false;
    	}
		return true;
    }
}
