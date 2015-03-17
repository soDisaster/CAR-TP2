package com.example.ftpClient;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


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
	
	private void loop(){
		String cmd="";
		String ligne;
		Scanner sc = new Scanner(System.in);
		
		while (cmd.equals("QUIT")){
			
			ligne = sc.nextLine();
			
		}
	}
	public static void main (String args[]) throws UnknownHostException, IOException{
		
		FTPClient client = new FTPClient();
	}
}