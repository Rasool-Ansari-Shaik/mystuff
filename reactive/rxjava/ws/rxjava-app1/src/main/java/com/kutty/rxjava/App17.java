package com.kutty.rxjava;

import io.reactivex.Observable;

// 20 RxJava - Operators - repeat and scan
public class App17 {

	public static void main(String[] args) {
//		tryRepeat();
//		tryScan();
		tryScanWithInitialValue();
	}
	
	private static void tryRepeat() {
		Observable<Integer> observable = Observable.just(1,2,3,4,5);
		observable
			.repeat(3)
			.subscribe(System.out::println);
	}
	
	private static void tryScan() {
		Observable<Integer> observable = Observable.just(1,2,3,4,5);
		observable
			.scan((accumulator, next) -> accumulator + next)
			.subscribe(System.out::println);
	}
	
	private static void tryScanWithInitialValue() {
		Observable<Integer> observable = Observable.just(1,2,3,4,5);
		observable
			.scan(10, (accumulator, next) -> accumulator + next)
			.subscribe(System.out::println);
	}

}
