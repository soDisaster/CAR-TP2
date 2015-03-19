package com.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.example.ftpClient.FTPClient;
import com.example.ftpClient.FTPCommandes;

@Path("/res")
public class RessourcesFichiers {

	private FTPCommandes commandes;
	private FTPClient client;
	private boolean login;

	public RessourcesFichiers() throws UnknownHostException, IOException{
		this.client = new FTPClient();
		this.commandes=this.client.getCommandes();
		this.login = true;
		//this.commandes.CMDSTOR("dd.txt");


	}





	/*------------------------------------LOG IN/OUT ---------------------------------------------*/
	@GET
	@Path("/login")
	@Produces("text/html")
	public String logIn() throws IOException {

		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
				"<html xmlns=\"<xmlns />\" xml:lang=\"en\">"+
				"<head>" +
				"<title>Login</title>" +
				" </head>" +
				"<body>" +
				" <form action=\"/rest/api/res/data\" method=\"POST\">" +
				"<label for=\"name\">Name</label>" +
				"<input type=\"text\" name=\"name\" />" +
				"<br/>" +
				"<label for=\"name\">Password</label>" +
				"<input type=\"password\" name=\"pass\" />" +
				" <br/>" +
				"<input type=\"submit\" value=\"Submit\" />" +
				"</form>" +
				"</body>" +
				"</html>";

	}


	@POST
	@Path("/data")
	public String LogInAction(@FormParam( "name" )final String name,
			@FormParam( "pass" ) final String password )throws IOException  {

		commandes.CMDUSER(name);
		if(commandes.CMDPASS(password)){
			this.login = true;
			return getFiles();
		}
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<html xmlns=\"<xmlns />\" xml:lang=\"en\">"+
		"<head>" +
		"<title>Login</title>" +
		" </head>" +
		"<body>" +
		" <form action=\"/rest/api/res/login\" method=\"GET\">" +
		"<label for=\"name\">Login fail !</label>" +
		" <br/>" +
		"<input type=\"submit\" value=\"Submit\" />" +
		"</form>" +
		"</body>" +
		"</html>";
	}


	@GET
	@Path("/logout")
	@Produces("text/html")
	public String logOut() throws IOException {

		this.login = false;
		return logIn();

	}

	/*------------------------------------READ FILES AND LIST DIR ---------------------------------------------*/

	@GET
	@Path("/data")
	@Produces("text/html")
	public String getFiles() throws IOException {

		if(!login)
			return logIn();

		if(this.commandes.CMDCWD("/data")){
			return list(new File(this.commandes.getCurrentDir())) +"<form action=\"/rest/api/res/"+this.commandes.getCurrentDir()+"/add\">"+
					"<input type=\"submit\" value=\"Add File\">"+
					"</form>"+
					"<form action=\"/rest/api/res/logout\">"+
					"<input type=\"submit\" value=\"Logout\">"+
					"</form></br>";
		}
		return "<h1>PATH NOT FOUND</h1>";

	}


	@GET
	@Path("/data/{name}")
	@Produces("text/html")
	public String getFile(@PathParam( "name" ) String name)throws IOException  {

		if(!login)
			return logIn();

		if(this.commandes.CMDCWD("/data")){
			String result = searchFile(name);
			if(this.commandes.getCurrentDir().equals("data")){
				result += "<form action=\"/rest/api/res/"+this.commandes.getCurrentDir()+"/"+name+"/edit\">"+
						"<input type=\"submit\" value=\"Edit\">"+
						"</form></br>";
				result += "<button onclick= \"p()\">Delete</button>"+
						"<script type=\"text/javascript\">"+
						"function p(){"+	
						"console.log(\"Ici\");"+
						"xhr=window.ActiveXObject ? new ActiveXObject(\"Microsoft.XMLHTTP\") : new XMLHttpRequest();"+
						"xhr.onreadystatechange=function(){};"+
						"xhr.open(\"DELETE\", \"http://localhost:8080/rest/api/res/data/"+name+"\");"+
						"xhr.send(null);"+
						"};"+
						"</script>";
			}else{
				result +="<form action=\"/rest/api/res/"+this.commandes.getCurrentDir()+"/add\">"+
						"<input type=\"submit\" value=\"Add File\">"+
						"</form></br>";
				result +="<form action=\"/rest/api/res/logout\">"+
						"<input type=\"submit\" value=\"Logout\">"+
						"</form></br>";
			}
			return result;
		}
		return "<h1>PATH NOT FOUND</h1>";
	}

