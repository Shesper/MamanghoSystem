package me.ministrie.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.ministrie.configs.IconSetting;
import me.ministrie.configs.MessageSetting;
import me.ministrie.configs.ServerSetting;
import me.ministrie.configs.SoundSetting;
import me.ministrie.main.MamanghoSystem;
import me.ministrie.utils.component.ComponentUtil;

public class AdminCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String [] args){
		if(sender.isOp()){
			if(args.length == 1){
				if(args[0].equalsIgnoreCase("reload")){
					ServerSetting.load();
					MessageSetting.load();
					IconSetting.load();
					SoundSetting.load();
					MamanghoSystem.getEmoticonManager().load();
					MamanghoSystem.getBiomeInformation().load();
					sender.sendMessage(ComponentUtil.parseComponent("MamanghoSystem 플러그인의 설정 정보가 로드되었습니다."));
					return true;
				}
			}
		}
		return false;
	}
}
