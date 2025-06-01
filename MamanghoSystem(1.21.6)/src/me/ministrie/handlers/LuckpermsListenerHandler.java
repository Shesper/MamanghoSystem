package me.ministrie.handlers;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import me.ministrie.api.player.MamanghoPlayer;
import me.ministrie.main.MamanghoSystem;
import me.ministrie.packet.protocol.ProtocolTools;
import me.ministrie.thread.ThreadUtils;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;

public class LuckpermsListenerHandler{
	
	private static LuckpermsListenerHandler singleton;
	
	private final Plugin plugin;
	private final LuckPerms lp_provider;
	
	public LuckpermsListenerHandler(Plugin plugin, LuckPerms lp){
		this.plugin = plugin;
		this.lp_provider = lp;
		EventBus eventbus = this.lp_provider.getEventBus();
		eventbus.subscribe(this.plugin, UserDataRecalculateEvent.class, this::onUserDataRecalculateEvent);
		Bukkit.getLogger().log(Level.INFO, "[MamanghoSystem] luckperm plugin event hook.");
	}
	
	private void onUserDataRecalculateEvent(UserDataRecalculateEvent event){
		User user = event.getUser();
		if(user != null){
			Group group = lp_provider.getGroupManager().getGroup(user.getPrimaryGroup());
			if(group != null){
				MamanghoPlayer player = MamanghoSystem.getPlayerManager().getPlayer(user);
				if(player != null){
					ThreadUtils.runThreadSafe((v) -> player.updateNameVisually());
				}
			}
		}
	}
	
	public static void start(){
		if(singleton != null) throw new RuntimeException("already started listener");
		singleton = new LuckpermsListenerHandler(MamanghoSystem.getInstance(), LuckPermsProvider.get());
	}
}
