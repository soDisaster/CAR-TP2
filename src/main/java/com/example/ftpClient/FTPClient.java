package com.example.ftpClient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class FTPClient{
	
	private Socket co;
	private int port = 9999;
	private String addesse = "127.0.0.1";
	private BufferedReader in;
	private DataOutputStream out;
	private FTPCommandes commandes;

	public FTPClient() throws UnknownHostException, IOException{
		this.co = new Socket(InetAddress.getByName(addesse),port);
		
		in = new BufferedReader(new InputStreamReader(co.getInputStream()));
		out = new DataOutputStream(co.getOutputStream());
		
		String s = in.readLine();
		
		
		System.out.println(s);
		
		commandes= new FTPCommandes(in, out);
		
		commandes.CMDUSER("rosa");
		commandes.CMDPASS("Azerty2");
		
		
	}	
	
	public FTPCommandes getCommandes(){
		return this.commandes;
	}
	
	
	public static void main (String args[]) throws UnknownHostException, IOException{
		
		@SuppressWarnings("unused")
		FTPClient client = new FTPClient();
	}
}