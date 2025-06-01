package me.ministrie.gui;

public interface Pageable{

	public int getPage();
	
	public int getMaxPage();
	
	public boolean nextPage();
	
	public boolean previousPage();
	
	public void initializePages();
}
