package me.ministrie.skins.impl;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.ministrie.api.data.player.PlayerData;
import me.ministrie.api.player.MamanghoPlayer;
import me.ministrie.configs.IconSetting;
import me.ministrie.configs.MessageSetting;
import me.ministrie.handlers.data.player.PlayerDataHandler.DataEnum;
import me.ministrie.skins.ItemTarget;
import me.ministrie.skins.SkinSound;
import me.ministrie.skins.SkinType;
import me.ministrie.skins.WeaponSkin;
import me.ministrie.skins.WeaponType;
import me.ministrie.utils.string.StringUtils;
import net.md_5.bungee.api.ChatColor;

public class WeaponSkinImpl implements WeaponSkin{

	private int priority;
	private String uid;
	private NamespacedKey modelKey;
	private SkinSound sound;
	private ItemTarget target;
	private String name;
	private List<String> descriptions;
	
	public WeaponSkinImpl(YamlConfiguration config){
		this.priority = config.getInt("skin.priority", 0);
		this.uid = config.getString("skin.uid");
		this.target = ItemTarget.valueOf(config.getString("skin.item-target").toUpperCase());
		this.modelKey = NamespacedKey.fromString(config.getString("skin.model-key", "minecraft:empty"));
		this.sound = new SkinSound(config, "skin.sounds.hit-sound");
		this.name = ChatColor.translateAlternateColorCodes('&', config.getString("skin.name", ""));
		this.descriptions = StringUtils.translateColorCodes(config.getStringList("skin.descriptions"));
	}
	
	@Override
	public String getUID(){
		return uid;
	}
	
	@Override
	public NamespacedKey getModelKey(){
		return modelKey;
	}

	@Override
	public SkinSound getHitSound(){
		return sound;
	}

	@Override
	public SkinType getSkinType(){
		return SkinType.MELEE_WEAPON;
	}

	@Override
	public ItemTarget getItemTarget(){
		return target;
	}

	@Override
	public int getPriority(){
		return priority;
	}

	@Override
	public ItemStack getIcon(MamanghoPlayer viewer){
		PlayerData data = viewer.getData();
		WeaponSkin swordSkin = data.getData(DataEnum.SWORD_SKIN);
		WeaponSkin axeSkin = data.getData(DataEnum.AXE_SKIN);
		WeaponSkin maceSkin = data.getData(DataEnum.MACE_SKIN);
		WeaponSkin pickaxeSkin = data.getData(DataEnum.PICKAXE_SKIN);
		WeaponSkin shovelSkin = data.getData(DataEnum.SHOVEL_SKIN);
		WeaponSkin hoeSkin = data.getData(DataEnum.HOE_SKIN);
		WeaponSkin fishingrodSkin = data.getData(DataEnum.FISHING_ROD_SKIN);
		String sword = swordSkin != null ? swordSkin.getName() : MessageSetting.FORMAT_SKIN_EMPTY.getValue();
		String axe = axeSkin != null ? axeSkin.getName() : MessageSetting.FORMAT_SKIN_EMPTY.getValue();
		String mace = maceSkin != null ? maceSkin.getName() : MessageSetting.FORMAT_SKIN_EMPTY.getValue();
		String pickaxe = pickaxeSkin != null ? pickaxeSkin.getName() : MessageSetting.FORMAT_SKIN_EMPTY.getValue();
		String shovel = shovelSkin != null ? shovelSkin.getName() : MessageSetting.FORMAT_SKIN_EMPTY.getValue();
		String hoe = hoeSkin != null ? hoeSkin.getName() : MessageSetting.FORMAT_SKIN_EMPTY.getValue();
		String fishingrod = fishingrodSkin != null ? fishingrodSkin.getName() : MessageSetting.FORMAT_SKIN_EMPTY.getValue();
		List<String> lore = MessageSetting.FORMAT_SKIN_MELEE.getList(sword, axe, mace, pickaxe, shovel, hoe, fishingrod);
		ItemStack icon = IconSetting.SKIN_ICON_TEMPLATE.getIconInfo().toIcon(this.getName(), this.getDescriptions(), lore);
		ItemMeta meta = icon.getItemMeta();
		meta.setItemModel(modelKey);
		icon.setItemMeta(meta);
		return icon;
	}

	@Override
	public String getName(){
		return name;
	}

	@Override
	public List<String> getDescriptions(){
		return descriptions;
	}

	@Override
	public boolean compatible(ItemStack item){
		return false;
	}
	
	@Override
	public boolean compatible(ItemStack item, WeaponType type){
		if(item == null || item.isEmpty()) return false;
		switch(type){
			case AXE:
				return item.getType().name().contains("_AXE");
			case MACE:
				return item.getType().equals(Material.MACE);
			case SWORD:
				return item.getType().name().contains("_SWORD");
			case PICKAXE:
				return item.getType().name().contains("_PICKAXE");
			case SHOVEL:
				return item.getType().name().contains("_SHOVEL");
			case HOE:
				return item.getType().name().contains("_HOE");
			case FISHING_ROD:
				return item.getType().equals(Material.FISHING_ROD);
			default:
				return false;
		}
	}
}
