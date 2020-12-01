package lgerrets.duodungeon.game;

import lgerrets.duodungeon.DuoDungeonPlugin;

public class DuoTeammate {
	protected DuoPlayer player;
	protected DuoTeam.TeamType type;
	
	public DuoTeammate(DuoPlayer player) {
		this.player = player;
	}
	
	public DuoPlayer getDuoPlayer() {
		return player;
	}
	
	public void Unregister()
	{
		DuoTeam.all_players.get(this.type).remove(this);
		DuoDungeonPlugin.logg("Players left in team " + type + ": " + DuoTeam.all_players.get(type).size());
	}
}
