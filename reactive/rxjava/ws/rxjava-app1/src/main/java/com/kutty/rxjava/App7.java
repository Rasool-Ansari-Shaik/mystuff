package com.kutty.rxjava;

import io.reactivex.Observable;

public class App7 {

	public static void main(String[] args) {
		Observable<Integer> observable = Observable.range(0, 10);
		observable.subscribe(System.out::println);
	}

}
