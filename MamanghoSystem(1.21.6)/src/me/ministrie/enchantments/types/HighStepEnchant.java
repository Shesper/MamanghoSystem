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

public class HighStepEnchant extends CustomEnchantment{

	public static final NamespacedKey key = NamespacedKey.minecraft("highstep");
	
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
		AttributeInstance attribute = player.getAttribute(Attribute.STEP_HEIGHT);
		if(attribute.getModifier(key) != null) return;
		attribute.addModifier(new AttributeModifier(key, (double)level * 0.5, Operation.ADD_NUMBER, EquipmentSlotGroup.FEET));
	}

	@Override
	public void onUnequip(Player player){
		this.onUnequip(player, null, 0);
	}

	@Override
	public void onUnequip(Player player, EquipmentSlot slot, int level){
		AttributeInstance attribute = player.getAttribute(Attribute.STEP_HEIGHT);
		attribute.removeModifier(key);
	}

	@Override
	public void onBroken(Player player, EquipmentSlot slot, int level){
		this.onUnequip(player, slot, level);
	}
}
