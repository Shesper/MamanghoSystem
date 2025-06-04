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

public class SprintEnchant extends CustomEnchantment{

	public static final NamespacedKey key = NamespacedKey.minecraft("sprint");
	
	@Override
	public NamespacedKey getKey(){
		return key;
	}

	@Override
	public EquipmentSlot[] getSlots(){
		return new EquipmentSlot[]{EquipmentSlot.FEET};
	}
	
	@Override
	public void onEquip(Player player, EquipmentSlot slot, int level){
		AttributeInstance attribute = player.getAttribute(Attribute.MOVEMENT_SPEED);
		if(attribute.getModifier(key) != null) return;
		attribute.addModifier(new AttributeModifier(key, 0.1*level, Operation.MULTIPLY_SCALAR_1, EquipmentSlotGroup.FEET));
	}

	@Override
	public void onUnequip(Player player){
		this.onUnequip(player, null, 0);
	}

	@Override
	public void onUnequip(Player player, EquipmentSlot slot, int level){
		AttributeInstance attribute = player.getAttribute(Attribute.MOVEMENT_SPEED);
		attribute.removeModifier(key);
	}

	@Override
	public void onBroken(Player player, EquipmentSlot slot, int level){
		this.onUnequip(player, slot, level);
	}
}
