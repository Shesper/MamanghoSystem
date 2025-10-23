package me.ministrie.enchantments.types;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import me.ministrie.enchantments.CustomEnchantment;

public class TimberEnchant extends CustomEnchantment{

	public static final NamespacedKey key = NamespacedKey.minecraft("timber");
	
	@Override
	public NamespacedKey getKey(){
		return key;
	}

	@Override
	public EquipmentSlot[] getSlots(){
		return new EquipmentSlot[]{EquipmentSlot.HAND};
	}

	@Override
	public void onBroken(Player player, EquipmentSlot slot, int level){}
}
