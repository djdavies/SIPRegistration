package org.example.servlet.sip;

import java.time.LocalDateTime;

import javax.servlet.sip.URI;

public class RegistryEntry {
	private URI contact;
	private LocalDateTime expiryTime;
	
	public URI getContact() {
		return contact;
	}
	
	public LocalDateTime getExpiryTime() {
		return this.expiryTime;
	}
	
	public void setContact(URI contact) {
		this.contact = contact;
	}
	
	public void setExpiryTime(LocalDateTime expiryTime) {
		this.expiryTime = expiryTime;
	}
}
