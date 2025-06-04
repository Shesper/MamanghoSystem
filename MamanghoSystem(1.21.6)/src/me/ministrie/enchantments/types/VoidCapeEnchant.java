package me.ministrie.enchantments.types;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.EquipmentSlot;

import me.ministrie.enchantments.CustomEnchantment;

public class VoidCapeEnchant extends CustomEnchantment{

	public static final NamespacedKey key = NamespacedKey.minecraft("voidcape");
	
	@Override
	public NamespacedKey getKey(){
		return key;
	}

	@Override
	public EquipmentSlot[] getSlots(){
		return new EquipmentSlot[]{EquipmentSlot.CHEST};
	}
	
	@Override
	public void onTrigger(Player player, EquipmentSlot slot, Object value, int level){
		if(value == null) return;
		if(value instanceof EntityDamageEvent event){
			if(event.getCause().equals(DamageCause.VOID)){
				double damage = event.getFinalDamage();
				double hp = player.getHealth();
				if(damage >= hp){
					event.setCancelled(true);
					player.setHealth(1);
					Location spawnpoint = player.getRespawnLocation();
					World world = Bukkit.getWorld("world");
					if(spawnpoint == null) spawnpoint = world.getSpawnLocation();
					player.setExperienceLevelAndProgress(0);
					player.setFallDistance(0);
					player.teleport(spawnpoint);
					player.playEffect(EntityEffect.PROTECTED_FROM_DEATH);
				}
			}
		}
	}
	
	@Override
	public void onEquip(Player player, EquipmentSlot slot, int level){}

	@Override
	public void onUnequip(Player player){}

	@Override
	public void onUnequip(Player player, EquipmentSlot slot, int level){}

	@Override
	public void onBroken(Player player, EquipmentSlot slot, int level){}
}
