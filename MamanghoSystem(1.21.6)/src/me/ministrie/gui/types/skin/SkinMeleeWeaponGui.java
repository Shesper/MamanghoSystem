package me.ministrie.gui.types.skin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;

import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.collect.Maps;

import me.ministrie.api.data.player.PlayerData;
import me.ministrie.api.player.MamanghoPlayer;
import me.ministrie.configs.IconSetting;
import me.ministrie.configs.MessageSetting;
import me.ministrie.configs.SoundSetting;
import me.ministrie.gui.ButtonAction;
import me.ministrie.gui.Pageable;
import me.ministrie.gui.Screen;
import me.ministrie.gui.ScreenHolder;
import me.ministrie.gui.types.holders.skin.SkinMeleeWeaponGuiHolder;
import me.ministrie.handlers.data.player.PlayerDataHandler.DataEnum;
import me.ministrie.main.MamanghoSystem;
import me.ministrie.skins.WeaponSkin;
import me.ministrie.utils.MathUtil;
import me.ministrie.utils.component.ComponentUtil;

public class SkinMeleeWeaponGui implements Screen, Pageable, ButtonAction<WeaponSkin>{

	private static final int MAX_SIZE = 36;
	private static final int[] BLACK_LINES = {36, 37, 38, 39, 40, 41, 42, 43, 44}; 
	private static final int PREVIOUS_BUTTON_SLOT = 48;
	private static final int PAGE_SLOT = 49;
	private static final int NEXT_BUTTON_SLOT = 50;
	private static final int UNSET_SLOT = 53;
	
	private MamanghoPlayer viewer;
	private Screen previous;
	private ScreenHolder holder;
	private WeaponSkin[] actions;
	
	private int page;
	private Map<Integer, List<WeaponSkin>> pages = Maps.newHashMap();
	
	public SkinMeleeWeaponGui(MamanghoPlayer viewer){
		this.viewer = viewer;
		this.holder = new SkinMeleeWeaponGuiHolder(this);
	}
	
	@Override
	public MamanghoPlayer getViewer(){
		return viewer;
	}

	@Override
	public void open(){
		this.viewer.getPlayer().openInventory(getPanel());
	}

	protected Inventory getPanel(){
		this.initializePages();
		Inventory panel = Bukkit.createInventory(holder, 54, ComponentUtil.parseComponent("근접 무기 스킨 설정"));
		this.actions = new WeaponSkin[panel.getSize()];
		
		List<WeaponSkin> page = this.pages.get(this.page);
		if(page != null && !page.isEmpty()){
			int slot = 0;
			for(WeaponSkin skin : page){
				panel.setItem(slot, skin.getIcon(viewer));
				this.actions[slot] = skin;
				slot++;
			}
		}
		
		this.postProcess(panel);
		return panel;
	}
	
	@Override
	public void open(Screen previous){
		this.previous = previous;
		this.open();
	}

	@Override
	public void refresh(){
		Player player = this.viewer.getPlayer();
		InventoryView view = player.getOpenInventory();
		Inventory inv = view.getTopInventory();
		InventoryHolder holder = inv.getHolder();
		if(holder != null && holder instanceof SkinMeleeWeaponGuiHolder){
			inv.setContents(this.getPanel().getContents());
		}
	}

	@Override
	public Screen getPreviousScreen(){
		return previous;
	}

	@Override
	public ScreenHolder getHolder(){
		return holder;
	}

