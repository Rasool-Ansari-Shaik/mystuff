package com.kutty.rxjava;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;

// 14 RxJava - Single, Maybe and Completable
public class App11 {

	public static void main(String[] args) {
		createSingle();
		createMaybe();
		createCompletable();
	}
	
	private static void createSingle() {
		Single.just("Hello World")
			  .subscribe(System.out::println);
	}
	
	private static void createMaybe() {
		Maybe.empty()
			 .subscribe(new MaybeObserver() {

				@Override
				public void onSubscribe(Disposable d) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSuccess(Object t) {
					System.out.println(t);
				}

				@Override
				public void onError(Throwable e) {
					System.out.println(e);
				}

				@Override
				public void onComplete() {
					System.out.println("Complete");
				}});
	}
	
	private static void createCompletable() {
		Completable.fromSingle(Single.just("Hello World"))
			  .subscribe(() -> System.out.println("Done"));
	}

}
