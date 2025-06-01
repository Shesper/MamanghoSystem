package me.ministrie.managers;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.HashMap;

import me.ministrie.api.player.MamanghoPlayer;
import me.ministrie.functions.Callback;
import me.ministrie.handlers.player.MamanghoPlayerHandler;
import me.ministrie.main.MamanghoSystem;
import net.luckperms.api.model.user.User;

public class PlayerManager{

	private Plugin plugin;
	private Map<UUID, MamanghoPlayer> caches = new HashMap<>();
	
	public PlayerManager(Plugin plugin){
		this.plugin = plugin;
	}
	
	public Plugin getPlugin(){
		return plugin;
	}
	
	public void loadAndCache(Player player, Callback<MamanghoPlayer> callback){
		MamanghoPlayerHandler user = new MamanghoPlayerHandler(player);
		user.load(callback);
	}
	
	public void cachePlayer(MamanghoPlayer cache){
		this.caches.put(cache.getPlayer().getUniqueId(), cache);
		MamanghoSystem.getTabListUpdater().updateTabUser(cache);
	}
	
	public MamanghoPlayer removePlayer(Player player){
		return this.caches.remove(player.getUniqueId());
	}
	
	public void removePlayerShutdown(Player player){
		this.caches.remove(player.getUniqueId());
	}
	
	public MamanghoPlayer getPlayer(Player player){
		if(player == null) return null;
		return this.caches.get(player.getUniqueId());
	}
	
	public MamanghoPlayer getPlayer(UUID playeruuid){
		if(playeruuid == null) return null;
		return this.caches.get(playeruuid);
	}
	
	public MamanghoPlayer getPlayer(User user){
		if(user == null) return null;
		return this.caches.get(user.getUniqueId());
	}
}
