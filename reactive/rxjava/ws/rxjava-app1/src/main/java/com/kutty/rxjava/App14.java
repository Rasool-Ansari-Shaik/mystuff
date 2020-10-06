package com.kutty.rxjava;

import io.reactivex.Observable;

// 17 RxJava - Operators - take(), skip(), takeWhile(), skipWhile()
public class App14 {

	public static void main(String[] args) {
//		tryTake();
//		trySkip();
//		tryTakeWhile();
		trySkipWhile();
	}
	
	private static void tryTake() {
		Observable<Integer> observable = Observable.just(1,2,3,4,5);
		observable
			.take(2) // count
			.subscribe(System.out::println);
	}
	
	private static void trySkip() {
		Observable<Integer> observable = Observable.just(1,2,3,4,5);
		observable	          
			.skip(3) // count
			.subscribe(System.out::println);
	}
	
	private static void tryTakeWhile() {
		Observable<Integer> observable = Observable.just(1,2,3,4,5,1,2,3,4,5);
		observable	          
			.takeWhile(item -> item <= 3)
			.subscribe(System.out::println);
	}
	
	private static void trySkipWhile() {
		Observable<Integer> observable = Observable.just(1,2,3,4,5,1,2,3,4,5);
		observable	          
			.skipWhile(item -> item <= 3)
			.subscribe(System.out::println);
	}
	
	private static void pause(long timeInterval) {
		
		try {
			Thread.sleep(timeInterval);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
