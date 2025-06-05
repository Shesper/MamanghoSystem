package me.ministrie.packet.protocol.listener;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.components.EquippableComponent;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;

import me.ministrie.api.data.player.PlayerData;
import me.ministrie.api.player.MamanghoPlayer;
import me.ministrie.handlers.data.player.PlayerDataHandler.DataEnum;
import me.ministrie.packet.protocol.ContainerLocation;
import me.ministrie.skins.BowSkin;
import me.ministrie.skins.WeaponSkin;
import me.ministrie.skins.WeaponType;

public class SkinListener extends PacketAdapter{

	public SkinListener(Plugin plugin){
		super(plugin, PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.WINDOW_ITEMS, PacketType.Play.Server.ENTITY_EQUIPMENT);
	}
	
	@Override
	public void onPacketSending(PacketEvent event){
		PacketContainer packet = event.getPacket();
		Player player = event.getPlayer();
		if(event.getPacketType().equals(PacketType.Play.Server.ENTITY_EQUIPMENT)){
			Entity entity = packet.getEntityModifier(event).read(0);
			if(entity instanceof Player target){
				MamanghoPlayer user = MamanghoPlayer.getPlayer(target);
				if(user == null) return;
				List<Pair<ItemSlot, ItemStack>> slots = packet.getSlotStackPairLists().read(0);
				if(!slots.isEmpty()){
					boolean hideLayer = user.getData().getData(DataEnum.HIDE_ARMOR_LAYER);
					for(Pair<ItemSlot, ItemStack> slot : slots){
						if(slot.getFirst().equals(ItemSlot.MAINHAND) || slot.getFirst().equals(ItemSlot.OFFHAND)){
							slot.setSecond(this.computeStack(user.getData(), slot.getSecond()));
						}else{
							if(hideLayer) slot.setSecond(this.hideLayerArmorStack(slot.getSecond()));
						}
					}
					packet.getSlotStackPairLists().write(0, slots);
				}
			}
		}else if(event.getPacketType().equals(PacketType.Play.Server.SET_SLOT)){
			if(player.getGameMode().equals(GameMode.CREATIVE)) return;
			MamanghoPlayer user = MamanghoPlayer.getPlayer(player);
			if(user == null) return;
			StructureModifier<ItemStack> sm = packet.getItemModifier();
			ItemStack item = sm.read(0);
			int containerID = packet.getIntegers().read(0);
			int slot = packet.getIntegers().read(2);
			boolean hideLayer = user.getData().getData(DataEnum.HIDE_ARMOR_LAYER);
			if(containerID != 0){
				Inventory viewInventory = player.getOpenInventory().getTopInventory();
				ContainerLocation loc = ContainerLocation.fromSlot(viewInventory.getSize(), slot);
				if(loc.equals(ContainerLocation.BOTTOM)){
					this.compute(user.getData(), item);
					if(hideLayer) this.hideLayerArmor(item);
				}
			}else{
				this.compute(user.getData(), item);
				if(hideLayer) this.hideLayerArmor(item);
			}
			
		}else if(event.getPacketType().equals(PacketType.Play.Server.WINDOW_ITEMS)){
			if(player.getGameMode().equals(GameMode.CREATIVE)) return;
			MamanghoPlayer user = MamanghoPlayer.getPlayer(player);
			if(user == null) return;
			StructureModifier<List<ItemStack>> sm = packet.getItemListModifier();
			if(sm.size() == 0) return;
			Inventory viewInventory = player.getOpenInventory().getTopInventory();
			List<ItemStack> copys = sm.read(0);
			int containerID = packet.getIntegers().read(0);
			boolean hideLayer = user.getData().getData(DataEnum.HIDE_ARMOR_LAYER);
			if(containerID != 0){
				int slot = 0;
				for(ItemStack item : copys){
					ContainerLocation loc = ContainerLocation.fromSlot(viewInventory.getSize(), slot);
					if(loc.equals(ContainerLocation.BOTTOM)){
						this.compute(user.getData(), item);
						if(hideLayer) this.hideLayerArmor(item);
					}
					slot++;
				}
			}else{
				for(ItemStack item : copys){
					this.compute(user.getData(), item);
					if(hideLayer) this.hideLayerArmor(item);
				}
			}
			packet.getItemListModifier().write(0, copys);
		}
	}
	
