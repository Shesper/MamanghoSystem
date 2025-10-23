package me.ministrie.enchantments;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import me.ministrie.enchantments.types.AntiGravityEnchant;
import me.ministrie.enchantments.types.DestructEnchant;
import me.ministrie.enchantments.types.DiggingSpeedEnchant;
import me.ministrie.enchantments.types.DwarfEnchant;
import me.ministrie.enchantments.types.EarplugEnchant;
import me.ministrie.enchantments.types.EternalBindingCurseEnchant;
import me.ministrie.enchantments.types.FrenzyEnchant;
import me.ministrie.enchantments.types.GeasGiftEnchant;
import me.ministrie.enchantments.types.GiantEnchant;
import me.ministrie.enchantments.types.GlowArrowEnchant;
import me.ministrie.enchantments.types.HealthBoostEnchant;
import me.ministrie.enchantments.types.HeavyArmorEnchant;
import me.ministrie.enchantments.types.HighStepEnchant;
import me.ministrie.enchantments.types.LightningStrikeEnchant;
import me.ministrie.enchantments.types.LongHandsEnchant;
import me.ministrie.enchantments.types.PoisonArrowEnchant;
import me.ministrie.enchantments.types.PowerDiggingEnchant;
import me.ministrie.enchantments.types.RobustEnchant;
import me.ministrie.enchantments.types.SprintEnchant;
import me.ministrie.enchantments.types.StoneableCurseEnchant;
import me.ministrie.enchantments.types.StrengthCurseEnchant;
import me.ministrie.enchantments.types.TemperEnchant;
import me.ministrie.enchantments.types.TimberEnchant;
import me.ministrie.enchantments.types.TwohandsEnchant;
import me.ministrie.enchantments.types.VoidCapeEnchant;
import me.ministrie.enchantments.types.WitherArrowEnchant;
import me.ministrie.enchantments.types.WitheringStingEnchant;
import me.ministrie.enchantments.types.WrathCurseEnchant;

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
		m.put(DiggingSpeedEnchant.key, new DiggingSpeedEnchant());
		m.put(StoneableCurseEnchant.key, new StoneableCurseEnchant());
		m.put(WrathCurseEnchant.key, new WrathCurseEnchant());
		m.put(EternalBindingCurseEnchant.key, new EternalBindingCurseEnchant());
		m.put(TwohandsEnchant.key, new TwohandsEnchant());
		m.put(TimberEnchant.key, new TimberEnchant());
		m.put(GiantEnchant.key, new GiantEnchant());
		m.put(DwarfEnchant.key, new DwarfEnchant());
		m.put(HeavyArmorEnchant.key, new HeavyArmorEnchant());
		m.put(TemperEnchant.key, new TemperEnchant());
		m.put(AntiGravityEnchant.key, new AntiGravityEnchant());
		m.put(FrenzyEnchant.key, new FrenzyEnchant());
		m.put(GeasGiftEnchant.key, new GeasGiftEnchant());
		m.put(LightningStrikeEnchant.key, new LightningStrikeEnchant());
		m.put(WitheringStingEnchant.key, new WitheringStingEnchant());
		m.put(EarplugEnchant.key, new EarplugEnchant());
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
	
	public static int getCurseEnchantCount(Player player){
		int count = 0;
		for(ItemStack armor : player.getInventory().getArmorContents()){
			if(armor == null) continue;
			for(Entry<Enchantment, Integer> e : armor.getEnchantments().entrySet()){
				if(e.getKey().isCursed()){
					count++;
				}else{
					CustomEnchantment custom = getEnchantment(e.getKey().getKey());
					if(custom != null && custom instanceof ICurse){
						count++;
					}
				}
			}
		}
		ItemStack hand = player.getInventory().getItemInMainHand();
		ItemStack offhand = player.getInventory().getItemInOffHand();
		if(hand != null){
			for(Entry<Enchantment, Integer> e : hand.getEnchantments().entrySet()){
				if(e.getKey().isCursed()){
					count++;
				}else{
					CustomEnchantment custom = getEnchantment(e.getKey().getKey());
					if(custom != null && custom instanceof ICurse){
						count++;
					}
				}
			}
		}
		if(offhand != null){
			for(Entry<Enchantment, Integer> e : offhand.getEnchantments().entrySet()){
				if(e.getKey().isCursed()){
					count++;
				}else{
					CustomEnchantment custom = getEnchantment(e.getKey().getKey());
					if(custom != null && custom instanceof ICurse){
						count++;
					}
				}
			}
		}
		return count;
	}
	
	public static boolean hasEnchantmentLevel(ItemStack item, NamespacedKey key){
		if(item == null || item.getItemMeta() == null) return false;
		CustomEnchantment findEnchant = getEnchantment(key);
		if(findEnchant == null) return false;
		for(Entry<Enchantment, Integer> e : item.getItemMeta().getEnchants().entrySet()){
			if(e.getKey().getKey().equals(findEnchant.getKey())){
				return true;
			}
		}
		return false;
	}
	
	public static int getEnchantmentLevel(ItemStack item, NamespacedKey key){
		if(item == null || item.getItemMeta() == null) return 0;
		CustomEnchantment findEnchant = getEnchantment(key);
		if(findEnchant == null) return 0;
		for(Entry<Enchantment, Integer> e : item.getItemMeta().getEnchants().entrySet()){
			if(e.getKey().getKey().equals(findEnchant.getKey())){
				return e.getValue();
			}
		}
		return 0;
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
