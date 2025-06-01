package me.ministrie.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ministrie.api.player.MamanghoPlayer;
import me.ministrie.configs.MessageSetting;
import me.ministrie.handlers.data.player.PlayerDataHandler.DataEnum;
import me.ministrie.main.MamanghoSystem;

public class ShareLocationCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String [] args){
		if(sender instanceof Player player){
			MamanghoPlayer user = MamanghoSystem.getPlayerManager().getPlayer(player);
			if(user != null){
				user.getData().setData(DataEnum.SHARE_LOCATION, !user.getData().<Boolean>getData(DataEnum.SHARE_LOCATION));
				boolean enable = user.getData().getData(DataEnum.SHARE_LOCATION);
				player.sendMessage(enable ? MessageSetting.SYSTEM_SHARE_LOCATION_ENABLE.getComponent() : 
					MessageSetting.SYSTEM_SHARE_LOCATION_DISABLE.getComponent());
				return true;
			}
		}
		return false;
	}
}
