package lgerrets.duodungeon.listeners;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import lgerrets.duodungeon.game.DuoMap;
import lgerrets.duodungeon.players.DuoPlayer;
import lgerrets.duodungeon.players.DuoTeam;

public class Commands implements CommandExecutor  {
	
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
    	if (args.length == 0)
    		return false;
    	if (args[0].equalsIgnoreCase("start"))
    	{
    		DuoMap.InitializeDungeon();
    	}
    	else if (args[0].equalsIgnoreCase("spawn"))
    	{
    		if (DuoMap.game.IsRunning())
    			DuoMap.game.SpawnNewStruct();
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