	@GET
	@Path("/data/{dir}/{name}")
	@Produces("text/html")
	public String getFile(@PathParam( "name" ) String name,@PathParam( "dir" ) String dir)throws IOException  {

		if(!login)
			return logIn();

		System.out.println(this.commandes.getCurrentDir());
		if(this.commandes.CMDCWD("/data/"+dir)){
			String result = searchFile(name);
			if(this.commandes.getCurrentDir().equals("data/"+dir)){
				result += "<form action=\"/rest/api/res/"+this.commandes.getCurrentDir()+"/"+name+"/edit\">"+
						"<input type=\"submit\" value=\"Edit\">"+
						"</form></br>";
				result += "<button onclick= \"p()\">Delete</button>"+
						"<script type=\"text/javascript\">"+
						"function p(){"+	
						"console.log(\"Ici\");"+
						"xhr=window.ActiveXObject ? new ActiveXObject(\"Microsoft.XMLHTTP\") : new XMLHttpRequest();"+
						"xhr.onreadystatechange=function(){};"+
						"xhr.open(\"DELETE\", \"http://localhost:8080/rest/api/res/data/"+dir+"/"+name+"\");"+
						"xhr.send(null);"+
						"};"+
						"</script>";
			}else{
				result +="<form action=\"/rest/api/res/"+this.commandes.getCurrentDir()+"/add\">"+
						"<input type=\"submit\" value=\"Add File\">"+
						"</form></br>";
				result +="<form action=\"/rest/api/res/logout\">"+
						"<input type=\"submit\" value=\"Logout\">"+
						"</form></br>";
			}
			return result;

		}
		return "<h1>PATH NOT FOUND</h1>";
	}

	/*--------------------------------------- EDIT FILES ------------------------------------------------*/

	@GET
	@Path("/data/{name}/edit")
	@Produces("text/html")
	public String editFile(@PathParam( "name" ) String name)throws IOException  {

		if(!login)
			return logIn();

		System.out.println(this.commandes.getCurrentDir());
		if(this.commandes.CMDCWD("/data")){
			return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
					"<html xmlns=\"<xmlns />\" xml:lang=\"en\">"+
					"<head>" +
					"<title>Edit file</title>" +
					" </head>" +
					"<body>" +
					" <form action=\"/rest/api/res/data/"+name+"\" method=\"POST\">" +
					"<label for=\"name\">Name</label>" +
					"<input type=\"text\" name=\"name\" value=\""+name+"\"/>" +
					"<br/>" +
					"Content :" +
					"<TEXTAREA NAME=\"content\" COLS=40 ROWS=6>"+searchFile(name)+"</TEXTAREA>" +
					" <br/>" +
					"<input type=\"submit\" value=\"Submit\" />" +
					"</form>" +
					"</body>" +
					"</html>";
		}
		return "<h1>PATH NOT FOUND</h1>";
	}
	@GET
	@Path("/data/{dir}/{name}/edit")
	@Produces("text/html")
	public String editFile(@PathParam( "name" ) String name,@PathParam( "dir" ) String dir)throws IOException  {

		if(!login)
			return logIn();

		System.out.println(this.commandes.getCurrentDir());
		if(this.commandes.CMDCWD("/data/"+dir)){
			return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
					"<html xmlns=\"<xmlns />\" xml:lang=\"en\">"+
					"<head>" +
					"<title>Edit file</title>" +
					" </head>" +
					"<body>" +
					" <form action=\"/rest/api/res/data/"+name+"\" method=\"POST\" >" +
					"<label for=\"name\">Name</label>" +
					"<input type=\"text\" name=\"name\" value=\""+name+"\"/>" +
					"<br/>" +
					"Content :" +
					"<TEXTAREA NAME=\"content\" COLS=40 ROWS=6>"+searchFile(name)+"</TEXTAREA>" +
					" <br/>" +
					"<input type=\"submit\" value=\"Submit\" />" +
					"</form>" +
					"</body>" +
					"</html>";

		}
		return "<h1>PATH NOT FOUND</h1>";
	}

