package org.example.servlet.sip;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.Proxy;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.TooManyHopsException;
import javax.servlet.sip.URI;

public class RegisterBlock extends SipServlet {
	public ServletContext servletContext;
	
	// Annotation to create a SIP Factory object.
	@Resource
	SipFactory sipFactory;
	
	public void doInvite(SipServletRequest request) throws IOException {
		// Get aor URI from the HashMap.
		URI aorUri = request.getRequestURI();
		System.out.println("AOR URI from map: " + aorUri.toString());
		
		// Get the map.
		HashMap<URI, RegistryEntry> registryMap = 
				(HashMap<URI, RegistryEntry>) servletContext.getAttribute("registryMap");
		
		// If we can find our map...
		if (registryMap !=  null) {
			
			// Uri of the address to send/forward the request to.
			RegistryEntry sendRegisterEntry = registryMap.get(aorUri);
			if( null != sendRegisterEntry ) {
				try {
					// Get proxy.
					Proxy proxy = request.getProxy();
					// Proxy to forwardUri.
					proxy.proxyTo(sendRegisterEntry.getContact());
				} catch (TooManyHopsException e) {
					e.printStackTrace();
				}
			} else {
				// We know there is no IP with the given aorUri
				// so send back a error.
				request.createResponse(487).send();
			}
			
		} 
		// If the map isn't working etc...
		else {
			System.out.println("ERR: No map available");
		}
	} // end method.
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		// Assign & get servletContext.
		servletContext = this.getServletContext();
		
	}
	
	public void doRegister(SipServletRequest request) throws ServletParseException {
		// Get AOR.
		URI aor = request.getTo().getURI();
		System.out.println("AOR is: " + aor);
		
		// Use SIP factory so we can get URI from a String -- the contact header.
		String contactHeader = request.getHeader("Contact");
		contactHeader = contactHeader.replaceAll(">", "");
		contactHeader = contactHeader.replaceAll("<", "");
		
		System.out.println("contact header is: " + contactHeader);
		URI contact = this.sipFactory.createURI(contactHeader);
		
		System.out.println("Contact is: " + contact.toString());
		
		// Create registryMap using the attributes found in servletContext. 
		HashMap<URI, RegistryEntry> registryMap = (HashMap) servletContext.getAttribute("registryMap");
		
		// Check if map exists.
		if (registryMap == null) {
			System.out.println("No map found");
			registryMap = new HashMap<URI, RegistryEntry>();
		} 
		
		// TODO (for 2038) -- replace this method, otherwise UNIX timestamp will cause buffer overflow.
		LocalDateTime now = LocalDateTime.now();
		int expiryTime = now.getNano() + request.getExpires();

		// For adding new entries: contact, expiryTime.
		RegistryEntry registryEntry = new RegistryEntry();
		registryEntry.setContact(contact);
		registryEntry.setTimeStamp(expiryTime);
		
		// If entry with AOR exists, replace with new data. 
		if (registryMap.get(aor) != null) {
			registryMap.replace(aor, registryEntry);
			// Otherwise; create new entry.
		} else {
			// Putting an AOR contact record into the registryMap.
			registryMap.put(aor, registryEntry);
		}
		
		// Get servletContext, set Map as an attribute.
		servletContext.setAttribute("registryMap", registryMap);
				
		// Create response from the register request.
		try {
			// Send 200 OK.
			request.createResponse(200).send();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
