package com.kutty.rxjava;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observables.ConnectableObservable;

public class App10 {

	public static void main(String[] args) {
//		createObservableUsingInterval(); // Cold Observable
		createObservableUsingIntervalConnected(); // Connected Observable
	}
	
	private static void createObservableUsingInterval() { 
		Observable<Long> observable = Observable.interval(1, TimeUnit.SECONDS);
		observable.subscribe(i -> System.out.println("Observer1: "+i), System.out::println, () -> System.out.println("complete"));
		pause(1000);
		observable.subscribe(i -> System.out.println("Observer2: "+i), System.out::println, () -> System.out.println("complete"));
		pause(2000);
	}
	
	private static void createObservableUsingIntervalConnected() {
		ConnectableObservable<Long> observable = Observable.interval(1, TimeUnit.SECONDS).publish();
		observable.subscribe(i -> System.out.println("Observer1: "+i), System.out::println, () -> System.out.println("complete"));
		observable.connect();
		pause(4000);
		observable.subscribe(i -> System.out.println("Observer2: "+i), System.out::println, () -> System.out.println("complete"));
		pause(3000);
//		pause(10000);
	}
	
	private static void pause(long duration) {
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
