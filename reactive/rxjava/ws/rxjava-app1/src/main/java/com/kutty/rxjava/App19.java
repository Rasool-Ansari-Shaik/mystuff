package com.kutty.rxjava;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

// 23 RxJava - Observable Delay Same as Observable interval?
public class App19 {
	
	public static void main(String[] args) {
//		tryDelay();
		tryDelayError();
	}
	
	private static void tryDelay() {
		Observable<Integer> observable = Observable.just(1,2,3,4,5);
		observable
			.delay(3000, TimeUnit.MILLISECONDS)
			.subscribe(System.out::println);
		pause(5000);
	}
	
	private static void tryDelayError() {
		Observable.error(new Exception("Error"))
			.delay(3, TimeUnit.SECONDS,true)
			.subscribe(System.out::println,
					   System.out::println,
					   () -> System.out.println("Complete"));
		pause(5000);
	}
	
	private static void pause(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
