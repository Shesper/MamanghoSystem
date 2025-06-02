package me.ministrie.managers;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;

import me.ministrie.api.player.MamanghoPlayer;

public class PlayerCooldownManager{

	public enum CooldownType{
		BIG_EMOTICON;
	}
	
	private MamanghoPlayer handler;
	private ConcurrentMap<CooldownType, Long> caches = Maps.newConcurrentMap();
	
	public PlayerCooldownManager(MamanghoPlayer handler){
		this.handler = handler;
	}
	
	public MamanghoPlayer getHandler(){
		return handler;
	}
	
	public boolean isCooldown(CooldownType type){
		return this.getCooldown(type) > System.currentTimeMillis();
	}
	
	public long getCooldown(CooldownType type){
		return this.caches.getOrDefault(type, 0L);
	}
	
	public void setCooldown(CooldownType type, long time){
		this.caches.put(type, time);
	}
	
	public long getRemainCooldownWithSeconds(CooldownType type){
		long current = this.getCooldown(type);
		if(current < System.currentTimeMillis()) return 0;
		return (current-System.currentTimeMillis()) / 1000;
	}
}
