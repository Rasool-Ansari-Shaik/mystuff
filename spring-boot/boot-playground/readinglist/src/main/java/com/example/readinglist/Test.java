package com.example.readinglist;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Parse date time in UTC
		String received = "2020-08-01T19:15:25.864Z"; // Date Time in UTC
		ZonedDateTime dateBefore = ZonedDateTime.ofInstant(Instant.parse(received), ZoneOffset.UTC);
		System.out.println(dateBefore.toString());
		
		// Get Current date time in UTC
		ZonedDateTime dateAfter = ZonedDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
		System.out.println(dateAfter.toString());
		
		long noOfDaysBetween = ChronoUnit.DAYS.between(dateBefore, dateAfter);
		System.out.println(noOfDaysBetween);

	}

}
