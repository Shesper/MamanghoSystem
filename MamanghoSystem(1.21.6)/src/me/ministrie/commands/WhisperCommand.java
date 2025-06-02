package me.ministrie.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import me.ministrie.api.player.MamanghoPlayer;
import me.ministrie.utils.component.ComponentUtil;
import me.ministrie.utils.string.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class WhisperCommand implements CommandExecutor, TabCompleter{

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String [] args){
		if(s instanceof Player user){
			if(args.length >= 2){
				Player target = Bukkit.getPlayer(args[0]);
				if(target == null){
					user.sendMessage(ComponentUtil.translatiableFrom("argument.player.unknown").color(TextColor.fromHexString("#e04031")));
					return false;
				}
				MamanghoPlayer sender = MamanghoPlayer.getPlayer(user);
				MamanghoPlayer receiver = MamanghoPlayer.getPlayer(target);
				if(sender != null && receiver != null){
					Component messages = Component.text(StringUtils.mergeString(Arrays.asList(args).subList(1, args.length).toArray(new String[args.length-1])));
					user.sendMessage(ComponentUtil.translatiableFrom("commands.message.display.outgoing", receiver.getDisplaynameWithPrefix("#969393"), messages).color(TextColor.fromHexString("#969393")).decorate(TextDecoration.ITALIC));
					target.sendMessage(ComponentUtil.translatiableFrom("commands.message.display.incoming", sender.getDisplaynameWithPrefix("#969393"), messages).color(TextColor.fromHexString("#969393")).decorate(TextDecoration.ITALIC));
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public @Nullable List<String> onTabComplete(CommandSender sender, Command cmd, String label, String [] args){
		if(args.length == 1){
			return Bukkit.getOnlinePlayers().stream().map(e -> e.getName()).toList();
		}
		return null;
	}
}
