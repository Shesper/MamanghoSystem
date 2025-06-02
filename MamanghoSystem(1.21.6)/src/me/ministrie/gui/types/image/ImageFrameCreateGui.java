package me.ministrie.gui.types.image;

import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import com.google.common.collect.Maps;
import com.loohp.imageframe.ImageFrame;
import com.loohp.imageframe.objectholders.DitheringType;
import com.loohp.imageframe.objectholders.ImageMap;
import com.loohp.imageframe.objectholders.URLAnimatedImageMap;
import com.loohp.imageframe.objectholders.URLStaticImageMap;
import com.loohp.imageframe.utils.HTTPRequestUtils;

import me.ministrie.api.player.MamanghoPlayer;
import me.ministrie.configs.IconSetting;
import me.ministrie.configs.MessageSetting;
import me.ministrie.configs.SoundSetting;
import me.ministrie.gui.Screen;
import me.ministrie.gui.ScreenHolder;
import me.ministrie.gui.proccess.ProcessScreen;
import me.ministrie.gui.types.holders.image.ImageFrameCreateGuiHolder;
import me.ministrie.main.MamanghoSystem;
import me.ministrie.thread.ThreadUtils;
import me.ministrie.utils.Pair;
import me.ministrie.utils.component.ComponentUtil;

public class ImageFrameCreateGui implements ProcessScreen{

	private static final int WIDTH_SLOT = 20;
	private static final int HEIGHT_SLOT = 24;
	private static final int CONFIRM_SIZE_SLOT = 22;
	private static final int IMAGE_NAME_SLOT = 22;
	private static final int URL_SLOT = 22;
	private static final int RESULT_SLOT = 22;
	
	private static final int MAX_PROCESS = 3;
	private static final int MAX_IMAGE_SIZE = 10;
	
	private MamanghoPlayer viewer;
	private ScreenHolder holder;
	private Screen previous;
	private int currentprocess;
	
	private int width = 1;
	private int height = 1;
	
	private Map<Integer, Object> processValues = Maps.newHashMap();
	
	public ImageFrameCreateGui(MamanghoPlayer viewer){
		this.viewer = viewer;
		this.holder = new ImageFrameCreateGuiHolder(this);
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
		if(holder != null && holder instanceof ImageFrameCreateGuiHolder){
			inv.setContents(this.getPanel().getContents());
		}
	}

