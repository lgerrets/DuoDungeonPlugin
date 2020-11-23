package lgerrets.duodungeon.game;

import org.bukkit.GameMode;

public class DuoRunner extends DuoTeammate {

	public DuoRunner(DuoPlayer player) {
		super(player);
		player.getPlayer().setGameMode(GameMode.ADVENTURE);
	}
	
}