	@POST
	@Path("/data/{name}")
	public String updateFile(@PathParam("name") final String fileName,
			@FormParam( "name" )final String name,
			@FormParam( "content" ) final String content )throws IOException  {

		if(!name.equals(fileName)){
			File f = new File(this.commandes.getCurrentDir()+"/"+fileName);
			f.delete();
			this.commandes.CMDDELE(fileName);
		}

		File f = new File(this.commandes.getCurrentDir()+"/"+name);
		write(f,content);
		this.commandes.CMDSTOR(name);
		return read(f);
	}


	@POST
	@Path("/data/{dir}/{name}")
	public String updateFile(@PathParam("name") final String fileName,@PathParam( "dir" ) String dir,
			@FormParam( "name" )final String name,
			@FormParam( "content" ) final String content )throws IOException  {

		if(!name.equals(fileName)){
			File f = new File(this.commandes.getCurrentDir()+"/"+fileName);
			f.delete();
			this.commandes.CMDDELE(fileName);
		}
		File f = new File(this.commandes.getCurrentDir()+"/"+name);
		write(f,content);
		this.commandes.CMDSTOR(name);
		return read(f);
	}

	/*--------------------------------------- ADD FILES ------------------------------------------------*/

	@GET
	@Path("/data/add")
	@Produces("text/html")
	public String addFile()throws IOException  {

		if(!login)
			return logIn();

		System.out.println(this.commandes.getCurrentDir());
		if(this.commandes.CMDCWD("/data")){
			return  "<!DOCTYPE html>"+
					 "<html>"+
					 "<head>	"+	
					 "<meta charset=\"utf-8\" />"+
					 "<title>Add File</title>"+

					"</head>"+
					"<body>"+
					"<label for=\"name\">Name</label>"+
					"<input type=\"text\" name=\"name\" id=\"name\"/>"+
					"<br/>"+
					"Content :"+
					"<TEXTAREA id=\"content\" NAME=\"content\" COLS=40 ROWS=6></TEXTAREA>"+
					"<br/>"+
					"<button onclick= \"p()\">Submit</button>"+
					"<script type=\"text/javascript\">"+
					"function p(){"+
					"$.ajax({"+
					   " url: 'http://localhost:8080/rest/api/res/data',"+
					   " type: 'PUT',"+
					    "success: function(result) {"+
					    "    // Do something with the result"+
					    "}"+
					"});"+
					"};"+
					"</script>"+	
					"</body>"+
					"</html>";
		}
		return "<h1>PATH NOT FOUND</h1>";
	}
	@GET
	@Path("/data/{dir}/add")
	@Produces("text/html")
	public String addFile(@PathParam( "dir" ) String dir)throws IOException  {

		if(!login)
			return logIn();

		System.out.println(this.commandes.getCurrentDir());
		if(this.commandes.CMDCWD("/data/"+dir)){
			return 	 "<!DOCTYPE html>"+
					 "<html>"+
					 "<head>	"+	
					 "<meta charset=\"utf-8\" />"+
					 "<title>Add File</title>"+

					"</head>"+
					"<body>"+
					"<label for=\"name\">Name</label>"+
					"<input type=\"text\" name=\"name\" id=\"name\"/>"+
					"<br/>"+
					"Content :"+
					"<TEXTAREA id=\"content\" NAME=\"content\" COLS=40 ROWS=6></TEXTAREA>"+
					"<br/>"+
					"<button onclick= \"p()\">Submit</button>"+
					"<script type=\"text/javascript\">"+
					"function p(){"+
					"var obj_send = {"+
					"name: document.getElementById('name').value,"+
					"content: document.getElementById('content').textContent"+
					"};"+		
					"console.log(\"Ici\");"+
					"xhr=window.ActiveXObject ? new ActiveXObject(\"Microsoft.XMLHTTP\") : new XMLHttpRequest();"+
					"xhr.onreadystatechange=function(){};"+
					"xhr.open(\"PUT\", \"http://localhost:8080/rest/api/res/data/"+dir+"\");"+
					"xhr.send(null);"+
					"};"+
					"</script>"+	
					"</body>"+
					"</html>";

		}
		return "<h1>PATH NOT FOUND</h1>";
	}

