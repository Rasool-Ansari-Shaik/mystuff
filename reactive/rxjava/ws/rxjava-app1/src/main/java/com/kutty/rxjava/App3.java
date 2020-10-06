package com.kutty.rxjava;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class App3 {

	public static void main(String[] args) {
		Observable<Integer> observable = Observable.just(1,2,3,4,null,6,7);
		
		Observer<Integer> observer = new Observer<Integer>() {

			@Override
			public void onSubscribe(Disposable d) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onNext(Integer t) {
				System.out.println(t);
			}

			@Override
			public void onError(Throwable e) {
				System.out.println("Errrrrrr: " +e.getLocalizedMessage());
			}

			@Override
			public void onComplete() {
				System.out.println("Completed");
			}
		};
		
		observable.subscribe(observer);
	}

}
