package me.ministrie.enchantments.types;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EquipmentSlot;

import me.ministrie.enchantments.CustomEnchantment;
import me.ministrie.enchantments.EnchantmentFinder;
import me.ministrie.enchantments.ICurse;

public class FrenzyEnchant extends CustomEnchantment implements ICurse{

	public static final NamespacedKey key = NamespacedKey.minecraft("frenzy");
	
	@Override
	public NamespacedKey getKey(){
		return key;
	}

	@Override
	public EquipmentSlot[] getSlots(){
		return new EquipmentSlot[]{EquipmentSlot.HAND};
	}
	
	@Override
	public double getIncreaseDamage(Player attacker, Entity victim, double damage, int level){
		double multiplier = EnchantmentFinder.getCurseEnchantCount(attacker);
		if(multiplier > 0){
			return damage * (1.0 + (multiplier * 0.05));
		}
		return damage;
	}
	
	@Override
	public void onTrigger(Player player, EquipmentSlot slot, Object value, int level){
		if(value instanceof EntityShootBowEvent event){
			if(event.getProjectile() instanceof Arrow arrow){
				double multiplier = EnchantmentFinder.getCurseEnchantCount(player);
				if(multiplier > 0){
					arrow.setDamage(arrow.getDamage() * (1.0 + (multiplier * 0.05)));
				}
			}
		}else if(value instanceof PlayerItemDamageEvent event){
			event.setDamage(event.getDamage() * 3);
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
