//Submitted By: Biswa Ranjan Nanda
//Mav ID : 1001558251
//RequestHandler class handles processing for each of the client in a separate thread
//RequestHandler class implements Runnable interface and override it's public void run() method.
//Reference: https://github.com/jyotisalitra/web-client-server
//Reference: https://www.youtube.com/watch?v=vCDrGJWqR8w
//Reference: Thread Tutorial from http://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html
//Reference: Socket Communications from http://www.oracle.com/technetwork/java/socket-140484.html

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class RequestHandler implements Runnable {

	private Socket clientSocket; 									//clientSocket declaration 
	private int clientID; 											//clientID declared  logging purpose

	private final String CRLF = "\r\n"; 							//carriage return line feed
	private final String SP = " "; 									//status line parts separator
	public RequestHandler(Socket cs, int cID) {						//Constructor for RequestHandler to set clientSocket and clientID
		this.clientSocket = cs;
		this.clientID = cID;
	}
	@Override
	public void run() {
																	//define input and output streams
		BufferedReader socketInStream = null; 						//reads data received over the socket's inputStream
		DataOutputStream socketOutStream = null; 					//writes data over the socket's outputStream
		
		FileInputStream fis = null; 								//reads file from the local file system
		
		try {
			socketInStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));		//get a reference to clientSocket's inputStream
			socketOutStream = new DataOutputStream(clientSocket.getOutputStream());							//get a reference to clientSocket's outputStream
			String packet = socketInStream.readLine();							//read a request from socket inputStream								
			if(packet != null)													//check if request is not null
			{
				System.out.println("[SERVER - CLIENT"+clientID+"]> Received a request: " + packet);
				String[] msgParts = packet.split(SP);							//split request line based on single whitespace into three parts
				if (msgParts[0].equals("GET") && msgParts.length == 3) {		//check if the request type is GET
					String filePath = msgParts[1];								//now get the path of the requested file from the request
					
																				//check if filePath starts with a forward slash "/"
																				//if not, add a forward slash and make it relative to the current file path
					if(filePath.indexOf("/") != 0)
					{	
						filePath = "/" + filePath;
					}
					System.out.println("[SERVER - CLIENT"+clientID+"]> Requested filePath: " + filePath);

					if(filePath.equals("/"))									//if requested filePath is null or requesting a default index file
					{
						System.out.println("[SERVER - CLIENT"+clientID+"]> Respond with default /index.html file");
						filePath = filePath + "index.html";						//set filePath to the default index.htm file
					}
					
					filePath = "." + filePath;									//make the filePath relative to the current location

							
					File file = new File(filePath);								//initialize a File object using filePath
					try {
																				//check if file with filePath exists on this server
						if (file.isFile() && file.exists()) {
																				//now create a HTTP response and send it back to the client
																				//write a status line on the response
																				//since the requested file exists, we will send a 200 OK response
							String responseLine = "HTTP/1.0" + SP + "200" + SP + "OK" + CRLF;
							socketOutStream.writeBytes(responseLine);
							socketOutStream.writeBytes("Content-type: " + getContentType(filePath) + CRLF); 		//write content type header line
							socketOutStream.writeBytes(CRLF);					//write a blank line representing end of response header
							fis = new FileInputStream(file);					//open the requested file
							byte[] buffer = new byte[1024];						// initialize a buffer of size 1K.
							int bytes = 0;
							while((bytes = fis.read(buffer)) != -1 ) {			// start writing content of the requested file into the socket's output stream.
								socketOutStream.write(buffer, 0, bytes);
							}
							
							System.out.println("[SERVER - CLIENT"+clientID+"]> Sending Response with status line: " + responseLine);
							
							socketOutStream.flush();							//flush outputstream
							System.out.println("[SERVER - CLIENT"+clientID+"]> HTTP Response sent");
							
						} else {
							
							System.out.println("[SERVER - CLIENT"+clientID+"]> ERROR: Requested filePath " + filePath + " does not exist"); 		//The requested file does not exist on this server
							String responseLine = "HTTP/1.0" + SP + "404" + SP + "Not Found" + CRLF;				//write a status line on the response with 404 Not Found response
							socketOutStream.writeBytes(responseLine);

							
							socketOutStream.writeBytes("Content-type: text/html" + CRLF);							//write content type header line
							socketOutStream.writeBytes(CRLF);					//write a blank line representing end of response header																					
							socketOutStream.writeBytes(getErrorFile());			//send content of the errorFile	
							System.out.println("[SERVER - CLIENT"+clientID+"]> Sending Response with status line: " + responseLine);
							socketOutStream.flush();
							System.out.println("[SERVER - CLIENT"+clientID+"]> HTTP Response sent"); //flush outputstream
						}
					} catch (FileNotFoundException e) {
						System.err.println("[SERVER - CLIENT"+clientID+"]> EXCEPTION: Requested filePath " + filePath + " does not exist");
					} catch (IOException e) {
						System.err.println("[SERVER - CLIENT"+clientID+"]> EXCEPTION in processing request." + e.getMessage());
					}
				} else {
					System.err.println("[SERVER - CLIENT"+clientID+"]> Invalid HTTP GET Request. " + msgParts[0]);
				}
			}
			else
			{	
				System.err.println("[SERVER - CLIENT"+clientID+"]> Discarding a NULL/unknown HTTP request."); // Some requests are unwanted which are discarded
			}
		} catch (IOException e) 
		{
			System.err.println("[SERVER - CLIENT"+clientID+"]> EXCEPTION in processing request." + e.getMessage());
		} finally {
			try {															//close the resources
				if (fis != null) {
					fis.close();
				}
				if (socketInStream != null) {
					socketInStream.close();
				}
				if (socketOutStream != null) {
					socketOutStream.close();
				}
				if (clientSocket != null) {
					clientSocket.close();
					System.out.println("[SERVER - CLIENT"+clientID+"]> Closing the connection.\n");
				}
			} catch (IOException e) {
				System.err.println("[SERVER - CLIENT"+clientID+"]> EXCEPTION in closing resource." + e);
			}
		}
	}
	
	private String getContentType(String filePath)								//Get Content-type of the file using its extension filepath and return content type
	{
		if(filePath.endsWith(".html") || filePath.endsWith(".html"))			//check if file type is html
		{
			return "text/html";
		}
		return "application/octet-stream";										//otherwise, a binary file
	}
	private String getErrorFile ()
	{
		String errorFileContent = 	"<!doctype html>" + "\n" +
									"<html lang=\"en\">" + "\n" +
									"<head>" + "\n" +
									"    <meta charset=\"UTF-8\">" + "\n" +
									"    <title>Error 404</title>" + "\n" +
									"</head>" + "\n" +
									"<body>" + "\n" +
									"    <b>ErrorCode:</b> 404" + "\n" +
									"    <br>" + "\n" +
									"    <b>Error Message:</b> The requested file does not exist on this server." + "\n" +
									"</body>" + "\n" +
									"</html>";
		return errorFileContent;
	}
}