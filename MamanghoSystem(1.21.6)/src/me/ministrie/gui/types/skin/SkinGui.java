package me.ministrie.gui.types.skin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;

import me.ministrie.api.player.MamanghoPlayer;
import me.ministrie.configs.IconSetting;
import me.ministrie.gui.Screen;
import me.ministrie.gui.ScreenHolder;
import me.ministrie.gui.types.holders.skin.SkinGuiHolder;
import me.ministrie.utils.component.ComponentUtil;

public class SkinGui implements Screen{

	private static final int MELEE_WEAPON_SLOT = 11;
	private static final int BOW_WEAPON_SLOT = 13;
	private static final int CROSSBOW_WEAPON_SLOT = 15;
	
	private MamanghoPlayer viewer;
	private Screen previous;
	private ScreenHolder holder;
	
	public SkinGui(MamanghoPlayer viewer){
		this.viewer = viewer;
		this.holder = new SkinGuiHolder(this);
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
		Inventory panel = Bukkit.createInventory(holder, 27, ComponentUtil.parseComponent("스킨 설정"));
		
		panel.setItem(MELEE_WEAPON_SLOT, IconSetting.SKIN_MELEE_WEAPON_ICON.getIconInfo().toIcon());
		panel.setItem(BOW_WEAPON_SLOT, IconSetting.SKIN_BOW_ICON.getIconInfo().toIcon());
		panel.setItem(CROSSBOW_WEAPON_SLOT, IconSetting.SKIN_CROSSBOW_ICON.getIconInfo().toIcon());
		
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
		if(holder != null && holder instanceof SkinGuiHolder){
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
		
		if(slot == MELEE_WEAPON_SLOT){
			SkinMeleeWeaponGui g = new SkinMeleeWeaponGui(this.viewer);
			g.open(this);
		}else if(slot == BOW_WEAPON_SLOT){
			SkinBowWeaponGui g = new SkinBowWeaponGui(this.viewer);
			g.open(this);
		}else if(slot == CROSSBOW_WEAPON_SLOT){
			SkinCrossbowWeaponGui g = new SkinCrossbowWeaponGui(this.viewer);
			g.open(this);
		}
	}
}
