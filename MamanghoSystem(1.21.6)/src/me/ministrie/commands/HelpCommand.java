package me.ministrie.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ministrie.configs.MessageSetting;

public class HelpCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String [] args){
		if(sender instanceof Player player){
			MessageSetting.SYSTEM_JOIN_MESSAGES.sendMessages(player);
			return true;
		}
		return false;
	}
}
