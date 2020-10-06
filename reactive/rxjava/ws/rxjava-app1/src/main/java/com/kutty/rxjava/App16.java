package com.kutty.rxjava;

import io.reactivex.Observable;

// 19 RxJava - Operators - defaultIfEmpty and switchIfEmpty
public class App16 {

	public static void main(String[] args) {
		tryDefaultIfEmpty();
		trySwitchIfEmpty();
	}
	
	private static void tryDefaultIfEmpty() {
		Observable<Integer> observable = Observable.just(1,2,3,4,5);
		observable
			.filter(item -> item > 10)
			.defaultIfEmpty(20)
			.subscribe(System.out::println);
	}
	
	private static void trySwitchIfEmpty() {
		Observable<Integer> observable = Observable.just(1,2,3,4,5);
		observable
			.filter(item -> item > 10)
			.switchIfEmpty(Observable.just(6,7,8,9,0))
			.subscribe(System.out::println);
	}

}
