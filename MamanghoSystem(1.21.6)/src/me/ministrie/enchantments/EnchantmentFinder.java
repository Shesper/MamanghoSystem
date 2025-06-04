package me.ministrie.enchantments;

import java.util.List;
import java.util.Map;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import me.ministrie.enchantments.types.DestructEnchant;
import me.ministrie.enchantments.types.GlowArrowEnchant;
import me.ministrie.enchantments.types.HealthBoostEnchant;
import me.ministrie.enchantments.types.HighStepEnchant;
import me.ministrie.enchantments.types.LongHandsEnchant;
import me.ministrie.enchantments.types.PoisonArrowEnchant;
import me.ministrie.enchantments.types.PowerDiggingEnchant;
import me.ministrie.enchantments.types.RobustEnchant;
import me.ministrie.enchantments.types.SprintEnchant;
import me.ministrie.enchantments.types.StrengthCurseEnchant;
import me.ministrie.enchantments.types.VoidCapeEnchant;
import me.ministrie.enchantments.types.WitherArrowEnchant;

public class EnchantmentFinder{

	private static final ImmutableList<CustomEnchantment> EMPTY_LIST = ImmutableList.of();
	private static final ImmutableMap<NamespacedKey, CustomEnchantment> registry;
	private static final ImmutableMap<EquipmentSlot, ImmutableList<CustomEnchantment>> categories;
	
	static{
		Map<NamespacedKey, CustomEnchantment> m = Maps.newHashMap();
		m.put(SprintEnchant.key, new SprintEnchant());
		m.put(RobustEnchant.key, new RobustEnchant());
		m.put(PowerDiggingEnchant.key, new PowerDiggingEnchant());
		m.put(HighStepEnchant.key, new HighStepEnchant());
		m.put(LongHandsEnchant.key, new LongHandsEnchant());
		m.put(StrengthCurseEnchant.key, new StrengthCurseEnchant());
		m.put(PoisonArrowEnchant.key, new PoisonArrowEnchant());
		m.put(WitherArrowEnchant.key, new WitherArrowEnchant());
		m.put(GlowArrowEnchant.key, new GlowArrowEnchant());
		m.put(HealthBoostEnchant.key, new HealthBoostEnchant());
		m.put(DestructEnchant.key, new DestructEnchant());
		m.put(VoidCapeEnchant.key, new VoidCapeEnchant());
		registry = ImmutableMap.copyOf(m);
		Map<EquipmentSlot, List<CustomEnchantment>> c = Maps.newHashMap();
		registry.forEach((k, v) -> {
			for(EquipmentSlot slot : v.getSlots()){
				List<CustomEnchantment> list = c.get(slot);
				if(list != null){
					list.add(v);
				}else{
					list = Lists.newArrayList();
					list.add(v);
					c.put(slot, list);
				}
			}
		});
		Map<EquipmentSlot, ImmutableList<CustomEnchantment>> freeze = Maps.newHashMap();
		c.forEach((k, v) -> {
			freeze.put(k, ImmutableList.copyOf(v));
		});
		categories = ImmutableMap.copyOf(freeze);
	}
	
	public static ImmutableMap<NamespacedKey, CustomEnchantment> getEnchantments(){
		return registry;
	}
	
	public static List<CustomEnchantment> fromCategory(EquipmentSlot slot){
		return categories.getOrDefault(slot, EMPTY_LIST);
	}
	
	public static List<CustomEnchantment> fromCategories(List<EquipmentSlot> slots){
		List<CustomEnchantment> list = Lists.newArrayList();
		for(EquipmentSlot slot : slots){
			List<CustomEnchantment> find = fromCategory(slot);
			if(find.isEmpty()) continue;
			list.addAll(find);
		}
		return list;
	}
	
	public static CustomEnchantment getEnchantment(NamespacedKey key){
		return registry.get(key);
	}
	
	public static Map<CustomEnchantment, Integer> findEnchantments(ItemStack item){
		Map<CustomEnchantment, Integer> r = Maps.newHashMap();
		if(item == null) return r;
		item.getEnchantments().forEach((k, v) -> {
			CustomEnchantment custom = getEnchantment(k.getKey());
			if(custom != null) r.put(custom, v);
		});
		return r;
	}
}
