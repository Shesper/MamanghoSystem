package me.ministrie.enchantments.types;

import org.bukkit.NamespacedKey;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import me.ministrie.enchantments.CustomEnchantment;

public class HealthBoostEnchant extends CustomEnchantment{

	public static final NamespacedKey key = NamespacedKey.minecraft("healthboost");
	public static final String KEY_FORMAT = "healthboost_%s";
	
	@Override
	public NamespacedKey getKey(){
		return key;
	}

	@Override
	public EquipmentSlot[] getSlots(){
		return new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
	}

	@Override
	public void onBroken(Player player, EquipmentSlot slot, int level){}
}
