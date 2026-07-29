package me.ministrie.enchantments.types;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.ministrie.enchantments.CustomEnchantment;
import me.ministrie.utils.MathUtil;

public class GeasGiftEnchant extends CustomEnchantment{

	public static final double TRIGGER_CHANCE = 0.1;
	public static final NamespacedKey key = NamespacedKey.minecraft("geas_gift");
	
	@Override
	public NamespacedKey getKey(){
		return key;
	}

	@Override
	public EquipmentSlot[] getSlots(){
		return new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
	}

	@Override
	public void onTrigger(Player player, EquipmentSlot slot, Object value, int level){
		if(value == null) return;
		if(value instanceof EntityDamageEvent event){
			Entity entity = event.getEntity();
			if(player.getUniqueId().equals(entity.getUniqueId())){
				if(MathUtil.percent(TRIGGER_CHANCE)){
					int amp = level > 1 ? 1 : 0;
					int duration = MathUtil.getRandomInt(80, 140);
					player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, amp));
				}
			}
		}
	}
	
	@Override
	public void onBroken(Player player, EquipmentSlot slot, int level){}
}
