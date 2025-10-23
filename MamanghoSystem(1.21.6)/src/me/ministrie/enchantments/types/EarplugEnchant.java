package me.ministrie.enchantments.types;

import org.bukkit.NamespacedKey;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import me.ministrie.enchantments.CustomEnchantment;

public class EarplugEnchant extends CustomEnchantment{

	public static final NamespacedKey key = NamespacedKey.minecraft("earplug");
	
	@Override
	public NamespacedKey getKey(){
		return key;
	}

	@Override
	public EquipmentSlot[] getSlots(){
		return new EquipmentSlot[]{EquipmentSlot.HEAD};
	}
	
	@Override
	public void onTrigger(Player player, EquipmentSlot slot, Object value, int level){
		if(value instanceof EntityDamageByEntityEvent event){
			if(event.getDamageSource().getDamageType().equals(DamageType.SONIC_BOOM)){
				double reduce_scale = 1.0 - (level * 0.125);
				event.setDamage(event.getDamage() * reduce_scale);
			}
		}
	}

	@Override
	public void onBroken(Player player, EquipmentSlot slot, int level){}
}
