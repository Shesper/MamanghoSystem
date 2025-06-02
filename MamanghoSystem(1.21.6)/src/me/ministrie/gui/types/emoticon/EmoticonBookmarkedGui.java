package me.ministrie.gui.types.emoticon;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import me.ministrie.configs.ServerSetting;
import me.ministrie.configs.SoundSetting;
import me.ministrie.emoticon.Emoticon;
import me.ministrie.emoticon.EmoticonBookmark;
import me.ministrie.gui.ButtonAction;
import me.ministrie.gui.Pageable;
import me.ministrie.gui.Screen;
import me.ministrie.gui.ScreenHolder;
import me.ministrie.gui.types.holders.emoticon.EmoticonBookmarkedGuiHolder;
import me.ministrie.handlers.data.player.PlayerDataHandler.DataEnum;
import me.ministrie.managers.PlayerCooldownManager;
import me.ministrie.managers.PlayerCooldownManager.CooldownType;
import me.ministrie.utils.MathUtil;
import me.ministrie.utils.component.ComponentUtil;
import me.ministrie.utils.string.StringUtils;

public class EmoticonBookmarkedGui extends EmoticonScreen implements Screen, Pageable, ButtonAction<Emoticon>{

	private static final int MAX_SIZE = 36;
	private static final int[] BLACK_LINES = {36, 37, 38, 39, 40, 41, 42, 43, 44}; 
	private static final int PREVIOUS_BUTTON_SLOT = 48;
	private static final int PAGE_SLOT = 49;
	private static final int NEXT_BUTTON_SLOT = 50;
	
	private MamanghoPlayer viewer;
	private ScreenHolder holder;
	private Emoticon[] actions;
	private Screen previous;
	
	private int page;
	private Map<Integer, List<Emoticon>> pages = new HashMap<>();
	
	public EmoticonBookmarkedGui(MamanghoPlayer viewer){
		this.viewer = viewer;
		this.holder = new EmoticonBookmarkedGuiHolder(this);
	}
	
	@Override
	public Emoticon getAction(int slot){
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
		if(this.pages.containsKey(this.page+1)){
			this.page++;
			this.refresh();
			SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
			return true;
		}
		return false;
	}

	@Override
	public boolean previousPage(){
		if(this.pages.containsKey(this.page-1)){
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
		EmoticonBookmark bookmark = this.viewer.getData().getData(DataEnum.EMOTICON_BOOKMARK, EmoticonBookmark.emptyBookmark());
		List<Emoticon> list = bookmark.getBookmarkedList();
		List<Emoticon> sublist = null;
		try{
			while((sublist = list.subList(offset, offset+MAX_SIZE)) != null){
				pages.put(page++, new ArrayList<>(sublist));
				offset += MAX_SIZE;
			}
		}catch(Exception e){
			List<Emoticon> lastpage = new ArrayList<>(list.subList(offset, list.size()));
			if(!lastpage.isEmpty() || pages.isEmpty()) pages.put(page, lastpage);
		}
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
	public void refresh(){
		Player player = this.viewer.getPlayer();
		InventoryView view = player.getOpenInventory();
		Inventory inv = view.getTopInventory();
		InventoryHolder holder = inv.getHolder();
		if(holder != null && holder instanceof EmoticonBookmarkedGuiHolder){
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
		Emoticon emoticon = null;
		if((emoticon = getAction(slot)) != null){
			ClickType click = event.getClick();
			if(click.equals(ClickType.LEFT)){
				this.closeAbsolutly();
				this.viewer.printIcon(emoticon);
			}else if(click.equals(ClickType.RIGHT)){
				EmoticonBookmark bookmark = this.viewer.getData().getData(DataEnum.EMOTICON_BOOKMARK, EmoticonBookmark.emptyBookmark());
				bookmark.bookmark(emoticon, false, this.viewer.getData());
				SoundSetting.DEFAULT_GUI_EMOTICON_BOOKMARK.playSound(viewer);
				this.refresh();
			}else if(click.equals(ClickType.SHIFT_LEFT)){
				this.hookPairFirst(emoticon);
				SoundSetting.DEFAULT_GUI_HOOK_EMOTICON_PAIR_FIRST.playSound(viewer);
				this.refresh();
			}else if(click.equals(ClickType.SHIFT_RIGHT)){
				if(this.getHookPairFirst() != null){
					this.closeAbsolutly();
					this.viewer.printPairIcon(this.getHookPairFirst(), emoticon);
					this.hookPairFirst(null);
					SoundSetting.DEFAULT_GUI_HOOK_EMOTICON_PAIR_FIRST.playSound(viewer);
					this.viewer.getPlayer().updateInventory();
				}
			}else if(click.equals(ClickType.NUMBER_KEY)){
				if(event.getHotbarButton() == 0){
					PlayerCooldownManager cooldowns = this.viewer.getCooldownManager();
					if(cooldowns.isCooldown(CooldownType.BIG_EMOTICON)){
						this.viewer.getPlayer().sendMessage(ComponentUtil.translatiableFrom("system.cooldowns.big-emoticon", ComponentUtil.parseComponent(COOLDOWN_FORMAT.formatted(Long.toString(cooldowns.getRemainCooldownWithSeconds(CooldownType.BIG_EMOTICON))))));
						return;
					}
					this.closeAbsolutly();
					this.viewer.printIcon(emoticon, true);
					this.viewer.getPlayer().updateInventory();
					if(!this.viewer.getPlayer().isOp()){
						cooldowns.setCooldown(CooldownType.BIG_EMOTICON, System.currentTimeMillis()+ServerSetting.BIG_EMOTICON_COOLDOWN.<Integer>getValue());
					}
				}
			}
		}else if(slot == PREVIOUS_BUTTON_SLOT){
			this.previousPage();
		}else if(slot == NEXT_BUTTON_SLOT){
			this.nextPage();
		}
	}
	
	protected Inventory getPanel(){
		this.initializePages();
		Inventory panel = Bukkit.createInventory(holder, 54, ComponentUtil.parseComponent(StringUtils.stripColor("북마크한 디시콘 리스트")));
		actions = new Emoticon[panel.getSize()];
		
		List<Emoticon> page = this.pages.get(this.page);
		if(page != null && !page.isEmpty()){
			int slot = 0;
			for(Emoticon e : page){
				panel.setItem(slot, e.getUIIcon(true, this.getHookPairFirst()));
				actions[slot] = e;
				slot++;
			}
		}
		this.postProcess(panel);
		return panel;
	}

	protected void postProcess(Inventory panel){
		for(int slot : BLACK_LINES){
			panel.setItem(slot, IconSetting.GUI_PAGEABLE_BLACKLINE.getIconInfo().toIcon());
		}
		panel.setItem(PREVIOUS_BUTTON_SLOT, IconSetting.GUI_PREVIOUS_BUTTON.getIconInfo().toIcon());
		panel.setItem(PAGE_SLOT, IconSetting.GUI_PAGE_BUTTON.getIconInfo().toIcon(this.getPage()+1, MathUtil.under(this.getMaxPage(), 1)));
		panel.setItem(NEXT_BUTTON_SLOT, IconSetting.GUI_NEXT_BUTTON.getIconInfo().toIcon());
	}
}
