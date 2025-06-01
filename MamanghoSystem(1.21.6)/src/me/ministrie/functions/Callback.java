package me.ministrie.functions;

public abstract interface Callback<T>{
	
	public abstract void done(T arg, Throwable error);
	
	public static <T> Callback<T> emptyCallback(){
		return new Callback<T>(){
			@Override
			public void done(T arg, Throwable error){}
		};
	}
}