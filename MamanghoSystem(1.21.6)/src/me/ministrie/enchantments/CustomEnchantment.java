package me.ministrie.enchantments;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

public abstract class CustomEnchantment{
	
	public abstract NamespacedKey getKey();
	
	public abstract EquipmentSlot[] getSlots();
	
	public abstract void onEquip(Player player, EquipmentSlot slot, int level);
	
	public abstract void onUnequip(Player player);
	
	public abstract void onUnequip(Player player, EquipmentSlot slot, int level);
	
	public abstract void onBroken(Player player, EquipmentSlot slot, int level);
	
	public void onTrigger(Player player, EquipmentSlot slot, Object value, int level){}
	
	public double getIncreaseDamage(Player attacker, Entity victim, double damage, int level){ return damage; }
	
	public double getReduceDamage(Player victim, double damage, int level){ return damage; }
	
	public boolean matcher(Enchantment enchantment){
		return enchantment.getKey().equals(this.getKey());
	}
}
