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
	EMOTICON_DETAIL_ICON_UNBOOKMARK("emoticon.detail-icon-unbookmarked", "FEATHER:-1", "{0}", "{1}|&f|&f|&f|&f|&f|&f|{2}|&f좌 클릭: &b이모티콘 사용|&f우 클릭: &a즐겨찾기 추가|&fSHIFT+좌클릭: &6더블 콘 사용|&f1번 키 입력: &d대왕 디시콘 사용"),
	EMOTICON_DETAIL_ICON_BOOKMARKED("emoticon.detail-icon-bookmark", "FEATHER:-1", "{0}", "{1}|&f|&f|&f|&f|&f|&f|{2}|&f좌 클릭: &b이모티콘 사용|&f우 클릭: &c즐겨찾기 제거|&fSHIFT+좌클릭: &6더블 콘 사용|&f1번 키 입력: &d대왕 디시콘 사용"),
	
	EMOTICON_DETAIL_ICON_UNBOOKMARK_HOOKED("emoticon.detail-icon-unbookmarked-hooked", "FEATHER:-1", "{0}", "{1}|&f|&f|&f|&f|&f|&f|{2}|&f좌 클릭: &b이모티콘 사용|&f우 클릭: &a즐겨찾기 추가|&f1번 키 입력: &d대왕 디시콘 사용|&f|&f더블 콘 첫번 째 선택 중(&a다음 아이콘을 SHIFT+우클릭&f)|{3}|&f|&f|&f|&f|&f"),
	EMOTICON_DETAIL_ICON_BOOKMARKED_HOOKED("emoticon.detail-icon-bookmark-hooked", "FEATHER:-1", "{0}", "{1}|&f|&f|&f|&f|&f|&f|{2}|&f좌 클릭: &b이모티콘 사용|&f우 클릭: &c즐겨찾기 제거|&f1번 키 입력: &d대왕 디시콘 사용|&f|&f더블 콘 첫번 째 선택 중(&a다음 아이콘을 SHIFT+우클릭&f)|{3}|&f|&f|&f|&f|&f"),
	
	IMAGE_FRAME_CREATE_IMAGES("image-frame.create-image", "IRON_INGOT:1", "&f이미지 맵 생성", "&f클릭 시 이미지 맵 생성을 시작합니다.|&f플레이어 당 &b{0}&f개의 이미지를 생성할 수 있습니다."),
	IMAGE_FRAME_MANAGEMENT_MY_IMAGES("image-frame.management-my-image", "IRON_INGOT:2", "&f내 이미지 맵 관리", "&f클릭 시 내가 만든 이미지 맵들을 관리합니다."),
	
	IMAGE_FRAME_WIDTH("image-frame.create.width", "IRON_INGOT:3", "&f가로 크기 설정", "&a좌클릭: &d+1 &f가로 크기|&e우클릭: &d-1 &f가로 크기|&f|&f가로 크기: &a{0}|&f|&f최대 {1}까지 가능합니다."),
	IMAGE_FRAME_HEIGHT("image-frame.create.height", "IRON_INGOT:4", "&f세로 크기 설정", "&a좌클릭: &d+1 &f세로 크기|&e우클릭: &d-1 &f세로 크기|&f|&f세로 크기: &a{0}|&f|&f최대 {1}까지 가능합니다."),
	IMAGE_FRAME_SIZE_CONFIRM("image-frame.create.size-confirm", "IRON_INGOT:5", "&f이미지 크기 확인", "&f크기: {0} x {1} (가로, 세로)|&f|&f가로x세로 면적 갯수만큼 빈 지도 아이템이|&f필요합니다. ({2}개)|&f|&f해당 크기로 설정하려면 &a우클릭&f하여|&f다음 단계를 진행합니다."),
	
	IMAGE_FRAME_MODIFIED_NAME_AFTER("image-frame.modified-name-after", "IRON_INGOT:5", "&f이미지 이름 설정 확인", "&f설정 하려는 이름: {0}|&f|&f이미지 이름을 위와 같이 설정하시겠습니까?|&f다른 이름으로 변경을 원하면 다시 &a좌클릭&f하여|&f변경할 수 있습니다.|&f|&f다음으로 진행하려면 &d우클릭&f해주세요."),
	IMAGE_FRAME_MODIFIED_NAME_ICON("image-frame.modifiy-name", "IRON_INGOT:6", "&f이미지 이름 설정", "&f클릭 시 채팅 창에 설정할 이름을 입력하여|&f이미지 이름을 설정합니다."),
	
	IMAGE_FRAME_URL_AFTER_ICON("image-frame.url-after", "IRON_INGOT:7", "&f이미지 URL 설정 확인", "&f입력된 URL: &a{0}|&f|&f이미지 URL을 위와 같이 설정하시겠습니까?&f다른 URL로 변경을 원하시면 다시 &a좌클릭&f하여|&f변경할 수 있으며, 틀린 곳이 없는지|&f필히 확인해주시기 바랍니다.|&f|&f다음으로 진행하려면 &d우클릭&f해주세요."),
	IMAGE_FRAME_URL_ICON("image-frame.url", "IRON_INGOT:8", "&f이미지 URL 설정", "&f클릭 시 채팅창에 설정할 이미지 URL을 입력하여|&f출력될 이미지를 설정합니다."),
	
	IMAGE_FRAME_RESULT_ICON("image-frame.result", "IRON_INGOT:9", "&f이미지 맵 생성 최종 확인" ,"&f|&f이미지 크기: &a{0} &fx &a{1} &f(가로, 세로)|&f이미지 이름: {2}|&f이미지 URL: &a{3}|&f|&f입력된 정보들입니다.|&f위 정보를 토대로 이미지맵을 생성하려면|&a우클릭&f해주세요."),
	IMAGE_FRAME_IMG_INFO_ICON("image-frame.image-info", "PAPER:-1", "&f이미지 정보", "&f|&f이미지 아이디: {0}|&f이미지 크기: &a{1} x {2} &f(가로, 세로)|&f이미지 이름: {3}|&f||&a좌클릭: &f이미지 맵 가져오기 (빈 지도 {4}개 필요)|&c우클릭: &f이미지 삭제");
	
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