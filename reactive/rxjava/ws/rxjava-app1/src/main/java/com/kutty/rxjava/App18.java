package com.kutty.rxjava;

import java.util.Comparator;

import io.reactivex.Observable;

// 21 RxJava - Operators - Sorted - 3 ways to Sort Anything
public class App18 {

	public static void main(String[] args) {
//		trySorted();
//		trySortedWithComparator();
		trySortedWithNonComparator();
	}
	
	private static void trySorted() {
		Observable<Integer> observable = Observable.just(3,2,5,4,1);
		observable
			.sorted()
			.subscribe(System.out::println);
	}
	
	private static void trySortedWithComparator() {
		Observable<Integer> observable = Observable.just(3,2,5,4,1);
		observable
			.sorted(Comparator.reverseOrder())
			.subscribe(System.out::println);
	}
	
	private static void trySortedWithNonComparator() {
		Observable<String> observable = Observable.just("abcd","adb","adbcd");
		observable
			.sorted((first, next) -> Integer.compare(first.length(), next.length()))
			.subscribe(System.out::println);
	}

}
