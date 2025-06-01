package me.ministrie.configs;

import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.YamlConfiguration;

public class BiomeInformation{

	private Map<NamespacedKey, BiomeSummary> caches = new HashMap<>();
	
	private static final String FILEPATH = "./plugins/MamanghoSystem/biomes.yml";
	
	public BiomeInformation(){
		this.load();
	}
	
	private static String normalize(String format){
		return format.replace("_", "-").toLowerCase();
	}
	
	public BiomeSummary getBiomeSummary(Biome b){
		return this.caches.get(b.getKey());
	}
	
	@SuppressWarnings("removal")
	public void load(){
		this.caches.clear();
		File file = new File(FILEPATH);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		if(file.exists()){
			for(Biome b : Biome.values()){
				this.caches.put(b.getKey(), BiomeSummary.of(config, b));
			}
		}else{
			for(Biome b : Biome.values()){
				BiomeSummary summary = BiomeSummary.of(config, b);
				String normalize = BiomeInformation.normalize(b.name());
				config.set("biomes." + normalize + ".translate-key", summary.getTranslateKey());
				config.set("biomes." + normalize + ".banner", summary.getBanner());
			}
			try{
				config.save(file);
			}catch (IOException e){
				e.printStackTrace();
			}
		}
	}
	
	public static final class BiomeSummary{
		
		private Biome biome;
		private String translateKey;
		private String banner;
		
		public BiomeSummary(Biome biome, String translateKey, String banner){
			this.biome = biome;
			this.translateKey = translateKey;
			this.banner = banner;
		}
		
		public Biome getBiome(){
			return biome;
		}
		
		public String getTranslateKey(){
			return this.translateKey;
		}
		
		public String getBanner(){
			return banner;
		}
		
		public static BiomeSummary of(Biome biome, String translateKey, String banner){
			return new BiomeSummary(biome, translateKey, banner);
		}
		
		public static BiomeSummary of(YamlConfiguration config, Biome biome){
			@SuppressWarnings("removal")
			String normalize = BiomeInformation.normalize(biome.name());
			String key = config.getString("biomes." + normalize + ".translate-key", "chat.hover.biome." + normalize);
			String banner = config.getString("biomes." + normalize + ".banner", "");
			return new BiomeSummary(biome, key, banner);
		}
	}
}
