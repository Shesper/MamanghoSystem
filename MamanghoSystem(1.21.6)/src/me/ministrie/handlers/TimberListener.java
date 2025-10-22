package me.ministrie.handlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.songoda.ultimatetimber.events.TreeFallEvent;

import me.ministrie.enchantments.EnchantmentFinder;
import me.ministrie.enchantments.types.TimberEnchant;

public class TimberListener implements Listener{
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onTreeFallEvent(TreeFallEvent event){
		ItemStack hand = event.getPlayer().getInventory().getItemInMainHand();
		if(hand == null) return;
		if(!EnchantmentFinder.hasEnchantmentLevel(hand, TimberEnchant.key)){
			event.setCancelled(true);
		}
	}
}
