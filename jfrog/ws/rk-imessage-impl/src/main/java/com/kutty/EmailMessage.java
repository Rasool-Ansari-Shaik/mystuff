package com.kutty;

import com.google.gson.JsonObject;
import com.rashaik.IMessage;

public class EmailMessage implements IMessage {

	public void sendMessage(JsonObject jsonObject) {
		System.out.println("Email Message");
	}
	
}
