package me.ministrie.utils;

public class Pair<K, V>{

	private K key;
	private V value;
	
	public Pair(K key, V value){
		this.key = key;
		this.value = value;
	}
	
	public K getKey(){
		return key;
	}
	
	public V getValue(){
		return value;
	}
	
	public static <K, V> Pair<K, V> of(K key, V value){
		return new Pair<K, V>(key, value);
	}
}
