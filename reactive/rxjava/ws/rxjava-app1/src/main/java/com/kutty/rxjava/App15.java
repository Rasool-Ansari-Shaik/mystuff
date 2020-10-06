package com.kutty.rxjava;

import io.reactivex.Observable;

// 18 RxJava - Operators - distinct and distinctUntilChanged
public class App15 {

	public static void main(String[] args) {
//		tryDistinct();
//		tryDistinctwithKeySelector();
//		tryDistinctUntilChanged();
		tryDistinctUntilChangedWithKeySelector();
	}
	
	private static void tryDistinct() {
		Observable<Integer> observable = Observable.just(1,2,3,4,5,1,2);
		observable
			.distinct()
			.subscribe(System.out::println);
	}
	
	private static void tryDistinctwithKeySelector() {
		Observable<String> observable = Observable.just("abc", "abcd", "abcde", "efrg");
		observable
			.distinct(item -> item.length())
			.subscribe(System.out::println);
	}
	
	private static void tryDistinctUntilChanged() {
		Observable<Integer> observable = Observable.just(1,1,2,2,3,4,5,1,2);
		observable
			.distinctUntilChanged() // check for consequetive elements
			.subscribe(System.out::println);
	}
	
	private static void tryDistinctUntilChangedWithKeySelector() {
		Observable<String> observable = Observable.just("abc", "abcd", "abcde", "efrg","sdff");
		observable
			.distinctUntilChanged(String::length) // check for consequetive elements
			.subscribe(System.out::println);
	}

}
