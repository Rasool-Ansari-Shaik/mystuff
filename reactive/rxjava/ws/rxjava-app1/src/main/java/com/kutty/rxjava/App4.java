package com.kutty.rxjava;

import io.reactivex.Observable;
import io.reactivex.observables.ConnectableObservable;

// Cold: can be emitted many time. Hot:Just emit once. Connected: Only emit after connected
public class App4 {

	public static void main(String[] args) {
//		createColdObservable();
		createConnctedObservable();
	}
	
	private static void createColdObservable() {
		Observable<Integer> observable = Observable.just(1,2,3,4,5);
		observable.subscribe(i -> System.out.println("Observer1: "+i));
//		pause(3000);
		observable.subscribe(i -> System.out.println("Observer2: "+i));
	}
	
	private static void createConnctedObservable() {
		ConnectableObservable<Integer> observable = Observable.just(1,2,3,4,5).publish();
		observable.subscribe(i -> System.out.println("Observer1: "+i));
		observable.connect();
		observable.subscribe(i -> System.out.println("Observer2: "+i));
	}

}
