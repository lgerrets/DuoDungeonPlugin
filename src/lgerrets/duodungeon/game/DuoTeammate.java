package lgerrets.duodungeon.game;

public class DuoTeammate {
	protected DuoPlayer player;
	
	public DuoTeammate(DuoPlayer player) {
		this.player = player;
	}
	
	public DuoPlayer getDuoPlayer() {
		return player;
	}
}
