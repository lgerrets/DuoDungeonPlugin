package lgerrets.duodungeon.game;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import lgerrets.duodungeon.ConfigManager;
import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.utils.Coords3d;
import lgerrets.duodungeon.utils.Index2d;
import lgerrets.duodungeon.utils.Index2d.Direction;
import lgerrets.duodungeon.utils.MyMath;

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
	static List<TetrisShape> all_shapes = Arrays.asList(TetrisShape.values());
	
	/*public static Map<TetrisShape, int[][]> occupations = new HashMap<>();
	static {
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
	}*/
	
	public static Map<TetrisShape, Index2d[]> occupations = new EnumMap<TetrisShape, Index2d[]>(TetrisShape.class);
	static {
		Index2d[] tmp_o = new Index2d[] {new Index2d(0,0), new Index2d(0,1), new Index2d(1,0), new Index2d(1,1)};
		occupations.put(TetrisShape.O, tmp_o);

		Index2d[] tmp_lr = new Index2d[] {new Index2d(0,0), new Index2d(0,1), new Index2d(1,0), new Index2d(0,2)};
		occupations.put(TetrisShape.LR, tmp_lr);

		Index2d[] tmp_l = new Index2d[] {new Index2d(0,0), new Index2d(1,0), new Index2d(1,1), new Index2d(1,2)};
		occupations.put(TetrisShape.L, tmp_l);

		Index2d[] tmp_z = new Index2d[] {new Index2d(0,1), new Index2d(1,0), new Index2d(1,1), new Index2d(0,2)};
		occupations.put(TetrisShape.Z, tmp_z);

		Index2d[] tmp_s = new Index2d[] {new Index2d(0,0), new Index2d(0,1), new Index2d(1,1), new Index2d(1,2)};
		occupations.put(TetrisShape.S, tmp_s);

		Index2d[] tmp_t = new Index2d[] {new Index2d(0,0), new Index2d(0,1), new Index2d(1,1), new Index2d(0,2)};
		occupations.put(TetrisShape.T, tmp_t);

		Index2d[] tmp_i = new Index2d[] {new Index2d(0,0), new Index2d(0,1), new Index2d(0,2), new Index2d(0,3)};
		occupations.put(TetrisShape.I, tmp_i);
	}
	
	private static Map<TetrisShape, Integer> n_templates = new HashMap<>();
	static {
		n_templates.put(TetrisShape.O, ConfigManager.DDConfig.getInt("o_pieces"));
		n_templates.put(TetrisShape.LR, ConfigManager.DDConfig.getInt("lr_pieces"));
		n_templates.put(TetrisShape.L, ConfigManager.DDConfig.getInt("l_pieces"));
		n_templates.put(TetrisShape.Z, ConfigManager.DDConfig.getInt("z_pieces"));
		n_templates.put(TetrisShape.S, ConfigManager.DDConfig.getInt("s_pieces"));
		n_templates.put(TetrisShape.T, ConfigManager.DDConfig.getInt("t_pieces"));
		n_templates.put(TetrisShape.I, ConfigManager.DDConfig.getInt("i_pieces"));
	}
	private static int template_separator = ConfigManager.DDConfig.getInt("piece_separation");
	
	private static ConfigurationSection waypoints = ConfigManager.DDConfig.getConfigurationSection("Waypoints");
	
	private static Map<TetrisShape, Coords3d> template_origins = new HashMap<>();
	static {
		template_origins.put(TetrisShape.O, Coords3d.FromWaypoint("tetris_o"));
		template_origins.put(TetrisShape.LR, Coords3d.FromWaypoint("tetris_lr"));
		template_origins.put(TetrisShape.L, Coords3d.FromWaypoint("tetris_l"));
		template_origins.put(TetrisShape.Z, Coords3d.FromWaypoint("tetris_z"));
		template_origins.put(TetrisShape.S, Coords3d.FromWaypoint("tetris_s"));
		template_origins.put(TetrisShape.T, Coords3d.FromWaypoint("tetris_t"));
		template_origins.put(TetrisShape.I, Coords3d.FromWaypoint("tetris_i"));
	}

	private int rotation;
	public TetrisShape shape;
	public Index2d[] map_occupation;
	public Index2d[] clone_from;
	public Index2d map_origin;
	private int rndTemplate;
	
	public Piece(TetrisShape tetris_shape, Index2d map_origin_)
	{
		map_origin = map_origin_;
		shape = tetris_shape;
		map_occupation = new Index2d[occupations.get(tetris_shape).length];
		for (int i=0; i<occupations.get(tetris_shape).length; i+=1)
			map_occupation[i] = occupations.get(tetris_shape)[i].add(map_origin);
		clone_from = occupations.get(shape);
		rndTemplate = MyMath.RandomUInt(n_templates.get(shape));
	}
	
	public Coords3d GetTemplateOrigin()
	{
		return template_origins.get(shape).add(0, 0, rndTemplate*template_separator);
	}
	
	public Piece() // OLD
	{
		
	}
	
	public void SetMapOccupation(Index2d[] map_occupation_) // OLD
	{
		map_occupation = map_occupation_;
	}
	
	public static Piece SpawnPiece(int[][] map)
	{
		TetrisShape shape = DrawTetrisShape();
		boolean found = false;
		Index2d idx = new Index2d(0,0);
		int tries = 0;
		while (!found)
		{
			tries += 1;
			if (tries > 100)
			{
				DuoDungeonPlugin.logg("Unable to place piece, dungeon is too full... Will likely crash!");
				return null;
			}
			idx.x = MyMath.RandomUInt(map.length);
			idx.z = MyMath.RandomUInt(map[0].length);
			found = MapIsFreeForTetrisShape(map, idx, shape);
		}
		DuoDungeonPlugin.logg("Spawning piece " + shape.toString());
		Piece piece = new Piece(shape, idx);
		return piece;
	}
	
	public int[][] InitUpdateMap(int[][] map)
	{
		for (Index2d idx : map_occupation)
			map[idx.x][idx.z] = 1;
		return map;
	}
	
	private static boolean MapIsFreeForTetrisShape(int[][] map, Index2d origin, TetrisShape shape_)
	{
		for (Index2d offset : occupations.get(shape_))
		{
			Index2d idx = offset.add(origin);
			if ((idx.x >= map.length) || (idx.z >= map[0].length))
				return false;
			if (map[idx.x][idx.z] > 0)
				return false;
		}
		return true;
	}
	
	private static TetrisShape DrawTetrisShape()
	{
		int idx = MyMath.RandomUInt(all_shapes.size());
		return all_shapes.get(idx);
	}
}
