package com.kutty.rxjava;

import io.reactivex.Observable;

public class App6 {

	public static void main(String[] args) {
		createObservableUsingEmpty();
		createObservableUsingNever();
	}
	
	private static void createObservableUsingEmpty() {
		Observable<Integer> observable = Observable.empty();
		observable.subscribe(System.out::println, System.out::println, () -> System.out.println("Completed"));
	}

	private static void createObservableUsingNever() {
		Observable<Integer> observable = Observable.never();
		observable.subscribe(System.out::println, System.out::println, () -> System.out.println("Completed"));
	}
}
