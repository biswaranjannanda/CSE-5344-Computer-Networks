//Submitted By: Biswa Ranjan Nanda
//Mav ID : 1001558251
//Initializes WebServer by passing port from the command line 
//Reference: https://github.com/jyotisalitra/web-client-server
//Reference: https://www.youtube.com/watch?v=vCDrGJWqR8w
//Reference: Thread Tutorial from http://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html

public class ServerInitializer {
	public static void main(String[] args) {						//main method
		int port = 8080;											//initialize port to default 8080
		if(args.length == 1)										//check command line arguments for port
		{	
			try {													//port is provided
				port = Integer.parseInt(args[0]); 					//check if port is an integer
			}
			catch (NumberFormatException nfe)
			{
				System.err.println("[SERVER]> No Port number provided. Server will start at default port.");
			}
		}

		System.out.println("[SERVER]> Using Server Port : " + port);
		WebServer ws = new WebServer(port); 						//constructing WebServer object
		new Thread(ws).start();										//start WebServer in a new thread
	}
}