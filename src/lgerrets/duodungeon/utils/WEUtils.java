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
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;

import lgerrets.duodungeon.DuoDungeonPlugin;
import lgerrets.duodungeon.game.DuoMap;

public class WEUtils {
	static public void CopyRegion(com.sk89q.worldedit.world.World world, BlockVector3 posA0, BlockVector3 posA1, BlockVector3 posB0, boolean cut, int rotate)
	{
		if(rotate > 1)
		{
			for (int i=1; i<rotate; i+=1)
				CopyRegion(world, posA0, posA1, posA0, cut, 1); // copy in-place
			CopyRegion(world, posA0, posA1, posB0, cut, 1); // ... and finally to the destination
		}
		else if (rotate < -1)
		{
			for (int i=1; i<-rotate; i+=1)
				CopyRegion(world, posA0, posA1, posA0, cut, -1); // copy in-place
			CopyRegion(world, posA0, posA1, posB0, cut, -1); // ... and finally to the destination
		}
		else {
			CuboidRegion region = new CuboidRegion(world, posA0, posA1);
			BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
	
			try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
			    ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
			        editSession, region, clipboard, region.getMinimumPoint()
			    );
			    // configure here
			    if (cut)
			    {
			    	forwardExtentCopy.setSourceFunction(new BlockReplace(editSession, BukkitAdapter.adapt(Material.AIR.createBlockData())));
			    	forwardExtentCopy.setRemovingEntities(true);
			    }
			    Operations.complete(forwardExtentCopy);
			} catch (WorldEditException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
				ClipboardHolder holder = new ClipboardHolder(clipboard);
	
			    if (rotate != 0) // should be -1 or 0 or 1 (1 for positive orientation)
			    {
				    AffineTransform transform = new AffineTransform();
				    transform = transform.rotateY(rotate*90);
				    holder.setTransform(holder.getTransform().combine(transform));
				    DuoDungeonPlugin.logg("try rotate");
				    posB0 = posB0.add(0, 0, DuoMap.tile_size-1);
			    }
	            Operation operation = holder.createPaste(editSession)
					            .to(posB0)
					            // configure here
					            .build();
	
			    Operations.complete(operation);
			} catch (WorldEditException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
						world.getBlockAt(x, y, z).setType(mat);
				}
			}
		}
	}
}
