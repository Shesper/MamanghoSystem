package me.ministrie.enchantments.types;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;

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
	public void onEquip(Player player, EquipmentSlot slot, int level){
		AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);
		NamespacedKey slotKey = NamespacedKey.minecraft(KEY_FORMAT.formatted(slot.name().toLowerCase()));
		if(attribute.getModifier(slotKey) != null) return;
		attribute.addModifier(new AttributeModifier(slotKey, 0.5*level, Operation.ADD_NUMBER, EquipmentSlotGroup.ANY));
	}

	@Override
	public void onUnequip(Player player){
		for(EquipmentSlot slot : this.getSlots()){
			this.onUnequip(player, slot, 0);
		}
	}

	@Override
	public void onUnequip(Player player, EquipmentSlot slot, int level){
		AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);
		attribute.removeModifier(NamespacedKey.minecraft(KEY_FORMAT.formatted(slot.name().toLowerCase())));
	}

	@Override
	public void onBroken(Player player, EquipmentSlot slot, int level){
		this.onUnequip(player, slot, level);
	}
}
