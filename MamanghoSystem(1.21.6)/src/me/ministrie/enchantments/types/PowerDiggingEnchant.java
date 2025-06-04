package me.ministrie.enchantments.types;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;

import me.ministrie.enchantments.CustomEnchantment;
import me.ministrie.enchantments.skills.SyncBlockBreak;
import me.ministrie.enchantments.skills.SyncBlockBreakShovel;
import me.ministrie.utils.MathUtil;

public class PowerDiggingEnchant extends CustomEnchantment{

	public static final NamespacedKey key = NamespacedKey.minecraft("power_digging");
	
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
		if(value instanceof BlockBreakEvent event){
			if(MathUtil.percent((double)level * 0.2)){
				PlayerInventory inv = player.getInventory();
				boolean isPickaxe = inv.getItemInMainHand() != null && inv.getItemInMainHand().getType().name().contains("_PICKAXE");
				boolean isShovel = inv.getItemInMainHand() != null && inv.getItemInMainHand().getType().name().contains("_SHOVEL");
				if(isPickaxe){
					SyncBlockBreak.run(player, event.getBlock().getLocation(), inv.getItemInMainHand());
				}else if(isShovel){
					SyncBlockBreakShovel.run(player, event.getBlock().getLocation(), inv.getItemInMainHand());
				}
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
