package org.example.servlet.sip;

import javax.servlet.sip.URI;

public class RegistryEntry {
	private URI contact;
	private int expiryTime;
	
	public URI getContact() {
		return contact;
	}
	
	public int getExpiryTime() {
		return this.expiryTime;
	}
	
	public void setContact(URI contact) {
		this.contact = contact;
	}
	
	public void setTimeStamp(int expiryTime) {
		this.expiryTime = expiryTime;
	}
}