	protected Inventory getPanel(){
		Inventory panel = Bukkit.createInventory(holder, 45, ComponentUtil.parseComponent("이미지 생성"));
		if(this.getProcess() == 0){
			panel.setItem(WIDTH_SLOT, IconSetting.IMAGE_FRAME_WIDTH.getIconInfo().toIcon(width, MAX_IMAGE_SIZE));
			panel.setItem(HEIGHT_SLOT, IconSetting.IMAGE_FRAME_HEIGHT.getIconInfo().toIcon(height, MAX_IMAGE_SIZE));	
			panel.setItem(CONFIRM_SIZE_SLOT, IconSetting.IMAGE_FRAME_SIZE_CONFIRM.getIconInfo().toIcon(width, height, width*height));
		}else if(this.getProcess() == 1){
			if(this.hasValue(this.getProcess())){
				panel.setItem(IMAGE_NAME_SLOT, IconSetting.IMAGE_FRAME_MODIFIED_NAME_AFTER.getIconInfo().toIcon(this.<String>getValue(this.getProcess())));
			}else{
				panel.setItem(IMAGE_NAME_SLOT, IconSetting.IMAGE_FRAME_MODIFIED_NAME_ICON.getIconInfo().toIcon());
			}
		}else if(this.getProcess() == 2){
			if(this.hasValue(this.getProcess())){
				panel.setItem(URL_SLOT, IconSetting.IMAGE_FRAME_URL_AFTER_ICON.getIconInfo().toIcon(this.<String>getValue(this.getProcess())));
			}else{
				panel.setItem(URL_SLOT, IconSetting.IMAGE_FRAME_URL_ICON.getIconInfo().toIcon());
			}
		}else if(this.getProcess() == 3){
			panel.setItem(RESULT_SLOT, IconSetting.IMAGE_FRAME_RESULT_ICON.getIconInfo().toIcon(this.width, this.height, this.getValue(1), this.getValue(2), width*height));
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
		ClickType clickType = event.getClick();
		if(this.getProcess() == 0){
			if(slot == WIDTH_SLOT){
				if(clickType.equals(ClickType.LEFT)){
					this.addWidth();
				}else if(clickType.equals(ClickType.RIGHT)){
					this.subtractWidth();
				}
			}else if(slot == HEIGHT_SLOT){
				if(clickType.equals(ClickType.LEFT)){
					this.addHeight();
				}else if(clickType.equals(ClickType.RIGHT)){
					this.subtractHeight();
				}
			}else if(slot == CONFIRM_SIZE_SLOT){
				if(clickType.equals(ClickType.RIGHT)){
					if(!this.checkEnoughMap()){
						MessageSetting.SYSTEM_GUI_NOT_ENOUGH_EMPTY_MAP.sendMessages(viewer);
						viewer.getPlayer().playSound(viewer.getPlayer(), Sound.ENTITY_ITEM_BREAK, 1.1f, 1.1f);
						return;
					}
					this.nextProcess();
					SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
				}
			}
		}else if(this.getProcess() == 1){
			if(slot == IMAGE_NAME_SLOT){
				if(clickType.equals(ClickType.LEFT)){
					MessageSetting.SYSTEM_GUI_WRITE_IMAGE_NAME_IN_CHAT.sendMessages(viewer);
					this.close();
					this.viewer.setProcessScreen(this);
				}else if(clickType.equals(ClickType.RIGHT)){
					if(this.hasValue(this.getProcess())){
						this.nextProcess();
						SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
					}
				}
			}
		}else if(this.getProcess() == 2){
			if(slot == URL_SLOT){
				if(clickType.equals(ClickType.LEFT)){
					MessageSetting.SYSTEM_GUI_WRITE_IMAGE_URL_IN_CHAT.sendMessages(viewer);
					this.close();
					this.viewer.setProcessScreen(this);
				}else if(clickType.equals(ClickType.RIGHT)){
					if(this.hasValue(this.getProcess())){
						this.nextProcess();
						SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
					}
				}
			}
		}else if(this.getProcess() == 3){
			if(slot == RESULT_SLOT){
				if(clickType.equals(ClickType.RIGHT)){
					this.nextProcess();
				}
			}
		}
	}
	
	public void nextProcess(){
		this.currentprocess++;
		if(this.currentprocess > MAX_PROCESS){
			if(!this.checkEnoughMap()){
				MessageSetting.SYSTEM_GUI_NOT_ENOUGH_EMPTY_MAP.sendMessages(viewer);
				viewer.getPlayer().playSound(viewer.getPlayer(), Sound.ENTITY_ITEM_BREAK, 1.1f, 1.1f);
				return;
			}
			this.consumeMap();
			this.close();
			MessageSetting.SYSTEM_GUI_PROCESS_CREATE_IMAGE.sendMessages(viewer);
			SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
			MamanghoSystem.getTaskChainFactory().newChain().<Pair<Boolean, ImageMap>>asyncFirstCallback((result) -> {
				ImageMap imageMap;
				String url = this.getValue(2);
				if (HTTPRequestUtils.getContentSize(url) > ImageFrame.maxImageFileSize) {
					viewer.getPlayer().sendMessage(ImageFrame.messageImageOverMaxFileSize.replace("{Size}", Long.toString(ImageFrame.maxImageFileSize)));
					return;
				} 
				String imageType = HTTPRequestUtils.getContentType(url);
				if (imageType == null)
					imageType = URLConnection.guessContentTypeFromName(url); 
				if (imageType == null) {
					imageType = "";
				}else{
					imageType = imageType.trim();
				}
				try{
					if (imageType.equals("image/gif")){
						imageMap = URLAnimatedImageMap.create(ImageFrame.imageMapManager, this.<String>getValue(1), url, width, height, DitheringType.NEAREST_COLOR, viewer.getPlayer().getUniqueId()).get();
					} else {
						imageMap = URLStaticImageMap.create(ImageFrame.imageMapManager, this.<String>getValue(1), url, width, height, DitheringType.NEAREST_COLOR, viewer.getPlayer().getUniqueId()).get();
					} 
					ImageFrame.imageMapManager.addMap(imageMap);
					result.accept(Pair.of(true, imageMap));
				} catch (Exception e) {
					e.printStackTrace();
					result.accept(Pair.of(false, null));
				}
			}).asyncLast(pair -> {
				boolean result = pair.getKey();
				ImageMap imageMap = pair.getValue();
				if(result){
					if(imageMap != null){
						ImageFrame.combinedMapItemHandler.giveCombinedMap(imageMap, viewer.getPlayer());
						MessageSetting.SYSTEM_GUI_PROCESSED_CREATE_IMAGE.sendMessages(viewer);
					}
				}else{
					MessageSetting.SYSTEM_GUI_PROCESSED_FAILED_CREATE_IMAGE.sendMessages(viewer);
					ItemStack item = new ItemStack(Material.MAP, this.width*this.height);
					HashMap<Integer, ItemStack> e = viewer.getPlayer().getInventory().addItem(item);
					for(ItemStack i : e.values()){
						viewer.getPlayer().getWorld().dropItem(viewer.getPlayer().getEyeLocation(), i).setVelocity(new Vector(0, 0, 0));
					}
				}
			}).execute();
		}else{
			this.refresh();
		}
	}

	@Override
	public int getProcess(){
		return currentprocess;
	}

	@Override
	public boolean writeValue(int process, Object input){
		if(process == 1){
			if(input instanceof String){
				String value = (String) input;
				MamanghoSystem.getTaskChainFactory().newChain().<Set<ImageMap>>asyncFirstCallback((map_list) -> {
					map_list.accept(ImageFrame.imageMapManager.getFromCreator(viewer.getPlayer().getUniqueId()));
				}).syncLast(result -> {
					if(result != null){
						if(result.stream().anyMatch(each -> each.getName().equalsIgnoreCase(value))){
							MessageSetting.SYSTEM_GUI_CONFLICT_IMAGE_NAME.sendMessages(viewer);
							return;
						}else{
							this.processValues.put(process, value);
							SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
							if(viewer.getPlayer().isOnline()){
								ImageFrameCreateGui.this.open();
								this.viewer.setProcessScreen(null);
							}
						}
					}
				}).execute();
				return true;
			}
			return false;
		}else if(process == 2){
			if(input instanceof String){
				String value = (String) input;
				this.processValues.put(process, value);
				SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
				ThreadUtils.runThreadSafe((v) -> {
					if(viewer.getPlayer().isOnline()){
						ImageFrameCreateGui.this.open();
						this.viewer.setProcessScreen(null);
					}
				});
				return true;
			}
			return false;
		}
		viewer.setProcessScreen(null);
		return false;
	}
	
	public void addWidth(){
		if(width+1 <= MAX_IMAGE_SIZE){
			this.width++;
			SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
			this.refresh();
		}
	}
	public void subtractWidth(){
		if(width-1 >= 1){
			this.width--;
			SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
			this.refresh();
		}
	}

	public void addHeight(){
		if(height+1 <= MAX_IMAGE_SIZE){
			this.height++;
			SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
			this.refresh();
		}
	}
	
	public void subtractHeight(){
		if(height-1 >= 1){
			this.height--;
			SoundSetting.DEFAULT_GUI_PAGE_CLICK.playSound(viewer);
			this.refresh();
		}
	}
	
	private boolean checkEnoughMap(){
		int enough = this.width*this.height;
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
	
	private void consumeMap(){
		int enough = this.width*this.height;
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

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getValue(int process){
		Object o = this.processValues.get(process);
		if(o == null) return null;
		return (T) o;
	}

	@Override
	public boolean hasValue(int process){
		return this.processValues.containsKey(process);
	}

	@Override
	public void cancelProcess(){
		this.viewer.setProcessScreen(null);
		ThreadUtils.runThreadSafe((v) -> {
			ImageFrameGui g = new ImageFrameGui(viewer);
			g.open();
		}, 1);
	}
}
