package com.example.ftpClient;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;


/**
 * @author Anne-Sophie et Thibault
 */

/**
 * Classe FTPCommandes
 */
public class FTPCommandes {

	/**
	 * Lecture
	 */
	private BufferedReader in;
	/**
	 * Ecriture
	 */
	private DataOutputStream out;
	/**
	 * Dossier courant
	 */
	private String currentLoc ;

	
	
	/**
	 * Constructeur FTPCommandes 
	 * @param in lecture
	 * @param out ecriture
	 */
	public FTPCommandes(BufferedReader in, DataOutputStream out) {
		this.in=in;
		this.out=out;
		this.currentLoc ="data";
	}
	
	
	/**
	 * Permet de lire le code retour du serveur FTP.
	 * @arg code, le code de retour
	 * @return vrai si le code n'est pas 550 ou 430 et qu'il a été lu correctement.
	 */
	public boolean read(String code) throws IOException{
		String s = "";
		boolean codeOK = false;

		while(!codeOK){
			s = in.readLine();
			if(s != null){
				if(s.equals(code)){
					codeOK=true;
					System.out.println(s);
					
				}
				
				if(s.equals("550") || s.equals("430") ){
					codeOK = true;
					System.out.println(s);
					return false;
				}

			}

		}
		return true;
	}
	


	/**
	 * Commande utilisateur
	 * Envoie USER au serveur FTP. 
	 * Attend de lire 331
	 * @param s Nom de l'utilisateur
	 */
	public void CMDUSER(String s) throws IOException{
		out.write(new String("USER "+s+"\n").getBytes());
		read("331");
	}


	/**
	 * Commande password
	 * Envoie PASS au serveur FTP. 
	 * Attend de lire 230
	 * @param s Mot de passe de l'utilisateur
	 */
	public boolean CMDPASS(String s) throws IOException{
		out.write(new String("PASS "+s+"\n").getBytes());
		
		if(!read("230")){
			return false;
		}
		
		return true;
	}
	

	/**
	 * Commande QUIT
	 * Envoie QUIT au serveur FTP. 
	 * Commande finale
	 * @param s Chaine vide.
	 */
	public void CMDQUIT(String s) throws IOException{
		out.write(new String("QUIT\n").getBytes());
	}

	
	/**
	 * Commande liste
	 * Envoie LIST au serveur FTP.
	 * Lorsqu'on gère des fichiers, on doit ouvrir une seconde socket.
	 * Le client accepte la connexion à cette socket. 
	 * Attend de lire 226
	 * Terminal : ls
	 * @param s Nom du repertoire
	 */
	public  String CMDLIST(String file) throws IOException{
		
		/*
		 * 
		 */
		ServerSocket server = CMDPORT("127,0,0,1");
		out.write(new String("LIST\n").getBytes());
		read("150");
		Socket soc = server.accept();

		DataInputStream r = new DataInputStream(soc.getInputStream());

		byte [] buffer = new byte[soc.getReceiveBufferSize()];
		String result="";

		while( r.read(buffer)>0){
			result = new String(buffer);
		}
		

		
		server.close();
		read("226");
		return result;
	}



	/**
	 * Commande RETR.
	 * Envoie RETR au serveur FTP. 
	 * Lorsqu'on gère des fichiers, on doit ouvrir une seconde socket.
	 * Le client accepte la connexion à cette socket. 
	 * Attend de lire 226
	 * Terminal : get
	 * Le client lit ce que le serveur envoie et l'écrit dans un fichier.
	 * @param s nom du fichier a recuperer
	 */
	public boolean CMDRETR(String file) throws IOException{
		ServerSocket server =CMDPORT("127,0,0,1");
		out.write(new String("RETR " +file+"\n").getBytes());
		read("150");
		Socket soc = server.accept();

		DataInputStream r = new DataInputStream(soc.getInputStream());

		try{
			FileOutputStream br = new FileOutputStream(new File(this.currentLoc+"/"+file));


			byte [] buffer = new byte[soc.getReceiveBufferSize()];
			int nbOfbyte;
			while((nbOfbyte = r.read(buffer))>0){
				br.write(buffer,0,nbOfbyte);
			}
			br.close();
			/* Data connection open; no transfer in progress.*/
		}catch(FileNotFoundException e){	
			/* Erreur */

		}


		server.close();
		if(!read("226")){
			return false;
		}
		return true;
	}

	/**
	 * Commande DELE
	 * Envoie DELE au serveur FTP. 
	 * Attend de lire 226
	 * Terminal : delete
	 * Le client lit ce que le serveur envoie et l'écrit dans un fichier.
	 * @param s Nom de l'utilisateur
	 */
	public boolean CMDDELE(String file) throws IOException{
		
		out.write(new String("DELE " +file+"\n").getBytes());
		
		if(!read("226")){
			return false;
		}
		return true;
	}

	/**
	 * Commande STOR.
	 * Envoie STOR au serveur FTP. 
	 * Lorsqu'on gère des fichiers, on doit ouvrir une seconde socket.
	 * Le client accepte la connexion à cette socket. 
	 * Attend de lire 226
	 * Terminal : put
	 * Le client envoie au serveur le fichier.
	 * @param s Nom du fichier à envoyer
	 */
	public boolean CMDSTOR(String file) throws IOException{
		ServerSocket server =CMDPORT("127,0,0,1");
		out.write(new String("STOR " +file+"\n").getBytes());
		read("150");
		Socket soc = server.accept();
		DataOutputStream d = new DataOutputStream(soc.getOutputStream());

		try{
			FileInputStream br = new FileInputStream(new File(this.currentLoc+"/"+file));

			byte [] buffer = new byte[soc.getReceiveBufferSize()];
			int nbOfbyte;
			while((nbOfbyte = br.read(buffer)) > 0){
				d.write(buffer,0,nbOfbyte);
			}
			d.flush();
			br.close();
			/* Data connection open; no transfer in progress.*/
		}catch(FileNotFoundException e){	
			/* Erreur */

		}
		server.close();
		if(!read("226")){
			
			return false;
		}
		return true;

		
		
	}

	
	/**
	 * Commande PORT
	 * Utile pour les commandes STOR, RETR et LIST
	 * Attend de lire 225
	 * Pour obtenir le numéro du port,
	 * on utilise un chiffre aléatoire que l'on multiplie par 256 et auquel on ajoute 42.
	 * @param s adresse
	 */
	public ServerSocket CMDPORT(String addr) throws IOException{

		Random r = new Random();
		int port = r.nextInt(200);

		out.write(new String("PORT "+addr+","+port+",42\n").getBytes());
		read("225");	

		port *= 256;
		port += 42;

		return new ServerSocket(port);
	}
	
	/**
	 * Commande CWD.
	 * Envoie CWD au serveur FTP. 
	 * Attend de lire 250
	 * Terminal : cd
	 * Le client lit ce que le serveur envoie et l'écrit dans un fichier.
	 * @param s Nom du chemin
	 */
	public boolean CMDCWD(String path) throws IOException{

		String newPath;
		if(path.charAt(0) =='/'){
			 newPath = path.substring(1);
		}
		else{
			newPath = this.currentLoc+"/"+path;
		}
		out.write(new String("CWD "+path+"\n").getBytes());

		if(!read("250")){
			return false;
		}
		this.currentLoc = newPath;
		return true;	


	}
	
	/**
	 * Accesseur au répertoire courant
	 * @return répertoie courant
	 */
	public String getCurrentDir(){
		return this.currentLoc;
	}

}
