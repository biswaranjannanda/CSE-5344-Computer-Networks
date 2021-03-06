Assignment Submitted by: Biswa Ranjan Nanda
UTA ID: 1001558251


Steps for the Compilation of the Code: 
---------------------------------------

1) Save the client and server codes in 2 different directories on same disk.
2) Compile the server side on command prompt by following commands:
	-> cd server
	-> javac *.java
3) Compile the client side on another command prompt by following commands:
	-> cd client
	-> javac *.java
4) On the server side prompt put following command:
	-> java ServerInitializer (port number)
		Example: java ServerInitializer 8080
5)On client side prompt:
	-> java Webclient (ip address) (port number) (file name)
		Example: java WebClient 127.0.0.1 8080 index.html
	    or	Example: java WebClient localhost 8080 index.html
	 the ip address used is 127.0.0.1 port number 8080 and file name is index.html

References & Citations:
-----------------------
* Socket Communications from http://www.oracle.com/technetwork/java/socket-140484.html 
* Thread Tutorial from http://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html
* https://elearn.uta.edu/bbcswebdav/pid-6739183-dt-content-rid-66706988_2/courses/2182-COMPUTER-NETWORKS-21611-002/Programming%20Assignment%201_reference_Java.pdf
* https://github.com/jyotisalitra/web-client-server
* https://www.youtube.com/watch?v=vCDrGJWqR8w
* Book: Computer Networking. A Top Down Approach. Fifth Edition by James F. Kurose, Keith W. Ross. Chapter 2.