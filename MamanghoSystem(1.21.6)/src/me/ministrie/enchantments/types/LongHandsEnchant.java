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

public class LongHandsEnchant extends CustomEnchantment{

	public static final NamespacedKey key = NamespacedKey.minecraft("longhands");
	
	@Override
	public NamespacedKey getKey(){
		return key;
	}

	@Override
	public EquipmentSlot[] getSlots(){
		return new EquipmentSlot[]{EquipmentSlot.CHEST};
	}
	
	@Override
	public void onEquip(Player player, EquipmentSlot slot, int level){
		AttributeInstance block_attribute = player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE);
		if(block_attribute.getModifier(key) == null){
			block_attribute.addModifier(new AttributeModifier(key, level, Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST));
		}
	}

	@Override
	public void onUnequip(Player player){
		this.onUnequip(player, null, 0);
	}

	@Override
	public void onUnequip(Player player, EquipmentSlot slot, int level){
		AttributeInstance block_attribute = player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE);
		block_attribute.removeModifier(key);
	}

	@Override
	public void onBroken(Player player, EquipmentSlot slot, int level){
		this.onUnequip(player, slot, level);
	}
}
