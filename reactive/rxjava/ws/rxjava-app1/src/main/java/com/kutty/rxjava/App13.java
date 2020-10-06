package com.kutty.rxjava;

import io.reactivex.Observable;

// 16 RxJava - Introduction to Operator - map() and filter()
public class App13 {

	public static void main(String[] args) {
//		mapOperator();
//		mapOperatorReturnDifferentData();
//		filterOperator();
		combineMapAndFilter();
	}
	
	private static void mapOperator() {
		Observable<Integer> observable = Observable.just(1,2,3,4,5);
		observable.map(item -> item * 2)
				.subscribe(System.out::println);
	}
	
	private static void mapOperatorReturnDifferentData() {
		Observable<Integer> observable = Observable.just(1,2,3,4,5);
		observable.map(item -> "Hello World!")
				  .subscribe(System.out::println);
	}
	
	private static void filterOperator() {
		Observable<Integer> observable = Observable.just(1,2,3,4,5);
		observable.filter(item -> false)
				.subscribe(System.out::println);
	}
	
	private static void combineMapAndFilter() {
		Observable<Integer> observable = Observable.just(1,2,3,4,5);
		observable.filter(item -> item % 2 == 0)
				.map(item -> item * 2)
				.subscribe(System.out::println);
	}

}
