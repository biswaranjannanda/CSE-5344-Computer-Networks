//Submitted By: Biswa Ranjan Nanda
//Mav ID : 1001558251
//Requirement - WebClient represents a single web client
//Reference: Socket Communications from http://www.oracle.com/technetwork/java/socket-140484.html
//Reference: https://github.com/jyotisalitra/web-client-server
//Reference: https://www.youtube.com/watch?v=vCDrGJWqR8w


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.GregorianCalendar;

public class WebClient {

	public static void main(String[] args) {
		final String CRLF = "\r\n"; 										//carriage return line feed
		final String SP = " "; 												//status line parts separator
		String serverHost = null;
		int serverPort = 8080;												//initialize serverPort with default port value
		String filePath = "/";												//initialize filePath with default file /
		if(args.length == 1)										//check command line arguments for serverhost, port, and filePath, at least serverHost is required
		{
			
			serverHost = args[0];											//first argument is serverHost
		}
		else if (args.length == 2){
			serverHost = args[0];											//first argument is serverHost
			try {															//second can be either serverPort or filePath
				serverPort = Integer.parseInt(args[1]); 					//check if port is an integer
			}
			catch (NumberFormatException nfe)
			{
				System.err.println("[CLIENT]> Integer Port is not provided. Default Server port will be used.");
				filePath = args[1];											//then assume this string is filePath
			}
		}
		else if (args.length == 3){
			serverHost = args[0];											//first argument is serverHost
			try {															//second argument is serverPort
				serverPort = Integer.parseInt(args[1]); 					//check if port is an integer
			}
			catch (NumberFormatException nfe)
			{
				System.err.println("[CLIENT]> Integer Port is not provided. Default Server port will be used.");
			}

			filePath = args[2];												//third argument is fileName
		}
		else
		{
			System.err.println("[CLIENT]> Not enough parameters provided. At least serverHost is required.");
			System.exit(-1);
		}
		System.out.println("[CLIENT]> Using Server Port: " + serverPort);
		System.out.println("[CLIENT]> Using FilePath: " + filePath);
		Socket socket = null;												//socket is initialized
																			//define input and output streams
		BufferedReader socketInStream = null; 								//reads data received over the socket's inputStream
		DataOutputStream socketOutStream = null; 							//writes data over the socket's outputStream
		
		FileOutputStream fos = null; 										//writes content of the responded file in a file
		
		try {
			
																			//get inet address of the serverHost
			InetAddress serverInet = InetAddress.getByName(serverHost);
			
																			//try to connect to the server
			socket = new Socket(serverInet, serverPort);
			System.out.println("[CLIENT]> Connected to the server at " + serverHost + ":" + serverPort);
			
																			//get a reference to socket's inputStream
			socketInStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
																			//get a reference to socket's outputStream
			socketOutStream = new DataOutputStream(socket.getOutputStream());

																			//now send a HTTP GET request
			String requestLine = "GET" + SP + filePath + SP +"HTTP/1.0" + CRLF;
			System.out.println("[CLIENT]> Sending HTTP GET request: " + requestLine);

																			//TIMER******************************
			long start = new GregorianCalendar().getTimeInMillis(); 
			
																			//send the requestLine
			socketOutStream.writeBytes(requestLine);
			
																			//send an empty line
			socketOutStream.writeBytes(CRLF);
			
																			//flush out output stream
			socketOutStream.flush();
			
			System.out.println("[CLIENT]> Waiting for a response from the server");
																			//extract response Code
			String responseLine = socketInStream.readLine();
			System.out.println("[CLIENT]> Received HTTP Response with status line: " + responseLine);

																			//extract content-type of the response
			String contentType = socketInStream.readLine();
			System.out.println("[CLIENT]> Received " + contentType);

																			//read a blank line i.e. CRLF
			socketInStream.readLine();

			System.out.println("[CLIENT]> Received Response Body:");
																			//start reading content body
			StringBuilder content = new StringBuilder();
			String res;
			while((res = socketInStream.readLine()) != null)
			{
																			//save content to a buffer
				content.append(res + "\n");
				
																			//print it as well
				System.out.println(res);
			}
										
			String fileName = getFileName(content.toString());				//get a name of the file from the response
			long finish = new GregorianCalendar().getTimeInMillis();
			System.out.println("RTT: " + (finish-start+" ms"));
																			//open a outputstream to the fileName
																			//file will be created if it does not exist
			fos = new FileOutputStream(fileName);
			fos.write(content.toString().getBytes());
			fos.flush();
			System.out.println("[CLIENT]> HTTP Response received. File Created: " + fileName);
		} catch (IllegalArgumentException iae) {
			System.err.println("[CLIENT]> EXCEPTION in connecting to the SERVER: " + iae.getMessage());
		} catch (IOException e) {
			System.err.println("[CLIENT]> ERROR " + e);
		}
		finally {
			try {
																			//close all resources
				if (socketInStream != null) {
					socketInStream.close();
				}
				if (socketOutStream != null) {
					socketOutStream.close();
				}
				if (fos != null) {
					fos.close();
				}
				if (socket != null) {
					socket.close();
					System.out.println("[CLIENT]> Closing the Connection.");
				}
			} catch (IOException e) {
				System.err.println("[CLIENT]> EXCEPTION in closing resource." + e);
			}
		}
	}
									//Function returns the content and filename which is the value of the <title> tag
	private static String getFileName(String content)						
	{
		String filename = "";												//default filename if <title> tag is empty
		filename = content.substring(content.indexOf("<title>")+("<title>").length(), content.indexOf("</title>"));
		if(filename.equals(""))
		{
			filename = "index";
		}	
		filename = filename+".html";
		return filename;
	}
}
