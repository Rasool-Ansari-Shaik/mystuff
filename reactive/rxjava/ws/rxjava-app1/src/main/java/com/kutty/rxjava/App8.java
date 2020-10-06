package com.kutty.rxjava;

import io.reactivex.Observable;

public class App8 {

	static int start=5, count=2;
	public static void main(String[] args) {
		
		Observable<Integer> observable = Observable.defer(() -> Observable.range(start, count));
		observable.subscribe(System.out::println, System.out::println,() -> System.out.println("completed"));
		count=3;
		observable.subscribe(System.out::println, System.out::println,() -> System.out.println("completed"));
	}

}
