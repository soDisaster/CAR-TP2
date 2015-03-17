package com.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/res")
public class RessourcesFichiers {

	 @GET
	 @Path("/test")
	 @Produces("text/html")
	 public String getBook( @PathParam("isbn") String isbn ) {
		 return "<h1>Hello World</h1>";
	 }
	 
	 
	 
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
