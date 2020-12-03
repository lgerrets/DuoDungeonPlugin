package lgerrets.duodungeon.game;

import java.util.ArrayList;
import java.util.EnumMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import lgerrets.duodungeon.utils.Coords3d;

public class DuoTeam {
	public enum TeamType
	{
		NONE,
		BUILDER,
		RUNNER,
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
	
	static public int HasEnchant(TeamType type, Enchantment ench)
	{
		int total_level = 0;
		for (DuoTeammate mate : all_players.get(type))
		{
			ItemStack[] equipments = mate.getDuoPlayer().getPlayer().getEquipment().getArmorContents();
			for (ItemStack equipment : equipments)
			{
				if (equipment != null && equipment.getItemMeta().hasEnchant(ench))
					total_level += equipment.getItemMeta().getEnchantLevel(ench);
			}
		}
		return total_level;
	}
	
	static void removePlayer(DuoPlayer p) {
		for (TeamType type : TeamType.values())
		{
			for (DuoTeammate teammate : all_players.get(type))
			{
				if (teammate.getDuoPlayer().equals(p))
				{
					teammate.Unregister();
					break;
				}
			}
		}
	}
	
	static ArrayList<DuoTeammate> GetAllPlayers()
	{
		ArrayList<DuoTeammate> ret = new ArrayList<DuoTeammate>();
		for (TeamType type : TeamType.values())
		{
			ret.addAll(all_players.get(type));
		}
		return ret;
	}
}
