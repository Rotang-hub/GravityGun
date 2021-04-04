package me.GravityGun.manager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.GravityGun.GravityGun;
import net.md_5.bungee.api.ChatColor;

public class Events implements Listener
{
private Plugin plugin = GravityGun.getPlugin(GravityGun.class);
	
	private Manager manager;
	
	public Events(Manager manager) 
	{
		this.manager = manager;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		Action action = event.getAction();
		ItemStack item = player.getInventory().getItemInMainHand();
		
		if(action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR))
		{
			if(item.equals(manager.getGun()))
			{
				if(event.getHand().equals(EquipmentSlot.HAND))
				{
					new BukkitRunnable() 
					{
						@Override
						public void run() 
						{
							manager.catchBlock(player, event.getClickedBlock());
						}
					}.runTaskLater(plugin, 2);
				}
			}
		}
			
		if(action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK))
		{
			manager.shotBlock(player);
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		
		manager.setDistance(player, 2.0);
		manager.setMode(player, true);
	}
	
	@EventHandler
	public void onSwapItem(PlayerSwapHandItemsEvent event)
	{
		Player player = event.getPlayer();
		ItemStack item = event.getOffHandItem();
		
		if(item.equals(manager.getGun()))
		{
			event.setCancelled(true);
			
			manager.setMode(player, !(manager.getMode(player)));
			
			if(manager.getMode(player))
			{
				player.sendMessage(ChatColor.GREEN + "Mode: Lengthen");
			}
			
			else
			{
				player.sendMessage(ChatColor.GREEN + "Mode: Shorten");
			}
		}
	}
	
	@EventHandler
	public void onPlayerSneak(PlayerToggleSneakEvent event)
	{
		Player player = event.getPlayer();
		
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				if(player.isSneaking())
				{
					if(manager.getMode(player))
						manager.increaseDistance(player);
					else
						manager.decreaseDistance(player);
				}
				else
					cancel();
			}
		}.runTaskTimer(plugin, 0, 1);
	}
}
