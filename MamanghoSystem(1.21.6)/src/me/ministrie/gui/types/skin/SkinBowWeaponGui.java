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
import me.ministrie.gui.types.holders.skin.SkinBowWeaponGuiHolder;
import me.ministrie.handlers.data.player.PlayerDataHandler.DataEnum;
import me.ministrie.main.MamanghoSystem;
import me.ministrie.skins.BowSkin;
import me.ministrie.utils.MathUtil;
import me.ministrie.utils.component.ComponentUtil;

public class SkinBowWeaponGui implements Screen, Pageable, ButtonAction<BowSkin>{

	private static final int MAX_SIZE = 36;
	private static final int[] BLACK_LINES = {36, 37, 38, 39, 40, 41, 42, 43, 44}; 
	private static final int PREVIOUS_BUTTON_SLOT = 48;
	private static final int PAGE_SLOT = 49;
	private static final int NEXT_BUTTON_SLOT = 50;
	private static final int UNSET_SLOT = 53;
	
	private MamanghoPlayer viewer;
	private Screen previous;
	private ScreenHolder holder;
	private BowSkin[] actions;
	
	private int page;
	private Map<Integer, List<BowSkin>> pages = Maps.newHashMap();
	
	public SkinBowWeaponGui(MamanghoPlayer viewer){
		this.viewer = viewer;
		this.holder = new SkinBowWeaponGuiHolder(this);
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
		Inventory panel = Bukkit.createInventory(holder, 54, ComponentUtil.parseComponent("활 스킨 설정"));
		this.actions = new BowSkin[panel.getSize()];
		
		List<BowSkin> page = this.pages.get(this.page);
		if(page != null && !page.isEmpty()){
			int slot = 0;
			for(BowSkin skin : page){
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
		if(holder != null && holder instanceof SkinBowWeaponGuiHolder){
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
		BowSkin skin = null;
		ClickType type = event.getClick();
		if((skin = this.getAction(slot)) != null){
			if(type.equals(ClickType.NUMBER_KEY)){
				int hotkey = event.getHotbarButton();
				if(hotkey == 0){
					BowSkin current = this.viewer.getData().<BowSkin>getData(DataEnum.BOW_SKIN);
					boolean same = current != null && current.getUID().equals(skin.getUID());
					if(same){
						if(this.viewer.getData().setData(DataEnum.BOW_SKIN, null)){
							SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
							ProtocolLibrary.getProtocolManager().updateEntity(this.viewer.getPlayer(), this.viewer.getPlayer().getWorld().getPlayers());
							this.refresh();
							this.viewer.getPlayer().updateInventory();
						}
					}else{
						if(this.viewer.getData().setData(DataEnum.BOW_SKIN, skin)){
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
					BowSkin current = this.viewer.getData().<BowSkin>getData(DataEnum.BOW_SKIN);
					if(current != null){
						this.viewer.getData().setData(DataEnum.BOW_SKIN, null);
						SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
						ProtocolLibrary.getProtocolManager().updateEntity(this.viewer.getPlayer(), this.viewer.getPlayer().getWorld().getPlayers());
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
	public BowSkin getAction(int slot){
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
		List<BowSkin> list = new ArrayList<>(MamanghoSystem.getSkinManager().getBowSkins());
		list.sort((a, b) -> {
			return Integer.valueOf(a.getPriority()).compareTo(b.getPriority());
		});
		List<BowSkin> sublist = null;
		try{
			while((sublist = list.subList(offset, offset+MAX_SIZE)) != null){
				pages.put(page++, new ArrayList<>(sublist));
				offset += MAX_SIZE;
			}
		}catch(Exception e){
			List<BowSkin> lastpage = new ArrayList<>(list.subList(offset, list.size()));
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
		BowSkin skin = data.getData(DataEnum.BOW_SKIN);
		String bow = skin != null ? skin.getName() : MessageSetting.FORMAT_SKIN_EMPTY.getValue();
		panel.setItem(UNSET_SLOT, IconSetting.SKIN_BOW_UNSET_ICON.getIconInfo().toIcon(bow));
	}
}
