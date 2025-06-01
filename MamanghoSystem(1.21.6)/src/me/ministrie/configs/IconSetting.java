package me.ministrie.configs;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Function;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

import me.ministrie.utils.InstantIcon;
import me.ministrie.utils.Pair;
import me.ministrie.utils.component.ComponentUtil;
import me.ministrie.utils.string.StringUtils;

public enum IconSetting{

	GUI_PAGEABLE_BLACKLINE("gui.general.pageable-blackline", "FEATHER:1", " ", "", true),
	GUI_PREVIOUS_BUTTON("gui.general.previous-button", "FEATHER:2", "&f이전 페이지", ""),
	GUI_NEXT_BUTTON("gui.general.next-button", "FEATHER:3", "&f다음 페이지", ""),
	GUI_PAGE_BUTTON("gui.general.page-button", "FEATHER:4", "&f{0} / {1}", ""),
	GUI_EMOTICON_BOOKMARK_BUTTON("gui.general.emoticon-bookmark-button", "FEATHER:5", "&f북마크한 디시콘 보기", ""),
	EMOTICON_ICON("emoticon.icon", "FEATHER:-1", "{0}", "{1}|&f|&f|&f|&f|&f|&f|{2}"),
	EMOTICON_DETAIL_ICON_UNBOOKMARK("emoticon.detail-icon-unbookmarked", "FEATHER:-1", "{0}", "{1}|&f|&f|&f|&f|&f|&f|{2}|&f좌 클릭: &b이모티콘 사용|&f우 클릭: &a즐겨찾기 추가|&fSHIFT+좌클릭: &6더블 콘 사용"),
	EMOTICON_DETAIL_ICON_BOOKMARKED("emoticon.detail-icon-bookmark", "FEATHER:-1", "{0}", "{1}|&f|&f|&f|&f|&f|&f|{2}|&f좌 클릭: &b이모티콘 사용|&f우 클릭: &c즐겨찾기 제거|&fSHIFT+좌클릭: &6더블 콘 사용"),
	
	EMOTICON_DETAIL_ICON_UNBOOKMARK_HOOKED("emoticon.detail-icon-unbookmarked-hooked", "FEATHER:-1", "{0}", "{1}|&f|&f|&f|&f|&f|&f|{2}|&f좌 클릭: &b이모티콘 사용|&f우 클릭: &a즐겨찾기 추가|&f|&f더블 콘 첫번 째 선택 중(&a다음 아이콘을 SHIFT+우클릭&f)|{3}|&f|&f|&f|&f|&f"),
	EMOTICON_DETAIL_ICON_BOOKMARKED_HOOKED("emoticon.detail-icon-bookmark-hooked", "FEATHER:-1", "{0}", "{1}|&f|&f|&f|&f|&f|&f|{2}|&f좌 클릭: &b이모티콘 사용|&f우 클릭: &c즐겨찾기 제거|&f|&f더블 콘 첫번 째 선택 중(&a다음 아이콘을 SHIFT+우클릭&f)|{3}|&f|&f|&f|&f|&f");
	
	public static final NamespacedKey DUMMY_ATTIRUBTE_MODIFIER = NamespacedKey.minecraft("dummy_attribute_modifier");
	public static final Pair<Attribute, AttributeModifier> DUMMY_ATTRIBUTE = Pair.of(Attribute.LUCK, new AttributeModifier(DUMMY_ATTIRUBTE_MODIFIER, 0.0, Operation.ADD_NUMBER));
	public static final NamespacedKey EMPTY_KEY = NamespacedKey.minecraft("empty_component");
	
	public static final String FILENAME = "./plugins/MamanghoSystem/icons.yml";
	public static YamlConfiguration config = null;
	public static Map<IconSetting, IconInfo> cache_values = new HashMap<>();

	private String path;
	private String def_icon;
	private String def_title;
	private List<String> def_lore;
	private NamespacedKey tooltipKey;
	private boolean hideTooltip;
	
	IconSetting(String path, String icon, String def, String def_lore){
		this.path = path;
		this.def_icon = icon;
		this.def_title = def;
		this.def_lore = StringUtils.deserializeString(def_lore, false);
		this.tooltipKey = NamespacedKey.minecraft("empty_component");
		this.hideTooltip = false;
	}
	
	IconSetting(String path, String icon, String def, String def_lore, boolean hideTooltip){
		this.path = path;
		this.def_icon = icon;
		this.def_title = def;
		this.def_lore = StringUtils.deserializeString(def_lore, false);
		this.tooltipKey = NamespacedKey.minecraft("empty_component");
		this.hideTooltip = hideTooltip;
	}
	
	public String getPath(){
		return path;
	}
	
	public String getDefaultIconFormat(){
		return this.def_icon;
	}
	
	public String getDefaultTitle(){
		return this.def_title;
	}
	
	public List<String> getDefaultPlainLore(){
		return this.def_lore;
	}
	
