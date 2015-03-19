package com.example.ftpClient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author Anne-Sophie et Thibault
 */

/**
 * Classe FTPClient
 */
public class FTPClient{
	
	/**
	 * Attributs d'instance.
	 */
	
	/** 
	 * Socket de connexion
	 */
	private Socket co;
	/** 
	 * Port de connexion
	 */
	private int port = 9999;
	
	/** 
	 * Adresse locale
	 */
	private String addesse = "127.0.0.1";
	/** 
	 * Lecture
	 */
	private BufferedReader in;
	/** 
	 * Ecriture
	 */
	private DataOutputStream out;
	
	/** 
	 * Commandes FTP
	 */
	private FTPCommandes commandes;
	
	
	
	
	/** 
	 * Constructeur
	 */
	public FTPClient() throws UnknownHostException, IOException{
		
		// Socket de connexion avec pour paramètres adresse (127.0.0.1) et port.
		this.co = new Socket(InetAddress.getByName(addesse),port);
		
		// Lecture
		in = new BufferedReader(new InputStreamReader(co.getInputStream()));
		//Ecriture
		out = new DataOutputStream(co.getOutputStream());
		
		// Pour "attraper" le premier code de connexion (220)
		String s = in.readLine();
		System.out.println(s);
		commandes= new FTPCommandes(in, out);	
	}	
	
	/**
	 * Accesseur commandes
	 * @return FTPCommandes
	 */
	public FTPCommandes getCommandes(){
		return this.commandes;
	}
	
	/**
	 * Main, creation d'un FTPClient
	 * @param args
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public static void main (String args[]) throws UnknownHostException, IOException{
		
		@SuppressWarnings("unused")
		FTPClient client = new FTPClient();
	}
}