package me.ministrie.gui.types.emoticon;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;

import me.ministrie.api.player.MamanghoPlayer;
import me.ministrie.configs.IconSetting;
import me.ministrie.configs.SoundSetting;
import me.ministrie.gui.ButtonAction;
import me.ministrie.gui.Pageable;
import me.ministrie.gui.Screen;
import me.ministrie.gui.ScreenHolder;
import me.ministrie.gui.types.holders.emoticon.EmoticonGuiHolder;
import me.ministrie.main.MamanghoSystem;
import me.ministrie.utils.MathUtil;
import me.ministrie.utils.component.ComponentUtil;

import me.ministrie.emoticon.EmoticonPackage;

public class EmoticonGui extends EmoticonScreen implements Screen, Pageable, ButtonAction<EmoticonPackage>{

	private static final int MAX_SIZE = 36;
	private static final int[] BLACK_LINES = {36, 37, 38, 39, 40, 41, 42, 43, 44}; 
	private static final int PREVIOUS_BUTTON_SLOT = 48;
	private static final int PAGE_SLOT = 49;
	private static final int NEXT_BUTTON_SLOT = 50;
	private static final int BOOKMARKED_SLOT = 53;
	
	private MamanghoPlayer viewer;
	private Screen previous;
	private ScreenHolder holder;
	
	private int page;
	private Map<Integer, List<EmoticonPackage>> pages = new HashMap<>();
	private EmoticonPackage[] actions;
	
	public EmoticonGui(MamanghoPlayer viewer){
		this.viewer = viewer;
		this.holder = new EmoticonGuiHolder(this);
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
			page++;
			this.refresh();
			SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
			return true;
		}
		return false;
	}

	@Override
	public boolean previousPage(){
		if(this.pages.containsKey(page-1)){
			page--;
			this.refresh();
			SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
			return true;
		}
		return false;
	}

	@Override
	public MamanghoPlayer getViewer(){
		return viewer;
	}

	@Override
	public void open(){
		this.viewer.getPlayer().openInventory(getPanel());
	}

	@Override
	public void open(Screen previous){
		this.previous = previous;
		this.open();
	}

	@Override
	public void close(){
		this.viewer.getPlayer().closeInventory();
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
		EmoticonPackage emoticon = null;
		if((emoticon = getAction(slot)) != null){
			if(event.getClick().equals(ClickType.LEFT)){
				EmoticonDetailGui g = new EmoticonDetailGui(this.viewer, emoticon);
				g.open(this);
			}
		}else if(slot == PREVIOUS_BUTTON_SLOT){
			this.previousPage();
		}else if(slot == NEXT_BUTTON_SLOT){
			this.nextPage();
		}else if(slot == BOOKMARKED_SLOT){
			if(event.getClick().equals(ClickType.LEFT)){
				EmoticonBookmarkedGui g = new EmoticonBookmarkedGui(this.viewer);
				g.open(this);
			}
		}
	}
	
	@Override
	public void initializePages(){
		pages.clear();
		int page = 0;
		int offset = 0;
		List<EmoticonPackage> list = MamanghoSystem.getEmoticonManager().getEmoticons();
		List<EmoticonPackage> sublist = null;
		try{
			while((sublist = list.subList(offset, offset+MAX_SIZE)) != null){
				pages.put(page++, new ArrayList<>(sublist));
				offset += MAX_SIZE;
			}
		}catch(Exception e){
			List<EmoticonPackage> lastpage = new ArrayList<>(list.subList(offset, list.size()));
			if(!lastpage.isEmpty() || pages.isEmpty()) pages.put(page, lastpage);
		}
	}

	@Override
	public void refresh(){
		Player player = this.viewer.getPlayer();
		InventoryView view = player.getOpenInventory();
		Inventory inv = view.getTopInventory();
		InventoryHolder holder = inv.getHolder();
		if(holder != null && holder instanceof EmoticonGuiHolder){
			inv.setContents(this.getPanel().getContents());
		}
	}
	
	protected Inventory getPanel(){
		this.initializePages();
		Inventory panel = Bukkit.createInventory(holder, 54, ComponentUtil.parseComponent("디시콘"));
		actions = new EmoticonPackage[panel.getSize()];
		
		List<EmoticonPackage> page = this.pages.get(this.page);
		if(page != null && !page.isEmpty()){
			int slot = 0;
			for(EmoticonPackage e : page){
				panel.setItem(slot, e.getUIIcon());
				actions[slot] = e;
				slot++;
			}
		}
		this.postProcess(panel);
		return panel;
	}

	@Override
	public EmoticonPackage getAction(int slot){
		if(actions == null) return null;
		return actions[slot];
	}
	
	protected void postProcess(Inventory panel){
		for(int slot : BLACK_LINES){
			panel.setItem(slot, IconSetting.GUI_PAGEABLE_BLACKLINE.getIconInfo().toIcon());
		}
		panel.setItem(PREVIOUS_BUTTON_SLOT, IconSetting.GUI_PREVIOUS_BUTTON.getIconInfo().toIcon());
		panel.setItem(PAGE_SLOT, IconSetting.GUI_PAGE_BUTTON.getIconInfo().toIcon(this.getPage()+1, MathUtil.under(this.getMaxPage(), 1)));
		panel.setItem(NEXT_BUTTON_SLOT, IconSetting.GUI_NEXT_BUTTON.getIconInfo().toIcon());
		panel.setItem(BOOKMARKED_SLOT, IconSetting.GUI_EMOTICON_BOOKMARK_BUTTON.getIconInfo().toIcon());
	}
}
