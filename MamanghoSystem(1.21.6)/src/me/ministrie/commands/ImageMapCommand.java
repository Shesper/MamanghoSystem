package me.ministrie.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ministrie.api.player.MamanghoPlayer;
import me.ministrie.gui.types.image.ImageFrameGui;

public class ImageMapCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String [] args){
		if(sender instanceof Player player){
			MamanghoPlayer user = MamanghoPlayer.getPlayer(player);
			if(user != null){
				ImageFrameGui g = new ImageFrameGui(user);
				g.open();
			}
		}
		return false;
	}
}
