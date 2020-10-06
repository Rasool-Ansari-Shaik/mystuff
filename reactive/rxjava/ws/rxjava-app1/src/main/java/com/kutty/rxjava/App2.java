package com.kutty.rxjava;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;

public class App2 {

	public static void main(String[] args) {
//		createObservableUsingJust();
//		createObservableUsingIterable();
		createObservableUsingCreate();
	}
	
	private static void createObservableUsingJust() {
		System.out.println("createObservableUsingJust");
		Observable<Integer> observable = Observable.just(1, 2, 3, 4, 5);
		observable.subscribe(item -> System.out.println(item));
	}
	
	private static void createObservableUsingIterable() {
		System.out.println("createObservableUsingIterable");
		List<Integer> list = Arrays.asList(1,2,3,4,5);
		Observable<Integer> observable = Observable.fromIterable(list);
		observable.subscribe(System.out::println);
	}
	
	private static void createObservableUsingCreate() {
		System.out.println("createObservableUsingEmitter");
		Observable<Integer> observable = Observable.create(emitter -> {
				emitter.onNext(1);
				emitter.onNext(2);
				emitter.onNext(3);
				emitter.onComplete();
				emitter.onNext(4);
				emitter.onNext(5);
				emitter.onNext(null);
//				emitter.onComplete();
			}
		);
		
		observable.subscribe(item -> System.out.println(item),
				error -> System.out.println("Error occured: "+ error.getMessage()),
				() -> System.out.println("Complete")
			);
		
	}

}
