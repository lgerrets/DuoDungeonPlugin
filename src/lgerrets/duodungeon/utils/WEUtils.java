package lgerrets.duodungeon.utils;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.block.BlockReplace;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;

public class WEUtils {
	static public void CopyRegion(com.sk89q.worldedit.world.World world, BlockVector3 posA0, BlockVector3 posA1, BlockVector3 posB0, boolean cut)
	{
		CuboidRegion region = new CuboidRegion(world, posA0, posA1);
		BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

		try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
		    ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
		        editSession, region, clipboard, region.getMinimumPoint()
		    );
		    // configure here
		    if (cut)
		    	forwardExtentCopy.setSourceFunction(new BlockReplace(editSession, BukkitAdapter.adapt(Material.AIR.createBlockData())));
		    Operations.complete(forwardExtentCopy);
		} catch (WorldEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
		    Operation operation = new ClipboardHolder(clipboard)
		            .createPaste(editSession)
		            .to(posB0)
		            // configure here
		            .build();
		    Operations.complete(operation);
		} catch (WorldEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static public void FillRegion(com.sk89q.worldedit.world.World world, CuboidRegion region, BlockData mat)
	{
		try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) { // get the edit session and use -1 for max blocks for no limit, this is a try with resources statement to ensure the edit session is closed after use
			editSession.setBlocks(region, BukkitAdapter.adapt(mat));
		} catch (WorldEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static public void FillRegionExcept(World world, Coords3d posA0, Coords3d posA1, Material mat, Material except)
	{
		Coords3d[] corners = Coords3d.CalculateExtremaCorners(posA0, posA1);
		for(int x=corners[0].x; x<corners[1].x; x+=1)
		{
			for(int y=corners[0].y; y<corners[1].y; y+=1)
			{
				for(int z=corners[0].z; z<corners[1].z; z+=1)
				{
					Block block = world.getBlockAt(x, y, z);
					if (block.getType() != except)
						block.setType(mat);
				}
			}
		}
	}
}
