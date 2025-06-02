package me.ministrie.gui.types.image;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;

import com.loohp.imageframe.ImageFrame;
import com.loohp.imageframe.objectholders.ImageMap;
import com.loohp.imageframe.utils.MapUtils;

import me.ministrie.api.player.MamanghoPlayer;
import me.ministrie.configs.IconSetting;
import me.ministrie.configs.MessageSetting;
import me.ministrie.gui.ButtonAction;
import me.ministrie.gui.Screen;
import me.ministrie.gui.ScreenHolder;
import me.ministrie.gui.types.holders.image.ImageMapListGuiHolder;
import me.ministrie.main.MamanghoSystem;
import me.ministrie.thread.ThreadUtils;
import me.ministrie.utils.component.ComponentUtil;

public class ImageMapListGui implements Screen, ButtonAction<ImageMap>{

	private MamanghoPlayer viewer;
	private ScreenHolder holder;
	private Screen previous;
	private ImageMap[] actions;
	private boolean processing;
	
	public ImageMapListGui(MamanghoPlayer viewer){
		this.viewer = viewer;
		this.holder = new ImageMapListGuiHolder(this);
	}
	
	@Override
	public MamanghoPlayer getViewer(){
		return viewer;
	}

	@Override
	public void open(){
		MamanghoSystem.getTaskChainFactory().newChain().<Inventory>asyncFirstCallback((gui) -> {
			gui.accept(getPanel());
		}).syncLast(ui -> {
			if(viewer.getPlayer().isOnline()) viewer.getPlayer().openInventory(ui);
		}).execute();
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
		if(holder != null && holder instanceof ImageMapListGuiHolder){
			inv.setContents(this.getPanel().getContents());
		}
	}
	
	protected Inventory getPanel(){
		Inventory panel = Bukkit.createInventory(holder, 54, ComponentUtil.parseComponent("내 이미지 맵 정보 관리"));
		this.actions = new ImageMap[panel.getSize()];
		
		Set<ImageMap> list = ImageFrame.imageMapManager.getFromCreator(viewer.getPlayer().getUniqueId());
		int slot = 0;
		if(list != null){
			List<ImageMap> sotred = list.stream().sorted((A, B) -> {
				Integer a = A.getImageIndex();
				return a.compareTo(B.getImageIndex());
			}).collect(Collectors.toList());
			for(ImageMap map : sotred){
				actions[slot] = map;
				panel.setItem(slot, IconSetting.IMAGE_FRAME_IMG_INFO_ICON.getIconInfo().toIcon(map.getImageIndex(), map.getWidth(), map.getHeight(), map.getName(), map.getWidth()*map.getHeight()));
				slot++;
			}
		}
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
		if(this.getAction(slot) != null){
			ImageMap map = this.getAction(slot);
			if(event.isLeftClick()){
				if(!checkEnoughMap(map)){
					MessageSetting.SYSTEM_GUI_NOT_ENOUGH_EMPTY_MAP.sendMessages(viewer);
					viewer.getPlayer().playSound(viewer.getPlayer(), Sound.ENTITY_ITEM_BREAK, 1.1f, 1.1f);
					return;
				}
				this.consumeMap(map);
				ImageFrame.combinedMapItemHandler.giveCombinedMap(map, viewer.getPlayer());
			}else if(event.isRightClick()){
				if(processing) return;
				processing = true;
				ThreadUtils.runAsyncThread(() -> {
					if(ImageFrame.imageMapManager.deleteMap(map.getImageIndex())){
						MessageSetting.SYSTEM_GUI_PROCESS_DELETE_IMAGE.sendMessages(viewer);
						processing = false;
						ThreadUtils.runThreadSafe((v) -> {
							this.refresh();
                            Inventory inventory = viewer.getPlayer().getInventory();
                            for (int i = 0; i < inventory.getSize(); i++) {
                                ItemStack currentItem = inventory.getItem(i);
                                MapView currentMapView = MapUtils.getItemMapView(currentItem);
                                if (currentMapView != null) {
                                    if (ImageFrame.imageMapManager.isMapDeleted(currentMapView)) {
                                        inventory.setItem(i, new ItemStack(Material.MAP, currentItem.getAmount()));
                                    }
                                }
                            }
						});
					}else{
						processing = false;
						MessageSetting.SYSTEM_GUI_PROCESS_FAILED_DELETE_IMAGE.sendMessages(viewer);
					}
				});
			}
		}
	}

	@Override
	public ImageMap getAction(int slot){
		if(actions == null) return null;
		return actions[slot];
	}
	
	private boolean checkEnoughMap(ImageMap map){
		int enough = map.getWidth()*map.getHeight();
		int amount = 0;
		PlayerInventory pinv = viewer.getPlayer().getInventory();
		
		ItemStack[] items = pinv.getStorageContents();
		for(ItemStack item : items){
			if(item == null) continue;
			if(item.getType().equals(Material.MAP)){
				amount += item.getAmount();
			}
		}
		return amount >= enough;
	}
	
	private void consumeMap(ImageMap map){
		int enough = map.getWidth()*map.getHeight();;
		PlayerInventory pinv = viewer.getPlayer().getInventory();
		
		ItemStack[] items = pinv.getStorageContents();
		int slot = 0;
		for(ItemStack item : items){
			if(item == null){
				slot++;
				continue;
			}
			if(item.getType().equals(Material.MAP)){
				int amount = item.getAmount();
				if(amount > enough){
					item.setAmount(item.getAmount()-enough);
					pinv.setItem(slot, item);
					return;
				}
				if(amount == enough){
					pinv.setItem(slot, null);
					return;
				}
				enough -= amount;
				pinv.setItem(slot, null);
			}
			slot++;
		}
	}
}
