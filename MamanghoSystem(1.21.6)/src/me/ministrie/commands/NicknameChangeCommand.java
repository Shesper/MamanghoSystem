package me.ministrie.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ministrie.api.player.MamanghoPlayer;
import me.ministrie.configs.MessageSetting;
import me.ministrie.configs.ServerSetting;
import me.ministrie.main.MamanghoSystem;
import me.ministrie.utils.component.ComponentUtil;
import me.ministrie.utils.string.StringUtils;
import net.md_5.bungee.api.ChatColor;

public class NicknameChangeCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String [] args){
		if(sender instanceof Player player){
			if(args.length != 1){
				MessageSetting.SYSTEM_NAMECHANGE_COMMAND_GUIDE.sendMessages(player);
				return false;
			}else{
				String nickname = StringUtils.stripColor(ChatColor.translateAlternateColorCodes('&', args[0]));
				int maxlength = ServerSetting.NICKNAME_CHANGE_MAX_LENGTH.<Integer>getValue();
				if(nickname.length() > maxlength){
					MessageSetting.SYSTEM_NAMECHANGE_OVER_LENGTH.sendMessages(player, maxlength);
					return false;
				}
				MamanghoPlayer user = MamanghoSystem.getPlayerManager().getPlayer(player);
				if(user != null){
					player.displayName(ComponentUtil.parseComponent(nickname));
					user.setCustomNickname(nickname);
					MessageSetting.SYSTEM_NAMECHANGE_CHANGED.sendMessages(player, nickname);
					MamanghoSystem.getTabListUpdater().updateTabUser(user);
					return true;
				}
			}
		}
		return false;
	}
}
