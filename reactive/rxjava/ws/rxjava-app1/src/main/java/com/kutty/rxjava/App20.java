package com.kutty.rxjava;

import io.reactivex.Observable;

// 24 RxJava - Operators - Contains
public class App20 {

	public static void main(String[] args) {
//		tryContainsWithPrimitives();
		tryContainsWithNonPrimitives();
	}
	
	private static void tryContainsWithPrimitives() {
		Observable.just(1,2,3,4,5)
			.contains(12)
			.subscribe(System.out::println);
	}
	
	private static void tryContainsWithNonPrimitives() {
		User user = new User("Rasool");
		Observable.just(user)
			.contains(new User("Rasool"))
			.subscribe(System.out::println);
			
	}

}

class User {
	String name;
	User(String name){
		this.name = name;
	}
}
