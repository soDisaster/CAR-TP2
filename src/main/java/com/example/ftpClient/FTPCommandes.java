package com.example.ftpClient;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class FTPCommandes {

	private BufferedReader in;
	private DataOutputStream out;
	private ServerSocket serv;

	public FTPCommandes(BufferedReader in, DataOutputStream out) {
		this.in=in;
		this.out=out;
	}


	public void CMDUSER(String s) throws IOException{
		out.write(new String("USER "+s+"\n").getBytes());
		read("331");



	}


	public void CMDPASS(String s) throws IOException{
		out.write(new String("PASS "+s+"\n").getBytes());
		read("230");



	}
	public void read(String code) throws IOException{
		String s = "";
		boolean codeOK=false;

		while(!codeOK){
			s=in.readLine();
			if(s != null){
				if(s.equals(code)){
					codeOK=true;
					System.out.println(s);
				}

			}

		}
	}

	public void CMDQUIT(String s) throws IOException{
		out.write(new String("QUIT\n").getBytes());
	}

	public  String CMDLIST(String file) throws IOException{
		ServerSocket server =CMDPORT("127,0,0,1");
		out.write(new String("LIST\n").getBytes());
		read("150");
		Socket soc = server.accept();

		DataInputStream r = new DataInputStream(soc.getInputStream());

		byte [] buffer = new byte[soc.getReceiveBufferSize()];
		int nbOfbyte;
		String result="";

		while((nbOfbyte = r.read(buffer))>0){
			result = new String(buffer);
		}
		

		
		server.close();
		read("226");
		return result;
	}




	public void CMDRETR(String file) throws IOException{
		ServerSocket server =CMDPORT("127,0,0,1");
		out.write(new String("RETR " +file+"\n").getBytes());
		read("150");
		Socket soc = server.accept();

		DataInputStream r = new DataInputStream(soc.getInputStream());

		try{
			FileOutputStream br = new FileOutputStream(new File("data/"+file));


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
		read("226");
	}


	public void CMDSTOR(String file) throws IOException{
		ServerSocket server =CMDPORT("127,0,0,1");
		out.write(new String("STOR " +file+"\n").getBytes());
		read("150");
		Socket soc = server.accept();

		DataOutputStream d = new DataOutputStream(soc.getOutputStream());

		try{
			FileInputStream br = new FileInputStream(new File("data/"+file));

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
		read("226");
	}

	public ServerSocket CMDPORT(String addr) throws IOException{

		Random r =new Random();
		int port =r.nextInt(200);

		out.write(new String("PORT "+addr+","+port+",42\n").getBytes());
		read("225");	

		port *= 256;
		port += 42;

		return new ServerSocket(port);

	}
	public void CMDCWD(String path) throws IOException{

		
		out.write(new String("CWD "+path+"\n").getBytes());
		read("250");	


	}

}
