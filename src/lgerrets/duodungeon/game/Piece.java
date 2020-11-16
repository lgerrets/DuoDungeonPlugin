package lgerrets.duodungeon.game;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import lgerrets.duodungeon.ConfigManager;
import lgerrets.duodungeon.utils.Coords3d;
import lgerrets.duodungeon.utils.Index2d;
import lgerrets.duodungeon.utils.Index2d.Direction;

public class Piece {
	public enum TetrisShape
	{
		O,
		L,
		LR,
		Z,
		S,
		T,
		I,
	}
	
	public static Map<TetrisShape, int[][]> occupations = new HashMap<>();
	{
		int[][] tmp_o = {{1,1},
						 {1,1}};
		occupations.put(TetrisShape.O, tmp_o);
		int[][] tmp_lr = {{1,1},
				  		  {1,0},
				  		  {1,0}};
		occupations.put(TetrisShape.LR, tmp_lr);
		int[][] tmp_l = {{1,1},
				  		 {0,1},
				  		 {0,1}};
		occupations.put(TetrisShape.L, tmp_l);
		int[][] tmp_z = {{0,1},
				  		 {1,1},
				  		 {1,0}};
		occupations.put(TetrisShape.Z, tmp_z);
		int[][] tmp_s = {{1,0},
				  		 {1,1},
				  		 {0,1}};
		occupations.put(TetrisShape.S, tmp_s);
		int[][] tmp_t = {{1,0},
				  		 {1,1},
				  		 {1,0}};
		occupations.put(TetrisShape.T, tmp_t);
		int[][] tmp_i = {{1},{1},{1},{1}};
		occupations.put(TetrisShape.I, tmp_i);
	}
	
	public static Coords3d tetris_o_origin;
	{
		tetris_o_origin = new Coords3d();
		ConfigurationSection waypoints = ConfigManager.DDConfig.getConfigurationSection("Waypoints");
		ConfigurationSection tetris_o_section = waypoints.getConfigurationSection("tetris_o");
		tetris_o_origin.x = tetris_o_section.getInt("X");
		tetris_o_origin.y = tetris_o_section.getInt("Y");
		tetris_o_origin.z = tetris_o_section.getInt("Z");
	}
	
	
	private Location clone_from;
	private Location loc;
	private int rotation;
	TetrisShape shape;
	public Index2d[] map_occupation;
	
	public Piece()
	{
		
	}
	
	public Piece(Location template, TetrisShape tetris_shape)
	{
		clone_from = template;
		shape = tetris_shape;
	}
	
	public void SetMapOccupation(Index2d[] map_occupation_)
	{
		map_occupation = map_occupation_;
	}
}
