//Submitted By: Biswa Ranjan Nanda
//Mav ID : 1001558251
//Reference: Thread Tutorial from http://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html
//Reference: Socket Communications from http://www.oracle.com/technetwork/java/socket-140484.html
//Reference: https://www.youtube.com/watch?v=vCDrGJWqR8w
//Reference: https://github.com/jyotisalitra/web-client-server
//WebServer class starts serverSocket and listens to the client's request
//WebServer class implements runnable interface and override it's public void run() method.

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.GregorianCalendar;

public class WebServer implements Runnable {

	private ServerSocket serverSocket; 									//serverSocket is created and started
	private String serverHost; 											//hostname or IP address of the server
	private int serverPort; 											//Port number where server has to start
	private final String DEFAULT_HOST = "localhost";					//Default host and port values for the serverSocket
	private final int DEFAULT_PORT = 8080;
	
	public WebServer ()													//Default constructor if no port is passed
	{
		this.serverHost = DEFAULT_HOST; 								//Default hostname of the server
		this.serverPort = DEFAULT_PORT; 								//Default port 8080
	}
	
	public WebServer (String sHost, int port)							//Parameterized constructor if a port and serverHost are passed
	{
		this.serverHost = sHost; 										//hostname of the server
		this.serverPort = port; 										//default port 8080
	}
	
	public WebServer (int port)											//Parameterized constructor if a port is passed
	{
		this.serverHost = DEFAULT_HOST; 								//hostname of the server
		this.serverPort = port; 										//port passed by the ServerInitializer
	}
	
	@Override
	public void run() {
		
		try {
			InetAddress serverInet = InetAddress.getByName(serverHost);	//Getting the inet address of the host
			
			
																		//Initialization of serverSocket using serverInet address and serverPort
																		//using a default backlog value which depends on the implementation
			serverSocket = new ServerSocket(serverPort, 0, serverInet);

			System.out.println("[SERVER]> SERVER started at host: " + serverSocket.getInetAddress() + " port: " + serverSocket.getLocalPort() + "\n");
			
																		//provide each client an ID, starting with zero
			int clientID=0;
			
																		//multithreaded server
			while(true){
				Socket clientSocket = serverSocket.accept();			//waiting for a client to get connected
																		//Display below when a new client has connected to this server
				System.out.println("[SERVER - CLIENT"+clientID+"]> Connection established with the client at " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
				
																		//passing the value of clientSocket and clientID to RequestHandler object
				RequestHandler rh = new RequestHandler(clientSocket, clientID);
				new Thread(rh).start();									//handover processing for the newly connected client to RequestHandler in a separate thread
				clientID++;												//increment clientID for the next client;
			}
			
		} catch (UnknownHostException e) {
			System.err.println("[SERVER]> UnknownHostException for the hostname: " + serverHost); 	//Exception handling done for the Unknown Host Exception
		} catch (IllegalArgumentException iae) {
			System.err.println("[SERVER]> EXCEPTION in starting the SERVER: " + iae.getMessage());	//Exception in starting the Server due to illegal Argument Passing
		}
		catch (IOException e) {
			System.err.println("[SERVER]> EXCEPTION in starting the SERVER: " + e.getMessage());	//Exception in starting the Server due any other exception
		}
		finally {
				try {
					if(serverSocket != null){
						serverSocket.close();
					}
				} catch (IOException e) {
					System.err.println("[SERVER]> EXCEPTION in closing the server socket." + e);	//Exception in closing the Server
				}
		}
	}
}