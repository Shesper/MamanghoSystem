package me.ministrie.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.ministrie.configs.IconSetting;
import me.ministrie.configs.MessageSetting;
import me.ministrie.configs.ServerSetting;
import me.ministrie.configs.SoundSetting;
import me.ministrie.main.MamanghoSystem;
import me.ministrie.utils.ItemDBSave;
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
					MamanghoSystem.getSkinManager().load();
					MamanghoSystem.getModifier().update();
					sender.sendMessage(ComponentUtil.parseComponent("MamanghoSystem 플러그인의 설정 정보가 로드되었습니다."));
					return true;
				}else if(args[0].equalsIgnoreCase("dbsave")){
					if(sender instanceof Player){
						Player p = (Player) sender;
						ItemStack hand = p.getInventory().getItemInMainHand();
						if(hand != null && !hand.getType().equals(Material.AIR)){
							ItemDBSave.saveItem(hand);
							p.sendMessage("아이템 DB 저장완료.");
						}
					}
				}
			}else if(args.length == 2){
				if(args[0].equalsIgnoreCase("dbload")){
					if(sender instanceof Player){
						Player player = (Player) sender;
						String name = args[1];
						ItemStack item = ItemDBSave.loadItem(name);
						if(item != null){
							player.getInventory().addItem(item);
							player.sendMessage("DB에 저장된 아이템이 지급되었습니다.");
						}else{
							player.sendMessage(name + ".yml 파일이 존재하지 않습니다.");
						}
					}
				}
			}
		}
		return false;
	}
}
