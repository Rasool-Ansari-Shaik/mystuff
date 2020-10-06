package com.kutty.rxjava;

import io.reactivex.Observable;

public class App1 {

	public static void main(String[] args) {

		Observable.just(1,2,3,4,5)
				  .subscribe(System.out::println);

	}

}