	public NamespacedKey getDefaultTooltipKey(){
		return this.tooltipKey;
	}
	
	public Boolean getDefaultHideTooltip(){
		return this.hideTooltip;
	}
	
	public IconInfo getIconInfo(){
		return cache_values.get(this);
	}
	
	@SuppressWarnings("unchecked")
	public static void load(){
		cache_values.clear();
		File file = new File(FILENAME);
		boolean save = false;
		config = YamlConfiguration.loadConfiguration(file);
		for(IconSetting setting : IconSetting.values()){
			Map<IconSet, Object> memory = new HashMap<>();
			for(IconSet key : IconSet.values()){
				if(!key.exception()){
					if(!config.isSet(key.getKey(setting))){
						config.set(key.getKey(setting), key.getValue(setting));
						memory.put(key, key.getValue(setting));
						save = true;
					}else{
						memory.put(key, key.getConfig(setting));
					}
				}else{
					if(!config.isSet(key.getKey(setting))){
						config.set(key.getKey(setting), key.reversion(key.getValue(setting)));
						memory.put(key, key.getValue(setting));
						save = true;
					}else{
						memory.put(key, key.migrate(key.getConfig(setting)));
					}
				}
			}
			cache_values.put(setting, IconInfo.of(memory.get(IconSet.ICON).toString(), memory.get(IconSet.TITLE).toString(),
					(List<String>)memory.get(IconSet.LORE), (NamespacedKey)memory.get(IconSet.TOOLTIP), (Boolean)memory.get(IconSet.HIDE_TOOLTIP)));
		}
		if(save){
			try{
				config.save(file);
			}catch (IOException e){
				e.printStackTrace();
			}
		}
	}
	
	public static class IconInfo{
		
		private Material type;
		private int modeldata = -1;
		private String icon_format;
		private String title;
		private List<String> lore;
		private NamespacedKey tooltipKey;
		private boolean hide_tooltip;
		
		public IconInfo(String icon, String title, List<String> lore, NamespacedKey tooltipKey, boolean hide_tooltip){
			this.icon_format = icon;
			this.tooltipKey = tooltipKey;
			String[] sp = this.icon_format.split(":");
			if(sp.length == 2){
				this.type = Material.getMaterial(sp[0].toUpperCase());
				if(this.type == null || this.type.equals(Material.AIR)) this.type = Material.BARRIER;
				this.modeldata = Integer.parseInt(sp[1]);
			}else{
				this.type = Material.BARRIER;
			}
			this.title = title;
			this.lore = StringUtils.translateColorCodes(lore);
			this.hide_tooltip = hide_tooltip;
		}
		
		public Material getType(){
			return type;
		}
		
		public int getModelData(){
			return modeldata;
		}
		
		public String getIconFormat(){
			return icon_format;
		}
		
		public String getTitle(){
			return title;
		}
		
		public List<String> getLore(){
			return lore;
		}
		
		public NamespacedKey getTooltipKey(){
			return this.tooltipKey;
		}
		
		public boolean isHideTooltip(){
			return this.hide_tooltip;
		}
		
		public ItemStack toIcon(Object... o){
			try{
				ItemStack icon = new ItemStack(type);
				ItemMeta meta = icon.getItemMeta();
				if(meta == null) return icon;
				meta.addAttributeModifier(DUMMY_ATTRIBUTE.getKey(), DUMMY_ATTRIBUTE.getValue());
				meta.addItemFlags(ItemFlag.values());
				if(this.tooltipKey != null && !this.tooltipKey.equals(EMPTY_KEY)) meta.setTooltipStyle(tooltipKey);
				
				if(title != null && !title.isEmpty()){
					meta.displayName(ComponentUtil.parseComponent(StringUtils.regexString(title, o)));
				}else{
					meta.displayName(ComponentUtil.parseComponent(" "));
				}
				
				if(lore != null && !lore.isEmpty()){
					meta.lore(ComponentUtil.parseComponents(StringUtils.regexList(lore, o)));
				}
				if(modeldata >= 0){
					CustomModelDataComponent component = meta.getCustomModelDataComponent();
					List<Float> floats = new ArrayList<>();
					floats.add(Integer.valueOf(modeldata).floatValue());
					component.setFloats(floats);
					meta.setCustomModelDataComponent(component);
				}
				if(this.hide_tooltip) meta.setHideTooltip(true);
				icon.setItemMeta(meta);
				return icon;
				
			}catch(Exception e){
				ItemStack icon = new ItemStack(Material.BARRIER);
				ItemMeta meta = icon.getItemMeta();
				meta.addAttributeModifier(DUMMY_ATTRIBUTE.getKey(), DUMMY_ATTRIBUTE.getValue());
				meta.addItemFlags(ItemFlag.values());
				if(this.tooltipKey != null && !this.tooltipKey.equals(EMPTY_KEY)) meta.setTooltipStyle(tooltipKey);
				
				if(title != null && !title.isEmpty()){
					meta.displayName(ComponentUtil.parseComponent(StringUtils.regexString(title, o)));
				}else{
					meta.displayName(ComponentUtil.parseComponent(" "));
				}
				
				if(lore != null && !lore.isEmpty()){
					meta.lore(ComponentUtil.parseComponents(StringUtils.regexList(lore, o)));
				}
				icon.setItemMeta(meta);
				return icon;
			}
		}
		
