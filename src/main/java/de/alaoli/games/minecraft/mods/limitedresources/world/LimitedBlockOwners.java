package de.alaoli.games.minecraft.mods.limitedresources.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class LimitedBlockOwners extends WorldSavedData
{
	public final static String ID = "de.alaoli.games.minecraft.mods.limitedresources.world.limitedblockowners";
	
	public LimitedBlockOwners() 
	{
		super( LimitedBlockOwners.ID );
	}

	@Override
	public void readFromNBT(NBTTagCompound p_76184_1_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeToNBT(NBTTagCompound p_76187_1_) {
		// TODO Auto-generated method stub
		
	}

	public static LimitedBlockOwners get( World world )
	{
		if( world.mapStorage == null )
		{
			return null;
		}
		LimitedBlockOwners data = (LimitedBlockOwners) world.mapStorage.loadData( LimitedBlockOwners.class, LimitedBlockOwners.ID );
		
		//Initialize if null
		if( data == null )
		{
			data = new LimitedBlockOwners();
			data.markDirty();
			world.mapStorage.setData( LimitedBlockOwners.ID, data );
		}
		return data;
	}
}
