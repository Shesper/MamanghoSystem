package me.ministrie.enchantments.types;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.ministrie.enchantments.CustomEnchantment;
import me.ministrie.utils.MathUtil;

public class WitheringStingEnchant extends CustomEnchantment{

	public static final double TRIGGER_CHANCE = 0.2;
	public static final NamespacedKey key = NamespacedKey.minecraft("withering_sting");
	
	@Override
	public NamespacedKey getKey(){
		return key;
	}

	@Override
	public EquipmentSlot[] getSlots(){
		return new EquipmentSlot[]{EquipmentSlot.HAND};
	}
	
	@Override
	public double getIncreaseDamage(Player attacker, Entity victim, double damage, int level){
		if(victim instanceof LivingEntity target){
			if(MathUtil.percent(TRIGGER_CHANCE)){
				target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, MathUtil.getRandomInt(80, 140), 0));
			}
			if(target.hasPotionEffect(PotionEffectType.WITHER)){
				return damage * (1.0 + ((double)level * 0.25));
			}
		}
		return damage;
	}

	@Override
	public void onBroken(Player player, EquipmentSlot slot, int level){}
}
