package com.rest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;

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
		String result =commandes.CMDLIST("");
		String[] test = result.split("\r\n");
		result="";
		for(int i = 0; i < test.length - 1; i++){
			if(test[i].length()>0)

				result+="<a href='"+test[i]+"'>" +  test[i] + "</a></br>";
		}
		return result ;
	}


	@GET
	@Path("/{name}")
	@Produces("text/html")
	public String getFile(@PathParam( "name" ) String name)throws IOException  {
		System.out.println(name);
		File f;
		try{
			f=new File("data/"+name);
			System.out.println(f);
			if(f.isFile()){
				System.out.println("Dans méthode isFile");
				return "file lu";
			}

			if(f.isDirectory()){
				this.commandes.CMDCWD(name);
				String result = this.commandes.CMDLIST("");
				String[] test = result.split("\r\n");
				result="";
				for(int i = 0; i < test.length - 1; i++){
					if(test[i].length()>0)
						result+="<a href='data/"+name+"/"+test[i]+"'>" +  test[i] + "</a></br>";
				}
				return result ;


			}
		}catch(FileNotFoundException e){
			System.out.println("FileNotFound");
			this.commandes.CMDRETR(name);
			f =new File("data/"+name);
			return "file tel et lu";
		}

		return "<h1>fail Bro</h1>";

	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
