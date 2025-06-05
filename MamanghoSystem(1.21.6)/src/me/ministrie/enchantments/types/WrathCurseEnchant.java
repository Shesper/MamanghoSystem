package me.ministrie.enchantments.types;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;

import me.ministrie.enchantments.CustomEnchantment;

public class WrathCurseEnchant extends CustomEnchantment{

	public static final NamespacedKey key = NamespacedKey.minecraft("wrath_curse");
	
	@Override
	public NamespacedKey getKey(){
		return key;
	}

	@Override
	public EquipmentSlot[] getSlots(){
		return new EquipmentSlot[]{EquipmentSlot.LEGS};
	}
	
	@Override
	public void onTrigger(Player player, EquipmentSlot slot, Object value, int level){
		if(value == null) return;
		if(value instanceof EntityShootBowEvent event){
			Entity projectile = event.getProjectile();
			if(projectile instanceof Arrow){
				Arrow arrow = (Arrow) projectile;
				arrow.setDamage(arrow.getDamage() * (1.0 + ((double)level * 0.25)));
			}
		}
	}
	
	@Override
	public double getIncreaseDamage(Player attacker, Entity victim, double damage, int level){
		if(victim instanceof LivingEntity){
			return damage * (1.0 + ((double)level * 0.25));
		}
		return damage;
	}
	
	@Override
	public void onEquip(Player player, EquipmentSlot slot, int level){
		AttributeInstance attribute = player.getAttribute(Attribute.BURNING_TIME);
		if(attribute.getModifier(key) != null) return;
		attribute.addModifier(new AttributeModifier(key, Integer.MAX_VALUE, Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS));
	}

	@Override
	public void onUnequip(Player player){
		this.onUnequip(player, null, 0);
	}

	@Override
	public void onUnequip(Player player, EquipmentSlot slot, int level){
		AttributeInstance attribute = player.getAttribute(Attribute.BURNING_TIME);
		attribute.removeModifier(key);
	}

	@Override
	public void onBroken(Player player, EquipmentSlot slot, int level){
		this.onUnequip(player, slot, level);
	}
}
