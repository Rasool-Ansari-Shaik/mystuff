package com.kutty.rxjava;

import io.reactivex.Observable;

public class App9 {

	public static void main(String[] args) {
		Observable<Integer> observable = Observable.fromCallable(() -> getNumber());
		observable.subscribe(System.out::println, System.out::println, () -> System.out.println("complete"));
	}
	
	private static int getNumber() {
		System.out.println("Creating a number");
		return 3 / 0;
	}

}
