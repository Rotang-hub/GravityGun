package me.GravityGun.manager;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.GravityGun.GravityGun;
import net.md_5.bungee.api.ChatColor;

public class Manager 
{
	private Plugin plugin = GravityGun.getPlugin(GravityGun.class);
	
	private Map<Player, FallingBlock> blockMap = new HashMap<>();
	private Map<Player, Double> distMap = new HashMap<>();
	private Map<Player, Boolean> modMap = new HashMap<>();
	
	public Manager() 
	{
		plugin.getServer().getPluginManager().registerEvents(new Events(this), plugin);
		
		for(Player p : plugin.getServer().getOnlinePlayers())
		{
			setDistance(p, 2.0);
			setMode(p, true);
		}
	}

	public ItemStack getGun() 
	{
		ItemStack gun = new ItemStack(Material.BLAZE_POWDER);
		ItemMeta meta = gun.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD + "Gravity Gun");
		gun.setItemMeta(meta);
		
		return gun;
	}

	//=======================================
	
	public void catchBlock(Player player, Block block) 
	{
		if(blockMap.get(player) != null)
		{
			releaseBlock(player);
			return;
		}
		
		if(block == null)
			return;
		
		BlockData data = block.getBlockData();
		block.setType(Material.AIR);
		
		FallingBlock fBlock = player.getWorld().spawnFallingBlock(block.getLocation().add(0.5, 0.05, 0.5), data);
		fBlock.setGravity(false);
		fBlock.setInvulnerable(true);
		//fBlock.setDropItem(false);
		fBlock.setHurtEntities(true);
		blockMap.put(player, fBlock);
		
		setDistance(player, 2.0);
		
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				if(blockMap.get(player) == null)
					cancel();
				
				else if(!(player.getInventory().getItemInMainHand().equals(getGun())))
				{
					fBlock.setGravity(true);
					blockMap.put(player, null);
					cancel();
				}
				
				else if(fBlock.isDead())
				{
					blockMap.put(player, null);
					cancel();
				}
				
				else
				{
					Location pos = player.getEyeLocation().add(player.getLocation().getDirection().multiply(getDistance(player)));
					Vector vec = pos.toVector().subtract(fBlock.getLocation().toVector()).multiply(0.4);
						
					fBlock.setVelocity(vec);
				}
			}
		}.runTaskTimer(plugin, 0, 1);
	}
	
	public void releaseBlock(Player player)
	{
		FallingBlock block = blockMap.get(player);
		
		if(block == null)
			return;
		
		block.setGravity(true);
		blockMap.put(player, null);
	}
	
	public void shotBlock(Player player)
	{
		if(blockMap.get(player) == null)
			return;
		
		FallingBlock block = blockMap.get(player);
		Vector vec = player.getLocation().getDirection().multiply(2);
		
		block.setGravity(true);
		blockMap.put(player, null);
		block.setVelocity(vec);
	}
	
	//=======================================
	
	public Double getDistance(Player player)
	{
		return distMap.get(player);
	}
	
	public void setDistance(Player player, double dist)
	{
		distMap.put(player, dist);
	}
	
	public void increaseDistance(Player player)
	{
		double dist = getDistance(player);
		
		if(dist <= 10)
			dist = dist + 0.2;
		
		setDistance(player, dist);
	}
	
	public void decreaseDistance(Player player)
	{
		double dist = getDistance(player);
		
		if(dist >= 1)
			dist = dist - 0.2;
		
		setDistance(player, dist);
	}
	
	//=======================================
	
	public boolean getMode(Player player)
	{
		return modMap.get(player);
	}
	
	public void setMode(Player player, boolean mod)
	{
		modMap.put(player, mod);
	}
}
