package com.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.example.ftpClient.FTPClient;
import com.example.ftpClient.FTPCommandes;

@Path("/res")
public class RessourcesFichiers {

	FTPCommandes commandes;
	FTPClient client;

	public RessourcesFichiers() throws UnknownHostException, IOException{
		this.client = new FTPClient();
		this.commandes=this.client.getCommandes();



	}
	@GET
	@Path("/data")
	@Produces("text/html")
	public String getFiles() throws IOException {
		if(this.commandes.CMDCWD("/data")){
			return list(new File(this.commandes.getCurrentDir()));
		}
		return "<h1>PATH NOT FOUND</h1>";

	}


	@GET
	@Path("/data/{name}")
	@Produces("text/html")
	public String getFile(@PathParam( "name" ) String name)throws IOException  {
		if(this.commandes.CMDCWD("/data")){
			return searchFile(name);
		}
		return "<h1>PATH NOT FOUND</h1>";
	}

	@GET
	@Path("/data/{dir}/{name}")
	@Produces("text/html")
	public String getFile(@PathParam( "name" ) String name,@PathParam( "dir" ) String dir)throws IOException  {
		System.out.println(this.commandes.getCurrentDir());
		if(this.commandes.CMDCWD("/data/"+dir)){
			return searchFile(name);
		}
		return "<h1>PATH NOT FOUND</h1>";
	}



	public String searchFile(String name) throws IOException{
		System.out.println(name);
		File f;

		f=new File(this.commandes.getCurrentDir()+"/"+name);
		if(f.exists()){
			System.out.println(f);
			if(f.isFile()){
				System.out.println("Dans méthode isFile");
				return read(f);
			}

			if(f.isDirectory()){
				if(this.commandes.CMDCWD(name)){
					return list(f);
				}


			}
		}else{
			System.out.println("FileNotFound");
			boolean retour = this.commandes.CMDRETR(name);
			System.out.println(f);

			if(retour){
				System.out.println("Dans méthode isFile");
				return read(f);
			}

			if(!retour){
				f.delete();
				Files.createDirectory(f.toPath());
				System.out.println("dossier :" +f.isDirectory());
				if(this.commandes.CMDCWD(name)){
					return list(f);
				}
			}




		}
		return "<h1>FILE NOT FOUND</h1>";
	}




	public String read(File f) throws IOException{
		String result ="";
		FileInputStream br = new FileInputStream(f);

		byte [] buffer = new byte[(int) f.length()];

		while(br.read(buffer) > 0){
			result+= new String(buffer);
		}
		br.close();
		return result;
	}




	public String list(File f) throws IOException{
		String result="";
		System.out.println(this.commandes.getCurrentDir());
		result = this.commandes.CMDLIST("");
		String[] test = result.split("\r\n");
		result="";
		for(int i = 0; i < test.length - 1; i++){
			if(test[i].length()>0)
				result+="<a href='/rest/api/res/"+this.commandes.getCurrentDir()+"/"+test[i]+"'>" +  test[i] + "</a></br>";
		}
		result+="<a href='/rest/api/res/"+this.commandes.getCurrentDir()+"/..'>..</a></br>";
		System.out.println("here");
		return result ;

	}


}
