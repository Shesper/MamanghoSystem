package me.ministrie.managers;

import java.util.UUID;
import java.util.WeakHashMap;
import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

public class DamageTickController{

	private WeakHashMap<UUID, Map<UUID, Long>> caches = new WeakHashMap<>();
	private WeakHashMap<UUID, Long> tick_players = new WeakHashMap<>();
	
	public DamageTickController(){}
	
	public boolean isVictimPlayerTick(Player victim){
		return this.getVictimPlayerTick(victim) > System.currentTimeMillis();
	}
	
	public long getVictimPlayerTick(Player victim){
		return this.tick_players.getOrDefault(victim.getUniqueId(), 0L);
	}
	
	public void damageTickForMonster(Player victim, long tick){
		this.tick_players.put(victim.getUniqueId(), System.currentTimeMillis()+tick);
	}
	
	public boolean isDamageTicking(Player attacker, Entity victim){
		return this.getRemainTick(attacker, victim) > System.currentTimeMillis();
	}
	
	public long getRemainTick(Player attacker, Entity victim){
		Map<UUID, Long> map = this.caches.get(attacker.getUniqueId());
		if(map == null) return 0;
		return map.getOrDefault(victim.getUniqueId(), 0L);
	}
	
	public void setRemainTick(Player attacker, Entity victim, long tick){
		Map<UUID, Long> map = this.caches.get(attacker.getUniqueId());
		if(map != null){
			map.put(victim.getUniqueId(), System.currentTimeMillis()+tick);
			return;
		}
		map = Maps.newHashMap();
		map.put(victim.getUniqueId(), System.currentTimeMillis()+tick);
		this.caches.put(attacker.getUniqueId(), map);
	}
	
	public void damageTicking(Player attacker, Entity victim, long tick){
		this.setRemainTick(attacker, victim, tick);
	}
	
	public void unregister(Player player){
		this.caches.remove(player.getUniqueId());
	}
}
