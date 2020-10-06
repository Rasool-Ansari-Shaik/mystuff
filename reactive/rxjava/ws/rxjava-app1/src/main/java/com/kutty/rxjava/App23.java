package com.kutty.rxjava;

import io.reactivex.Observable;

// 27 Rxjava - Do/Action Operators
public class App23 {
	
	public static void main(String[] args) {
//		tryDoOnSubscribe();
//		tryDoOnNext();
		tryDoOnComplete();
	}
	
	// Do some action before executing all items in subscribe
	private static void tryDoOnSubscribe() {
		Observable.just(1,2,3,4,5)
			.doOnSubscribe(disposable -> System.out.println("Do on Subscribe"))
			.subscribe(System.out::println,
					error -> System.out.println("Error: "+ error.getMessage()),
					() -> System.out.println("Completed"));
	}
	
	// Do some action before executing "each" item in subscribe
	private static void tryDoOnNext() {
		Observable.just(1,2,3,4,5)
			.doOnNext(item -> System.out.println("Do On Next: "+ ++item)) // eventhough we change the item value here, it won't change the actual value in subscribe
			.subscribe(System.out::println,
					error -> System.out.println("Error: "+ error.getMessage()),
					() -> System.out.println("Complete"));
	}
	
	// Do some action after executing all items after subscribe and before complete
	private static void tryDoOnComplete() {
		Observable.just(1,2,3,4,5)
			.doOnComplete(() -> System.out.println("Do On Complete"))
			.subscribe(System.out::println,
					error -> System.out.println("Error: "+ error.getMessage()),
					() -> System.out.println("Complete"));
	}

}
