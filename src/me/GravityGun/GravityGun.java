package me.GravityGun;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.GravityGun.manager.Manager;
import net.md_5.bungee.api.ChatColor;

public class GravityGun extends JavaPlugin
{
	private Manager manager;
	
	@Override
	public void onEnable()
	{
		getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "Gravity Gun 플러그인 활성화");
		
		if(manager != null)
			return;
		manager = new Manager();
	}
	
	@Override
	public void onDisable()
	{
		getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "Gravity Gun 플러그인 비활성화");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		Player player = (Player) sender;
		
		if(label.equalsIgnoreCase("gravitygun"))
		{
			player.getInventory().addItem(manager.getGun());
		}
		
		return false;
	}
}
