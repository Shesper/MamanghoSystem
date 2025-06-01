package me.ministrie.schedulers;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.ministrie.api.player.MamanghoPlayer;
import me.ministrie.configs.MessageSetting;
import me.ministrie.main.MamanghoSystem;

public class UserTabListUpdater{

	private BukkitTask task;
	
	public UserTabListUpdater(){
		this.start();
	}
	
	public void stop(){
		if(task != null) task.cancel();
		task = null;
	}
	
	public void start(){
		this.stop();
		task = new BukkitRunnable(){
			@Override
			public void run(){
				Bukkit.getOnlinePlayers().forEach(user -> {
					MamanghoPlayer player = MamanghoSystem.getPlayerManager().getPlayer(user);
					if(player != null){
						updateTabUser(player);
					}
				});
			}
		}.runTaskTimer(MamanghoSystem.getInstance(), 100, 100);
	}
	
	public void updateTabUser(MamanghoPlayer player){
		int ping = player.getPlayer().getPing();
		String color = ping < 150 ? MessageSetting.CHAT_TABLIST_NORMAL_PING_COLOR.getValue() : MessageSetting.CHAT_TABLIST_WARNING_PING_COLOR.getValue();
		player.getPlayer().playerListName(MessageSetting.CHAT_TABLIST_FORMAT.getComponent(player.getNickname(), player.getPrefix(), ping, color));
	}
}