	private void compute(PlayerData data, ItemStack item){
		if(item == null || item.isEmpty()) return;
		WeaponSkin swordSkin = data.getData(DataEnum.SWORD_SKIN);
		WeaponSkin axeSkin = data.getData(DataEnum.AXE_SKIN);
		WeaponSkin maceSkin = data.getData(DataEnum.MACE_SKIN);
		WeaponSkin pickaxeSkin = data.getData(DataEnum.PICKAXE_SKIN);
		WeaponSkin shovelSkin = data.getData(DataEnum.SHOVEL_SKIN);
		WeaponSkin hoeSkin = data.getData(DataEnum.HOE_SKIN);
		WeaponSkin fishingrodSkin = data.getData(DataEnum.FISHING_ROD_SKIN);
		BowSkin bowSkin = data.getData(DataEnum.BOW_SKIN);
		BowSkin crossbowSkin = data.getData(DataEnum.CROSSBOW_SKIN);
		ItemMeta meta = item.getItemMeta();
		if(swordSkin != null && swordSkin.compatible(item, WeaponType.SWORD)){
			meta.setItemModel(swordSkin.getModelKey());
			item.setItemMeta(meta);
			return;
		}
		if(axeSkin != null && axeSkin.compatible(item, WeaponType.AXE)){
			meta.setItemModel(axeSkin.getModelKey());
			item.setItemMeta(meta);
			return;
		}
		if(maceSkin != null && maceSkin.compatible(item, WeaponType.MACE)){
			meta.setItemModel(maceSkin.getModelKey());
			item.setItemMeta(meta);
			return;
		}
		if(pickaxeSkin != null && pickaxeSkin.compatible(item, WeaponType.PICKAXE)){
			meta.setItemModel(pickaxeSkin.getModelKey());
			item.setItemMeta(meta);
			return;
		}
		if(shovelSkin != null && shovelSkin.compatible(item, WeaponType.SHOVEL)){
			meta.setItemModel(shovelSkin.getModelKey());
			item.setItemMeta(meta);
			return;
		}
		if(hoeSkin != null && hoeSkin.compatible(item, WeaponType.HOE)){
			meta.setItemModel(hoeSkin.getModelKey());
			item.setItemMeta(meta);
			return;
		}
		if(fishingrodSkin != null && fishingrodSkin.compatible(item, WeaponType.FISHING_ROD)){
			meta.setItemModel(fishingrodSkin.getModelKey());
			item.setItemMeta(meta);
			return;
		}
		if(bowSkin != null && bowSkin.compatible(item)){
			meta.setItemModel(bowSkin.getModelKey());
			item.setItemMeta(meta);
			return;
		}
		if(crossbowSkin != null && crossbowSkin.compatible(item)){
			meta.setItemModel(crossbowSkin.getModelKey());
			item.setItemMeta(meta);
			return;
		}
	}
	