	@Override
	public void click(InventoryClickEvent event){
		Inventory clicked = event.getClickedInventory();
		if(clicked == null || !clicked.getType().equals(InventoryType.CHEST)) return;
		
		int slot = event.getSlot();
		WeaponSkin skin = null;
		ClickType type = event.getClick();
		if((skin = this.getAction(slot)) != null){
			if(type.equals(ClickType.NUMBER_KEY)){
				int hotkey = event.getHotbarButton();
				if(hotkey == 0){
					WeaponSkin current = this.viewer.getData().<WeaponSkin>getData(DataEnum.SWORD_SKIN);
					boolean same = current != null && current.getUID().equals(skin.getUID());
					if(same){
						if(this.viewer.getData().setData(DataEnum.SWORD_SKIN, null)){
							SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
							ProtocolLibrary.getProtocolManager().updateEntity(this.viewer.getPlayer(), this.viewer.getPlayer().getWorld().getPlayers());
							this.refresh();
							this.viewer.getPlayer().updateInventory();
						}
					}else{
						if(this.viewer.getData().setData(DataEnum.SWORD_SKIN, skin)){
							SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
							ProtocolLibrary.getProtocolManager().updateEntity(this.viewer.getPlayer(), this.viewer.getPlayer().getWorld().getPlayers());
							this.refresh();
							this.viewer.getPlayer().updateInventory();
						}
					}
				}else if(hotkey == 1){
					WeaponSkin current = this.viewer.getData().<WeaponSkin>getData(DataEnum.AXE_SKIN);
					boolean same = current != null && current.getUID().equals(skin.getUID());
					if(same){
						if(this.viewer.getData().setData(DataEnum.AXE_SKIN, null)){
							SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
							ProtocolLibrary.getProtocolManager().updateEntity(this.viewer.getPlayer(), this.viewer.getPlayer().getWorld().getPlayers());
							this.refresh();
							this.viewer.getPlayer().updateInventory();
						}
					}else{
						if(this.viewer.getData().setData(DataEnum.AXE_SKIN, skin)){
							SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
							ProtocolLibrary.getProtocolManager().updateEntity(this.viewer.getPlayer(), this.viewer.getPlayer().getWorld().getPlayers());
							this.refresh();
							this.viewer.getPlayer().updateInventory();
						}
					}
				}else if(hotkey == 2){
					WeaponSkin current = this.viewer.getData().<WeaponSkin>getData(DataEnum.MACE_SKIN);
					boolean same = current != null && current.getUID().equals(skin.getUID());
					if(same){
						if(this.viewer.getData().setData(DataEnum.MACE_SKIN, null)){
							SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
							ProtocolLibrary.getProtocolManager().updateEntity(this.viewer.getPlayer(), this.viewer.getPlayer().getWorld().getPlayers());
							this.refresh();
							this.viewer.getPlayer().updateInventory();
						}
					}else{
						if(this.viewer.getData().setData(DataEnum.MACE_SKIN, skin)){
							SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
							ProtocolLibrary.getProtocolManager().updateEntity(this.viewer.getPlayer(), this.viewer.getPlayer().getWorld().getPlayers());
							this.refresh();
							this.viewer.getPlayer().updateInventory();
						}
					}
				}else if(hotkey == 3){
					WeaponSkin current = this.viewer.getData().<WeaponSkin>getData(DataEnum.PICKAXE_SKIN);
					boolean same = current != null && current.getUID().equals(skin.getUID());
					if(same){
						if(this.viewer.getData().setData(DataEnum.PICKAXE_SKIN, null)){
							SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
							ProtocolLibrary.getProtocolManager().updateEntity(this.viewer.getPlayer(), this.viewer.getPlayer().getWorld().getPlayers());
							this.refresh();
							this.viewer.getPlayer().updateInventory();
						}
					}else{
						if(this.viewer.getData().setData(DataEnum.PICKAXE_SKIN, skin)){
							SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
							ProtocolLibrary.getProtocolManager().updateEntity(this.viewer.getPlayer(), this.viewer.getPlayer().getWorld().getPlayers());
							this.refresh();
							this.viewer.getPlayer().updateInventory();
						}
					}
				}else if(hotkey == 4){
					WeaponSkin current = this.viewer.getData().<WeaponSkin>getData(DataEnum.SHOVEL_SKIN);
					boolean same = current != null && current.getUID().equals(skin.getUID());
					if(same){
						if(this.viewer.getData().setData(DataEnum.SHOVEL_SKIN, null)){
							SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
							ProtocolLibrary.getProtocolManager().updateEntity(this.viewer.getPlayer(), this.viewer.getPlayer().getWorld().getPlayers());
							this.refresh();
							this.viewer.getPlayer().updateInventory();
						}
					}else{
						if(this.viewer.getData().setData(DataEnum.SHOVEL_SKIN, skin)){
							SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
							ProtocolLibrary.getProtocolManager().updateEntity(this.viewer.getPlayer(), this.viewer.getPlayer().getWorld().getPlayers());
							this.refresh();
							this.viewer.getPlayer().updateInventory();
						}
					}
				}else if(hotkey == 5){
					WeaponSkin current = this.viewer.getData().<WeaponSkin>getData(DataEnum.HOE_SKIN);
					boolean same = current != null && current.getUID().equals(skin.getUID());
					if(same){
						if(this.viewer.getData().setData(DataEnum.HOE_SKIN, null)){
							SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
							ProtocolLibrary.getProtocolManager().updateEntity(this.viewer.getPlayer(), this.viewer.getPlayer().getWorld().getPlayers());
							this.refresh();
							this.viewer.getPlayer().updateInventory();
						}
					}else{
						if(this.viewer.getData().setData(DataEnum.HOE_SKIN, skin)){
							SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
							ProtocolLibrary.getProtocolManager().updateEntity(this.viewer.getPlayer(), this.viewer.getPlayer().getWorld().getPlayers());
							this.refresh();
							this.viewer.getPlayer().updateInventory();
						}
					}
				}else if(hotkey == 6){
					WeaponSkin current = this.viewer.getData().<WeaponSkin>getData(DataEnum.FISHING_ROD_SKIN);
					boolean same = current != null && current.getUID().equals(skin.getUID());
					if(same){
						if(this.viewer.getData().setData(DataEnum.FISHING_ROD_SKIN, null)){
							SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
							ProtocolLibrary.getProtocolManager().updateEntity(this.viewer.getPlayer(), this.viewer.getPlayer().getWorld().getPlayers());
							this.refresh();
							this.viewer.getPlayer().updateInventory();
						}
					}else{
						if(this.viewer.getData().setData(DataEnum.FISHING_ROD_SKIN, skin)){
							SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
							ProtocolLibrary.getProtocolManager().updateEntity(this.viewer.getPlayer(), this.viewer.getPlayer().getWorld().getPlayers());
							this.refresh();
							this.viewer.getPlayer().updateInventory();
						}
					}
				}
			}
		}else if(slot == UNSET_SLOT){
			if(type.equals(ClickType.NUMBER_KEY)){
				int hotkey = event.getHotbarButton();
				if(hotkey == 0){
					WeaponSkin current = this.viewer.getData().<WeaponSkin>getData(DataEnum.SWORD_SKIN);
					if(current != null){
						this.viewer.getData().setData(DataEnum.SWORD_SKIN, null);
						SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
						this.refresh();
						this.viewer.getPlayer().updateInventory();
					}
				}else if(hotkey == 1){
					WeaponSkin current = this.viewer.getData().<WeaponSkin>getData(DataEnum.AXE_SKIN);
					if(current != null){
						this.viewer.getData().setData(DataEnum.AXE_SKIN, null);
						SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
						this.refresh();
						this.viewer.getPlayer().updateInventory();
					}
				}else if(hotkey == 2){
					WeaponSkin current = this.viewer.getData().<WeaponSkin>getData(DataEnum.MACE_SKIN);
					if(current != null){
						this.viewer.getData().setData(DataEnum.MACE_SKIN, null);
						SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
						this.refresh();
						this.viewer.getPlayer().updateInventory();
					}
				}else if(hotkey == 3){
					WeaponSkin current = this.viewer.getData().<WeaponSkin>getData(DataEnum.PICKAXE_SKIN);
					if(current != null){
						this.viewer.getData().setData(DataEnum.PICKAXE_SKIN, null);
						SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
						this.refresh();
						this.viewer.getPlayer().updateInventory();
					}
				}else if(hotkey == 4){
					WeaponSkin current = this.viewer.getData().<WeaponSkin>getData(DataEnum.SHOVEL_SKIN);
					if(current != null){
						this.viewer.getData().setData(DataEnum.SHOVEL_SKIN, null);
						SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
						this.refresh();
						this.viewer.getPlayer().updateInventory();
					}
				}else if(hotkey == 5){
					WeaponSkin current = this.viewer.getData().<WeaponSkin>getData(DataEnum.HOE_SKIN);
					if(current != null){
						this.viewer.getData().setData(DataEnum.HOE_SKIN, null);
						SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
						this.refresh();
						this.viewer.getPlayer().updateInventory();
					}
				}else if(hotkey == 6){
					WeaponSkin current = this.viewer.getData().<WeaponSkin>getData(DataEnum.FISHING_ROD_SKIN);
					if(current != null){
						this.viewer.getData().setData(DataEnum.FISHING_ROD_SKIN, null);
						SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
						this.refresh();
						this.viewer.getPlayer().updateInventory();
					}
				}
			}
		}else if(slot == PREVIOUS_BUTTON_SLOT){
			this.previousPage();
		}else if(slot == NEXT_BUTTON_SLOT){
			this.nextPage();
		}
	}

