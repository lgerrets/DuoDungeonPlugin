package lgerrets.duodungeon.players;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import lgerrets.duodungeon.DuoDungeonPlugin;

public class DuoTeammate {
	protected DuoPlayer player;
	protected DuoTeam.TeamType type;
	
	public DuoTeammate(DuoPlayer player) {
		this.player = player;
	}
	
	public void ResetTeammate()
	{
		player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1.f, 1.f);
		Player p = player.getPlayer();
		p.setHealth(player.getPlayer().getMaxHealth());
		for(PotionEffect effect : p.getActivePotionEffects())
			p.removePotionEffect(effect.getType());
		p.setFoodLevel(20);
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
