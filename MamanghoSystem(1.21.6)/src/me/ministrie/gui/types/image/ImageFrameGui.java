package me.ministrie.gui.types.image;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;

import com.loohp.imageframe.ImageFrame;

import me.ministrie.api.player.MamanghoPlayer;
import me.ministrie.configs.IconSetting;
import me.ministrie.gui.Screen;
import me.ministrie.gui.ScreenHolder;
import me.ministrie.gui.types.holders.image.ImageFrameGuiHolder;
import me.ministrie.utils.component.ComponentUtil;

public class ImageFrameGui implements Screen{

	public static final int CREATE_SLOT = 11;
	public static final int MANAGE_SLOT = 15;
	
	private MamanghoPlayer viewer;
	private Screen previous;
	private ScreenHolder holder;
	
	public ImageFrameGui(MamanghoPlayer viewer){
		this.viewer = viewer;
		this.holder = new ImageFrameGuiHolder(this);
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
		if(holder != null && holder instanceof ImageFrameGuiHolder){
			inv.setContents(this.getPanel().getContents());
		}
	}

	protected Inventory getPanel(){
		Inventory panel = Bukkit.createInventory(holder, 27, ComponentUtil.parseComponent("이미지 맵 관리"));
		int limits = ImageFrame.getPlayerCreationLimit(viewer.getPlayer());
		if(limits == -1) limits = 999;
		panel.setItem(CREATE_SLOT, IconSetting.IMAGE_FRAME_CREATE_IMAGES.getIconInfo().toIcon(limits));
		panel.setItem(MANAGE_SLOT, IconSetting.IMAGE_FRAME_MANAGEMENT_MY_IMAGES.getIconInfo().toIcon());
		return panel;
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
		
		if(event.getClick().equals(ClickType.LEFT)){
			if(slot == CREATE_SLOT){
				ImageFrameCreateGui g = new ImageFrameCreateGui(viewer);
				g.open();
			}else if(slot == MANAGE_SLOT){
				ImageMapListGui g = new ImageMapListGui(viewer);
				g.open(this);
			}
		}
	}
}
