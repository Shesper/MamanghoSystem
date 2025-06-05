package me.ministrie.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ministrie.api.player.MamanghoPlayer;
import me.ministrie.gui.types.skin.SkinGui;

public class SkinCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String [] args){
		if(sender instanceof Player player){
			if(player.hasPermission("mamangho.skin.general")){
				MamanghoPlayer viewer = MamanghoPlayer.getPlayer(player);
				if(viewer != null){
					SkinGui g = new SkinGui(viewer);
					g.open();
					return true;
				}
			}
		}
		return false;
	}
}
