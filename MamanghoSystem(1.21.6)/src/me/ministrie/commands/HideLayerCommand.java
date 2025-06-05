package me.ministrie.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.comphenix.protocol.ProtocolLibrary;

import me.ministrie.api.player.MamanghoPlayer;
import me.ministrie.configs.MessageSetting;
import me.ministrie.handlers.data.player.PlayerDataHandler.DataEnum;

public class HideLayerCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String [] args){
		if(sender instanceof Player player){
			MamanghoPlayer user = MamanghoPlayer.getPlayer(player);
			if(user != null){
				boolean toggle = user.getData().getData(DataEnum.HIDE_ARMOR_LAYER);
				if(user.getData().setData(DataEnum.HIDE_ARMOR_LAYER, !toggle)){
					player.updateInventory();
					ProtocolLibrary.getProtocolManager().updateEntity(player, player.getWorld().getPlayers());
					toggle = user.getData().getData(DataEnum.HIDE_ARMOR_LAYER);
					if(toggle){
						MessageSetting.SYSTEM_HIDE_LAYER_ON.sendMessages(user);
					}else{
						MessageSetting.SYSTEM_HIDE_LAYER_OFF.sendMessages(user);
					}
				}
			}
			return true;
		}
		return false;
	}
}
