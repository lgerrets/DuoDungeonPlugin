package lgerrets.duodungeon.game;

import java.util.ArrayList;
import java.util.EnumMap;

import org.bukkit.ChatColor;

public class DuoTeam {
	public enum TeamType
	{
		NONE,
		BUILDER,
		RUNNER,
	}
	
	public static EnumMap<TeamType, DuoTeam> teams = new EnumMap<>(TeamType.class);
	static {
		teams.put(TeamType.NONE, new DuoTeam(ChatColor.WHITE, TeamType.NONE));
		teams.put(TeamType.BUILDER, new DuoTeam(ChatColor.GREEN, TeamType.BUILDER));
		teams.put(TeamType.RUNNER, new DuoTeam(ChatColor.RED, TeamType.RUNNER));
	}
	
	static public ArrayList<DuoNoteamer> none_players = new ArrayList<DuoNoteamer>(); 
	static public ArrayList<DuoBuilder> builder_players = new ArrayList<DuoBuilder>(); 
	static public ArrayList<DuoRunner> runner_players = new ArrayList<DuoRunner>(); 
	static public EnumMap<TeamType, ArrayList<? extends DuoTeammate>> all_players = new EnumMap<>(TeamType.class);
	static {
		all_players.put(TeamType.NONE, none_players);
		all_players.put(TeamType.BUILDER, builder_players);
		all_players.put(TeamType.RUNNER, runner_players);
	}
	
	public ChatColor color;
	public TeamType teamType;
	
	public DuoTeam(ChatColor color, TeamType teamType)
	{
		this.color = color;
		this.teamType = teamType;
	}
	
	static void removePlayer(DuoPlayer p, TeamType teamType) {
		for (DuoTeammate teammate : all_players.get(teamType))
		{
			if (teammate.getDuoPlayer().equals(p))
			{
				all_players.get(teamType).remove(teammate);
				break;
			}
		}
	}
}