	private ItemStack computeStack(PlayerData data, ItemStack item){
		if(item == null || item.isEmpty()) return item;
		WeaponSkin swordSkin = data.getData(DataEnum.SWORD_SKIN);
		WeaponSkin axeSkin = data.getData(DataEnum.AXE_SKIN);
		WeaponSkin maceSkin = data.getData(DataEnum.MACE_SKIN);
		WeaponSkin pickaxeSkin = data.getData(DataEnum.PICKAXE_SKIN);
		WeaponSkin shovelSkin = data.getData(DataEnum.SHOVEL_SKIN);
		WeaponSkin hoeSkin = data.getData(DataEnum.HOE_SKIN);
		WeaponSkin fishingrodSkin = data.getData(DataEnum.FISHING_ROD_SKIN);
		BowSkin bowSkin = data.getData(DataEnum.BOW_SKIN);
		BowSkin crossbowSkin = data.getData(DataEnum.CROSSBOW_SKIN);
		ItemMeta meta = item.getItemMeta();
		if(swordSkin != null && swordSkin.compatible(item, WeaponType.SWORD)){
			meta.setItemModel(swordSkin.getModelKey());
			item.setItemMeta(meta);
			return item;
		}
		if(axeSkin != null && axeSkin.compatible(item, WeaponType.AXE)){
			meta.setItemModel(axeSkin.getModelKey());
			item.setItemMeta(meta);
			return item;
		}
		if(maceSkin != null && maceSkin.compatible(item, WeaponType.MACE)){
			meta.setItemModel(maceSkin.getModelKey());
			item.setItemMeta(meta);
			return item;
		}
		if(pickaxeSkin != null && pickaxeSkin.compatible(item, WeaponType.PICKAXE)){
			meta.setItemModel(pickaxeSkin.getModelKey());
			item.setItemMeta(meta);
			return item;
		}
		if(shovelSkin != null && shovelSkin.compatible(item, WeaponType.SHOVEL)){
			meta.setItemModel(shovelSkin.getModelKey());
			item.setItemMeta(meta);
			return item;
		}
		if(hoeSkin != null && hoeSkin.compatible(item, WeaponType.HOE)){
			meta.setItemModel(hoeSkin.getModelKey());
			item.setItemMeta(meta);
			return item;
		}
		if(fishingrodSkin != null && fishingrodSkin.compatible(item, WeaponType.FISHING_ROD)){
			meta.setItemModel(fishingrodSkin.getModelKey());
			item.setItemMeta(meta);
			return item;
		}
		if(bowSkin != null && bowSkin.compatible(item)){
			meta.setItemModel(bowSkin.getModelKey());
			item.setItemMeta(meta);
			return item;
		}
		if(crossbowSkin != null && crossbowSkin.compatible(item)){
			meta.setItemModel(crossbowSkin.getModelKey());
			item.setItemMeta(meta);
			return item;
		}
		return item;
	}
	
	private void hideLayerArmor(ItemStack armor){
		if(armor == null || armor.isEmpty()) return;
		ItemMeta meta = armor.getItemMeta();
		if(!(meta instanceof ArmorMeta || meta instanceof LeatherArmorMeta)) return;
		EquippableComponent equipable = meta.getEquippable();
		NamespacedKey modelKey = equipable.getModel();
		if(modelKey != null){
			if(modelKey.toString().equals("elytrachestplate:netherite_elytra")){
				equipable.setModel(NamespacedKey.fromString("elytrachestplate:netherite_elytra_hide"));
			}
		}else{
			equipable.setModel(NamespacedKey.fromString("minecraft:hide_layer"));
		}
		meta.setEquippable(equipable);
		armor.setItemMeta(meta);
	}
	
	private ItemStack hideLayerArmorStack(ItemStack armor){
		if(armor == null || armor.isEmpty()) return armor;
		ItemMeta meta = armor.getItemMeta();
		if(!(meta instanceof ArmorMeta || meta instanceof LeatherArmorMeta)) return armor;
		EquippableComponent equipable = meta.getEquippable();
		NamespacedKey modelKey = equipable.getModel();
		if(modelKey != null){
			if(modelKey.toString().equals("elytrachestplate:netherite_elytra")){
				equipable.setModel(NamespacedKey.fromString("elytrachestplate:netherite_elytra_hide"));
			}
		}else{
			equipable.setModel(NamespacedKey.fromString("minecraft:hide_layer"));
		}
		meta.setEquippable(equipable);
		armor.setItemMeta(meta);
		return armor;
	}
}
