package me.ministrie.skins.impl;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;

import me.ministrie.api.data.player.PlayerData;
import me.ministrie.api.player.MamanghoPlayer;
import me.ministrie.configs.IconSetting;
import me.ministrie.configs.MessageSetting;
import me.ministrie.handlers.data.player.PlayerDataHandler.DataEnum;
import me.ministrie.skins.BowSkin;
import me.ministrie.skins.ItemTarget;
import me.ministrie.skins.SkinSound;
import me.ministrie.skins.SkinType;
import me.ministrie.skins.WeaponSkin;
import me.ministrie.utils.string.StringUtils;
import net.md_5.bungee.api.ChatColor;

public class BowSkinImpl implements BowSkin{

	private int priority;
	private String uid;
	private NamespacedKey modelKey;
	private SkinSound shoot_sound;
	private SkinSound arrow_hit_sound;
	private ItemTarget target;
	private String name;
	private List<String> descriptions;
	
	public BowSkinImpl(YamlConfiguration config){
		this.priority = config.getInt("skin.priority", 0);
		this.uid = config.getString("skin.uid");
		this.target = ItemTarget.valueOf(config.getString("skin.item-target").toUpperCase());
		this.modelKey = NamespacedKey.fromString(config.getString("skin.model-key", "minecraft:empty"));
		this.shoot_sound = new SkinSound(config, "skin.sounds.shoot-sound");
		this.arrow_hit_sound = new SkinSound(config, "skin.sounds.arrow-hit-sound");
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
	public SkinSound getShootSound(){
		return shoot_sound;
	}

	@Override
	public SkinSound getArrowHitSound(){
		return arrow_hit_sound;
	}

	@Override
	public SkinType getSkinType(){
		return SkinType.BOW;
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
	public String getName(){
		return name;
	}

	@Override
	public List<String> getDescriptions(){
		return descriptions;
	}

	@Override
	public ItemStack getIcon(MamanghoPlayer viewer){
		PlayerData data = viewer.getData();
		BowSkin bowSkin = data.getData(DataEnum.BOW_SKIN);
		BowSkin crossbowSkin = data.getData(DataEnum.CROSSBOW_SKIN);
		String bow = bowSkin != null ? bowSkin.getName() : MessageSetting.FORMAT_SKIN_EMPTY.getValue();
		String crossbow = crossbowSkin != null ? crossbowSkin.getName() : MessageSetting.FORMAT_SKIN_EMPTY.getValue();
		List<String> lore = this.target.equals(ItemTarget.BOW) ? MessageSetting.FORMAT_SKIN_BOW.getList(bow)
				: this.target.equals(ItemTarget.CROSSBOW) ? MessageSetting.FORMAT_SKIN_CROSSBOW.getList(crossbow) : Lists.newArrayList();
		ItemStack icon = IconSetting.SKIN_ICON_TEMPLATE.getIconInfo().toIcon(this.getName(), this.getDescriptions(), lore);
		ItemMeta meta = icon.getItemMeta();
		meta.setItemModel(modelKey);
		icon.setItemMeta(meta);
		return icon;
	}

	@Override
	public boolean compatible(ItemStack item){
		if(item == null || item.isEmpty()) return false;
		return this.target.equals(ItemTarget.BOW) ? item.getType().equals(Material.BOW) 
				: this.target.equals(ItemTarget.CROSSBOW) ? item.getType().equals(Material.CROSSBOW) : false;
	}
}
