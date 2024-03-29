package lgerrets.duodungeon.players;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.utils.Coords3d;

public class DuoPlayer
{	
	private static final Map<UUID, DuoPlayer> players = new ConcurrentHashMap<UUID,DuoPlayer>();
		
	public static DuoPlayer getPlayer(UUID id)
	{
		return players.get(id);
	}
	
	public static Collection<DuoPlayer> getPlayers()
	{
		return players.values();
	}
	
	public static void RegisterListener(final Plugin p)
	{
		players.clear();
		PlayerLoader l = new PlayerLoader();
		Bukkit.getPluginManager().registerEvents(l, p);
	}
	
	private static class PlayerLoader implements Listener
	{
		public PlayerLoader()
		{
			//If any players are online when this is called (hint: /reload), we load their DuoPlayer
			for(Player p : Bukkit.getOnlinePlayers())
			{
				loadPlayer(p);
			}
		}
		
		@EventHandler(priority = EventPriority.LOWEST)
		public void playerCheck(PlayerJoinEvent event)
		{
			final Player p = event.getPlayer();
			if(!exists(p))
				loadPlayer(p);
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void playerQuits(PlayerQuitEvent event)
		{
			checkLeave(event.getPlayer());
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void playerKicked(PlayerKickEvent event)
		{
			checkLeave(event.getPlayer());
		}
		
		private void checkLeave(Player player)
		{
			DuoPlayer p = getPlayer(player.getUniqueId());
			if(p != null)
			{
				DuoTeam.removePlayer(p);
			}
			players.remove(player.getUniqueId());
		}
		
		private boolean exists(final Player p)
		{
			return players.containsKey(p.getUniqueId());
		}
		
		private void loadPlayer(final Player p)
		{
			final DuoPlayer player = new DuoPlayer(p.getUniqueId(), p.getName());
			players.put(p.getUniqueId(), player);
		}
	}
	
	private final UUID id;
	private final String name;
	
	private Map<Object,Object> data;
	private DuoTeam team;
	
	private DuoPlayer(UUID ID, String Name)
	{
		this.id = ID;
		this.name = Name;
		DuoDungeonPlugin.logg(DuoTeam.teams);
		this.setTeam(DuoTeam.teams.get(DuoTeam.TeamType.NONE));
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public UUID getID()
	{
		return this.id;
	}
	
	public Object getData(Object key)
	{
		if(data == null)
			return null;
		return data.get(key);
	}
	
	public void setData(Object key, Object value)
	{
		if(data == null)
			data = new HashMap<Object,Object>();
		data.put(key, value);
	}
	
	public void setTeam(DuoTeam t)
	{
		Player p = this.getPlayer();
		if (p == null)
			return;
		
		if (this.team == t)
		{
			p.sendMessage("You are already in team " + t.teamType.toString() + "!");
			return;
		}
		p.sendMessage(t.color + "You are now in team " + t.teamType.toString());
		
		DuoTeam.removePlayer(this);
		if (t.teamType == DuoTeam.TeamType.BUILDER)
		{
			DuoTeam.builder_players.add(new DuoBuilder(this));
		}
		else if (t.teamType == DuoTeam.TeamType.RUNNER)
		{
			DuoTeam.runner_players.add(new DuoRunner(this));
		}
		else
		{
			DuoTeam.none_players.add(new DuoNoteamer(this));
		}
		this.team = t;
		
		String playerName = this.name;
		String name = playerName.length() > 14 ? playerName.substring(0, 14) : playerName;
		try 
		{
			p.setPlayerListName(t.color + name);
		}
		catch(IllegalArgumentException e1) 
		{
			Random rnd = new Random();
			name = (name.length() > 11 ? name.substring(0, 11) : name) + "" + rnd.nextInt(9);
			try 
			{
				p.setPlayerListName(t.color + name);
			}
			catch(IllegalArgumentException e2) 
			{
				DuoDungeonPlugin.logg("setPlayerListName error: " + e2.getMessage());
			}
		}
	}
	
	public DuoTeam getTeam()
	{
		return this.team;
	}
	
	public void sendMessage(String message)
	{
		Player p =  Bukkit.getPlayer(id);
		if(p != null)
			p.sendMessage(message);
	}
	
	public Player getPlayer()
	{
		return Bukkit.getPlayer(this.id);
	}
	
	public boolean isOnline()
	{
		return getPlayer() != null;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof DuoPlayer)
		{
			DuoPlayer p = (DuoPlayer)obj;
			return this.id.equals(p.id);
		}
		else return false;
	}
}