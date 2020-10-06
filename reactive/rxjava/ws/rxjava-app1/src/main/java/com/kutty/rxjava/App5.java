package com.kutty.rxjava;

import io.reactivex.Observable;

public class App5 {

	public static void main(String[] args) {
		throwException();
		throwExceptionUsingCallable();
	}
	
	private static void throwException() {
		Observable<Integer> observable = Observable.error(new Exception());
		observable.subscribe(System.out::println, error -> System.out.println("Error1: "+error.hashCode()));
		observable.subscribe(System.out::println, error -> System.out.println("Error2: "+error.hashCode()));
	}
	
	private static void throwExceptionUsingCallable() {
		Observable<Integer> observable = Observable.error(() -> {
			System.out.println("New Exception Created");
			return new Exception();	
		});
		observable.subscribe(System.out::println, error -> System.out.println("Error1: "+error.hashCode()));
		observable.subscribe(System.out::println, error -> System.out.println("Error2: "+error.hashCode()));
	}
}
