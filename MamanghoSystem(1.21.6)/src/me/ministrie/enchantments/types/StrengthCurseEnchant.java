package me.ministrie.enchantments.types;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import me.ministrie.enchantments.CustomEnchantment;
import me.ministrie.enchantments.ICurse;

public class StrengthCurseEnchant extends CustomEnchantment implements ICurse{

	public static final NamespacedKey key = NamespacedKey.minecraft("strength_curse");
	
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
		if(victim instanceof LivingEntity){
			return damage * (1.0 + ((double)level * 0.1));
		}
		return damage;
	}
	
	@Override
	public double getReduceDamage(Player victim, double damage, int level){
		if(victim instanceof LivingEntity){
			return damage * (1.0 + ((double)level * 0.1));
		}
		return damage;
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