	@PUT
	@Path("/data")
	public String addFile(@FormParam( "name" )final String name,
			@FormParam( "content" ) final String content )throws IOException  {
		System.out.println(name +" : "+ content);
		File f = new File(this.commandes.getCurrentDir()+"/"+name);
		write(f,content);
		this.commandes.CMDSTOR(name);
		return getFiles();
	}


	@PUT
	@Path("/data/{dir}")
	public String addFile(@PathParam( "dir" ) String dir,
			@FormParam( "name" )final String name,
			@FormParam( "content" ) final String content )throws IOException  {

		File f = new File(this.commandes.getCurrentDir()+"/"+name);
		write(f,content);
		this.commandes.CMDSTOR(name);
		return getFile(dir);
	}
	/*--------------------------------------- DELETE FILES ------------------------------------------------*/



	@DELETE
	@Path("/data/{name}")
	public String deleteFile(@PathParam ( "name" )final String name)throws IOException  {

		File f = new File(this.commandes.getCurrentDir()+"/"+name);
		if(this.commandes.CMDDELE(name)){
			f.delete();
			return getFiles();
		}
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<html xmlns=\"<xmlns />\" xml:lang=\"en\">"+
		"<head>" +
		"<title>Delete</title>" +
		" </head>" +
		"<body>" +
		" <form action=\"/rest/api/res/data\" method=\"GET\">" +
		"<label for=\"name\">Delete fail ! Le fichier n'existe pas ou le dossier n'est pas vide</label>" +
		" <br/>" +
		"<input type=\"submit\" value=\"Submit\" />" +
		"</form>" +
		"</body>" +
		"</html>";
	}


	@DELETE
	@Path("/data/{dir}/{name}")
	public String deleteFile(@PathParam( "dir" ) String dir,
			@FormParam( "name" )final String name)throws IOException  {

		File f = new File(this.commandes.getCurrentDir()+"/"+name);
		if(this.commandes.CMDDELE(name)){
			f.delete();
			return getFile(dir);
		}
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<html xmlns=\"<xmlns />\" xml:lang=\"en\">"+
		"<head>" +
		"<title>Delete</title>" +
		" </head>" +
		"<body>" +
		" <form action=\"/rest/api/res/data/"+dir+"\" method=\"GET\">" +
		"<label for=\"name\">Delete fail ! Le fichier n'existe pas ou le dossier n'est pas vide</label>" +
		" <br/>" +
		"<input type=\"submit\" value=\"Submit\" />" +
		"</form>" +
		"</body>" +
		"</html>";
	}


	/*-------------------------------------- not REST function-------------------------*/

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

				if(!login)
					return logIn();
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

	public String write(File f, String content) throws IOException{
		String result ="";
		FileOutputStream br = new FileOutputStream(f);

		br.write(content.getBytes(),0,content.getBytes().length);
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
			if(test[i].length()>0){

				result+="<a href='/rest/api/res/"+this.commandes.getCurrentDir()+"/"+test[i]+"'>" +  test[i] + "</a></br>";
			}
		}
		result+="<a href='/rest/api/res/"+this.commandes.getCurrentDir()+"/..'>..</a></br>";
		System.out.println("here");
		return result ;

	}


}
