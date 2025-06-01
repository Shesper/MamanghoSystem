package me.ministrie.utils;

public class Trio<F, S, T>{
	
	private F first;
	private S second;
	private T third;
	
	public Trio(F first, S second, T third){
		this.first = first;
		this.second = second;
		this.third = third;
	}
	
	public F getFirst(){
		return first;
	}
	
	public S getSecond(){
		return second;
	}
	
	public T getThird(){
		return third;
	}
	
	public static <F, S, T> Trio<F, S, T> of(F first, S second, T third){
		return new Trio<F, S, T>(first, second, third);
	}
}
