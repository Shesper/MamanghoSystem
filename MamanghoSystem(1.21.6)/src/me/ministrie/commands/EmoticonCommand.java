package me.ministrie.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ministrie.api.player.MamanghoPlayer;
import me.ministrie.gui.types.emoticon.EmoticonGui;

public class EmoticonCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String [] args){
		if(sender instanceof Player player){
			MamanghoPlayer user = MamanghoPlayer.getPlayer(player);
			if(user != null){
				EmoticonGui g = new EmoticonGui(user);
				g.open();
			}
			return true;
		}
		return false;
	}
}
