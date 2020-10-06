package com.kutty.rxjava;

import java.io.IOException;

import io.reactivex.Observable;

// 25 RxJava - Error Handling Operators - Part 1
public class App21 {

	public static void main(String[] args) {
//		tryDoOnError();
//		tryOnErrorResumeNext();
//		tryOnErrorReturn();
		tryOnErrorReturnItem();
	}
	
	private static void tryDoOnError() {
		Observable.error(new Exception("Got exception"))
			.doOnError(error -> System.out.println("AT Do Error occured: "+ error.getMessage()))
			.subscribe(System.out::println,
					error -> System.out.println("Error occured: "+ error.getMessage()),
					() -> System.out.println("Completed"));
	}
	
	private static void tryOnErrorResumeNext() {
		Observable.error(new Exception("Exception occured"))
			.onErrorResumeNext(Observable.just(1,2,3,4,5))
			.subscribe(System.out::println,
					error -> System.out.println(error.getMessage()),
					() -> System.out.println("Completed"));
	}
	
	private static void tryOnErrorReturn() {
		Observable.error(new Exception("Exception occured"))
			.onErrorReturn(error -> {
				if (error instanceof IOException)
					return "IOE";
				else
					return "Ex";
			})
			.subscribe(System.out::println,
					error -> System.out.println(error.getMessage()),
					() -> System.out.println("Completed"));
	}

	private static void tryOnErrorReturnItem() {
		Observable.error(new Exception("Exception occured"))
			.onErrorReturnItem(100)
			.subscribe(System.out::println,
					error -> System.out.println(error.getMessage()),
					() -> System.out.println("Completed"));
	}
}
