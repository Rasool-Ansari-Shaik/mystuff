package com.kutty.rxjava;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.ResourceObserver;

//15 RxJava - Disposable and Composite Disposable - Why should we dispose?
public class App12 {

	public static void main(String[] args) {
//		handleDisposable();
//		handleDisposableInObserver();
//		handleDisposableOutsideObserver();
		compositeDisposable();
	}
	
	private static void handleDisposable() { 
		Observable<Long> observable = Observable.interval(1, TimeUnit.SECONDS);
		Disposable disposable = observable.subscribe(i -> System.out.println("Observer1: "+i), System.out::println, () -> System.out.println("complete"));
		pause(5000);
		disposable.dispose();
		pause(5000);
	}
	
	private static void handleDisposableInObserver() {
		Observable<Integer> observable = Observable.just(1,2,3,4,5);
		Observer<Integer> observer = new Observer<Integer>() {
			Disposable disposable;
			@Override
			public void onSubscribe(Disposable d) {
				disposable = d;
			}

			@Override
			public void onNext(Integer t) {
				if (t == 4)
					disposable.dispose();
				System.out.println(t);
			}

			@Override
			public void onError(Throwable e) {
				System.out.println("Error: "+e.getLocalizedMessage());
			}

			@Override
			public void onComplete() {
				System.out.println("Complete");
			}
		};
		
		observable.subscribe(observer);
		
	}

	private static void handleDisposableOutsideObserver() {
		Observable<Integer> observable = Observable.just(1,2,3,4,5);
		ResourceObserver<Integer> observer = new ResourceObserver<Integer>() {

			@Override
			public void onNext(Integer t) {
				System.out.println(t);
			}

			@Override
			public void onError(Throwable e) {
				System.out.println("Error: "+e.getLocalizedMessage());
			}

			@Override
			public void onComplete() {
				System.out.println("Complete");
			}
		};
		
		Disposable disposable = observable.subscribeWith(observer);
		disposable.dispose();		
	}

	private static void compositeDisposable() {
		CompositeDisposable compositeDisposable = new CompositeDisposable();
		Observable<Long> observable = Observable.interval(1, TimeUnit.SECONDS);
		Disposable disposable1 = observable.subscribe(i -> System.out.println("Observer1: "+i), System.out::println, () -> System.out.println("Observer1 completed"));
		Disposable disposable2 = observable.subscribe(i -> System.out.println("Observer2: "+i), System.out::println, () -> System.out.println("Observer2 completed"));
		compositeDisposable.addAll(disposable1, disposable2);
		pause(3000);
		compositeDisposable.delete(disposable1);
		compositeDisposable.dispose();
		pause(3000);
	}
	
	private static void pause(long duration) {
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
