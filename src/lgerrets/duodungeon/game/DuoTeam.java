package lgerrets.duodungeon.game;

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
	
	public ChatColor color;
	public TeamType teamType;
	
	public DuoTeam(ChatColor color, TeamType teamType)
	{
		this.color = color;
		this.teamType = teamType;
	}
}
