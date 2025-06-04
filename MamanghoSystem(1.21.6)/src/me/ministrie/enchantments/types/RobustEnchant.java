package me.ministrie.enchantments.types;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.EquipmentSlot;

import me.ministrie.enchantments.CustomEnchantment;

public class RobustEnchant extends CustomEnchantment{

	public static final NamespacedKey key = NamespacedKey.minecraft("robust");
	
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
		if(value instanceof FoodLevelChangeEvent event){
			if(event.getItem() == null) return;
			int newFood = event.getFoodLevel();
			int oldFood = event.getEntity().getFoodLevel();
			if(oldFood < newFood){
				Double result = ((double)(newFood-oldFood)) * (1.0+(0.2*(double)level));
				int r_level = result.intValue();
				int result_lv =oldFood + r_level;
				int food = result_lv < 0 ? 0 : result_lv;
				event.setFoodLevel(food);
			}
		}else if(value instanceof EntityRegainHealthEvent event){
			double scale = 1.0+(((double)level)*0.1);
			event.setAmount(event.getAmount()*scale);
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
