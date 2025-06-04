package me.ministrie.enchantments.types;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.ministrie.enchantments.CustomEnchantment;

public class PoisonArrowEnchant extends CustomEnchantment{

	public static final NamespacedKey key = NamespacedKey.minecraft("poison_arrow");
	
	@Override
	public NamespacedKey getKey(){
		return key;
	}

	@Override
	public EquipmentSlot[] getSlots(){
		return new EquipmentSlot[]{EquipmentSlot.HAND};
	}
	
	@Override
	public void onTrigger(Player player, EquipmentSlot slot, Object value, int level){
		if(value == null) return;
		if(value instanceof EntityShootBowEvent event){
			Entity projectile = event.getProjectile();
			if(projectile instanceof Arrow){
				Arrow arrow = (Arrow) projectile;
				float force = event.getForce();
				arrow.addCustomEffect(new PotionEffect(PotionEffectType.POISON, Float.valueOf(200.0f*force).intValue(), level-1), true);
				arrow.setPickupStatus(PickupStatus.DISALLOWED);
			}
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
