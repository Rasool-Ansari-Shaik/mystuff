package com.kutty.rxjava;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;

public class App22 {

	public static void main(String[] args) {
//		tryRetryWithPredicate();
//		tryRetry();
		tryRetryUntil();
	}
	
	private static void tryRetryWithPredicate() {
		Observable.error(new Exception("Exception occured"))
			.retry(error ->
			{
				if (error instanceof IOException)
				{
					System.out.println("Retry");
					return true;
				}
				return false;
			}
					)
			.subscribe(System.out::println,
					error -> System.out.println(error.getMessage()),
					() -> System.out.println("Completed"));
	}

	private static void tryRetry() {
		Observable.error(new Exception("Exception occured"))
			.retry(3)
			.subscribe(System.out::println,
					error -> System.out.println(error.getMessage()),
					() -> System.out.println("Completed"));
	}

	private static void tryRetryUntil() {
		AtomicInteger atomicInteger = new AtomicInteger();
		Observable.error(new Exception("Exception occured"))
			.doOnError(i -> {
				System.out.println(atomicInteger.getAndIncrement());
			})
			.retryUntil(() -> {
				System.out.println("Retrying...");
				return atomicInteger.get() >= 3;
			})
			.subscribe(System.out::println,
					error -> System.out.println(error.getMessage()),
					() -> System.out.println("Completed"));
	}
}