		public ItemStack toOverrideIcon(InstantIcon instant, Object... o){
			try{
				ItemStack icon = new ItemStack(instant.getType());
				ItemMeta meta = icon.getItemMeta();
				if(meta == null) return icon;
				meta.addAttributeModifier(DUMMY_ATTRIBUTE.getKey(), DUMMY_ATTRIBUTE.getValue());
				meta.addItemFlags(ItemFlag.values());
				if(this.tooltipKey != null && !this.tooltipKey.equals(EMPTY_KEY)) meta.setTooltipStyle(tooltipKey);
				
				if(title != null && !title.isEmpty()){
					meta.displayName(ComponentUtil.parseComponent(StringUtils.regexString(title, o)));
				}else{
					meta.displayName(ComponentUtil.parseComponent(" "));
				}
				
				if(lore != null && !lore.isEmpty()){
					meta.lore(ComponentUtil.parseComponents(StringUtils.regexList(lore, o)));
				}
				if(instant.getModelData() >= 0){
					CustomModelDataComponent component = meta.getCustomModelDataComponent();
					List<Float> floats = new ArrayList<>();
					floats.add(Integer.valueOf(instant.getModelData()).floatValue());
					component.setFloats(floats);
					meta.setCustomModelDataComponent(component);
				}
				if(this.hide_tooltip) meta.setHideTooltip(true);
				icon.setItemMeta(meta);
				return icon;
				
			}catch(Exception e){
				ItemStack icon = new ItemStack(Material.BARRIER);
				ItemMeta meta = icon.getItemMeta();
				meta.addAttributeModifier(DUMMY_ATTRIBUTE.getKey(), DUMMY_ATTRIBUTE.getValue());
				meta.addItemFlags(ItemFlag.values());
				if(this.tooltipKey != null && !this.tooltipKey.equals(EMPTY_KEY)) meta.setTooltipStyle(tooltipKey);
				
				if(title != null && !title.isEmpty()){
					meta.displayName(ComponentUtil.parseComponent(StringUtils.regexString(title, o)));
				}else{
					meta.displayName(ComponentUtil.parseComponent(" "));
				}
				
				if(lore != null && !lore.isEmpty()){
					meta.lore(ComponentUtil.parseComponents(StringUtils.regexList(lore, o)));
				}
				icon.setItemMeta(meta);
				return icon;
			}
		}
		
		public static IconInfo of(String icon_format, String title, List<String> lore, NamespacedKey tooltipKey, boolean hide_tooltip){
			return new IconInfo(icon_format, title, lore, tooltipKey, hide_tooltip);
		}
	}
	
	public enum IconSet{
		ICON(".icon"),
		TITLE(".title"),
		LORE(".lore"),
		TOOLTIP(".components.tooltip-key", true, (value) -> NamespacedKey.fromString(value)),
		HIDE_TOOLTIP(".options.hide-tooltip");
		
		private String key;
		private boolean exception;
		private Function<String, NamespacedKey> migration;
		
		IconSet(String key){
			this.key = key;
		}
		
		IconSet(String key, boolean exception, Function<String, NamespacedKey> migration){
			this.key = key;
			this.exception = exception;
			this.migration = migration;
		}
		
		public boolean exception(){
			return this.exception;
		}
		
		@SuppressWarnings("unchecked")
		public <T> T migrate(String value){
			if(migration != null){
				return (T) migration.apply(value);
			}
			return null;
		}
		
		public String reversion(Object value){
			if(value instanceof NamespacedKey){
				NamespacedKey key = (NamespacedKey) value;
				return key.asString();
			}
			return value.toString();
		}
		
		public String getKey(IconSetting setting){
			return setting.getPath() + this.key;
		}
		
		@SuppressWarnings("unchecked")
		public <T> T getValue(IconSetting setting){
			switch(this){
				case ICON:
					return (T) setting.getDefaultIconFormat();
				case LORE:
					return (T) setting.getDefaultPlainLore();
				case TITLE:
					return (T) setting.getDefaultTitle();
				case TOOLTIP:
					return (T) setting.getDefaultTooltipKey();
				case HIDE_TOOLTIP:
					return (T) setting.getDefaultHideTooltip();
				default:
					return null;
			}
		}
		
		@SuppressWarnings("unchecked")
		public <T> T getConfig(IconSetting setting){
			return (T) config.get(this.getKey(setting));
		}
	}
	
	public static NamespacedKey parseComponentKeyFromString(String format){
		if(format == null || format.isEmpty()) return EMPTY_KEY;
		return NamespacedKey.fromString(format);
	}
}