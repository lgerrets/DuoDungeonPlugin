package lgerrets.duodungeon.players;

import org.bukkit.Sound;

public class DuoNoteamer extends DuoTeammate {

	public DuoNoteamer(DuoPlayer player) {
		super(player);
		type = DuoTeam.TeamType.NONE;
		player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1.f, 0.f);
	}
	
}