	@Override
	public WeaponSkin getAction(int slot){
		if(actions == null) return null;
		return actions[slot];
	}

	@Override
	public int getPage(){
		return page;
	}

	@Override
	public int getMaxPage(){
		return pages.size();
	}

	@Override
	public boolean nextPage(){
		if(this.pages.containsKey(page+1)){
			this.page++;
			this.refresh();
			SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
			return true;
		}
		return false;
	}

	@Override
	public boolean previousPage(){
		if(this.pages.containsKey(page-1)){
			this.page--;
			this.refresh();
			SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
			return true;
		}
		return false;
	}

	@Override
	public void initializePages(){
		pages.clear();
		int page = 0;
		int offset = 0;
		List<WeaponSkin> list = new ArrayList<>(MamanghoSystem.getSkinManager().getMeleeWeaponSkins());
		list.sort((a, b) -> {
			return Integer.valueOf(a.getPriority()).compareTo(b.getPriority());
		});
		List<WeaponSkin> sublist = null;
		try{
			while((sublist = list.subList(offset, offset+MAX_SIZE)) != null){
				pages.put(page++, new ArrayList<>(sublist));
				offset += MAX_SIZE;
			}
		}catch(Exception e){
			List<WeaponSkin> lastpage = new ArrayList<>(list.subList(offset, list.size()));
			if(!lastpage.isEmpty() || pages.isEmpty()) pages.put(page, lastpage);
		}
	}
	
	protected void postProcess(Inventory panel){
		for(int slot : BLACK_LINES){
			panel.setItem(slot, IconSetting.GUI_PAGEABLE_BLACKLINE.getIconInfo().toIcon());
		}
		panel.setItem(PREVIOUS_BUTTON_SLOT, IconSetting.GUI_PREVIOUS_BUTTON.getIconInfo().toIcon());
		panel.setItem(PAGE_SLOT, IconSetting.GUI_PAGE_BUTTON.getIconInfo().toIcon(this.getPage()+1, MathUtil.under(this.getMaxPage(), 1)));
		panel.setItem(NEXT_BUTTON_SLOT, IconSetting.GUI_NEXT_BUTTON.getIconInfo().toIcon());
		
		PlayerData data = this.viewer.getData();
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
		panel.setItem(UNSET_SLOT, IconSetting.SKIN_MELEE_WEAPON_UNSET_ICON.getIconInfo().toIcon(sword, axe, mace, pickaxe, shovel, hoe, fishingrod));
	}
}
