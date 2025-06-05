package me.ministrie.handlers.data.player;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.ministrie.api.data.player.PlayerData;
import me.ministrie.handlers.data.player.PlayerDataHandler.DataEnum;
import me.ministrie.skins.BowSkin;
import me.ministrie.skins.WeaponSkin;
import me.ministrie.skins.WeaponType;

public class SkinModule{

	private PlayerData data;
	
	public SkinModule(PlayerData data){
		this.data = data;
	}
	
	public WeaponSkin getMeleeWeaponSkin(ItemStack item){
		if(item == null || item.isEmpty()) return null;
		if(WeaponType.SWORD.match(item)){
			return data.getData(DataEnum.SWORD_SKIN);
		}else if(WeaponType.AXE.match(item)){
			return data.getData(DataEnum.AXE_SKIN);
		}else if(WeaponType.MACE.match(item)){
			return data.getData(DataEnum.MACE_SKIN);
		}else if(WeaponType.PICKAXE.match(item)){
			return data.getData(DataEnum.PICKAXE_SKIN);
		}else if(WeaponType.SHOVEL.match(item)){
			return data.getData(DataEnum.SHOVEL_SKIN);
		}else if(WeaponType.HOE.match(item)){
			return data.getData(DataEnum.HOE_SKIN);
		}else if(WeaponType.FISHING_ROD.match(item)){
			return data.getData(DataEnum.FISHING_ROD_SKIN);
		}
		return null;
	}
	
	public BowSkin getBowSkin(ItemStack bow){
		if(bow == null || bow.isEmpty()) return null;
		if(bow.getType().equals(Material.BOW)){
			return data.getData(DataEnum.BOW_SKIN);
		}else if(bow.getType().equals(Material.CROSSBOW)){
			return data.getData(DataEnum.CROSSBOW_SKIN);
		}
		return null;
	}
}
