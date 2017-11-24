package cs520EleNa;

import java.io.*;
import java.net.*;


class client
{
   public static void main(String args[]) throws IOException
   {
      BufferedReader fromUser =
         new BufferedReader(new InputStreamReader(System.in));
      DatagramSocket clientSocket = new DatagramSocket();
      InetAddress IPAddress = InetAddress.getByName("localhost");
      //these will be used to contain the outgoing and incoming messages.
      byte[] sendData = new byte[1024];
      byte[] receiveData = new byte[1024];
      //timeout is in milliseconds, so this is .1 second.
      int timeout = 100;
	  
      while(true) {
    	  System.out.println("Type in a valid expression. A valid expression is (int) (OC) (int) - make sure it is seperated by a space, and that you are not dividing by zero. ");
    	  System.out.println("Type 'quitserver' to terminate the server and 'quitclient' to terminate the client.");
		  //read line from user
    	  String sentence = fromUser.readLine();
    	  
    	  //type 'quitclient' to exit the client
		  if(sentence.equals("quitclient")) {
			  break;
		  }
		  sendData = sentence.getBytes();
		  System.out.println("Client: " + sentence);
		  
		  //send the packet to the server
		  DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 50000);
		  clientSocket.send(sendPacket);
		  if(sentence.equals("quitserver")) {
			  continue;
		  }
		  
		  
		  //wait to recieve the packet. If no packet within the time, double time out and continue the loop.
		  DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		  clientSocket.setSoTimeout(timeout);
		  try {
			  clientSocket.receive(receivePacket);
		  }
		  catch(SocketTimeoutException e) {
			  System.out.println("Packet lost! Please try again!");
			  timeout *= 2;
			  continue;
		  }
		  //decode the incoming packet data
		  String fromServer = new String(receivePacket.getData());
		  
		  //we get the message in a comma-seperated pairing, which is [STATUS_CODE,VALUE]
		  String[] pairing = fromServer.split(",");
		  if(pairing.length != 2) {
			  System.out.println("Did not get pairing! ERROR!");
		  }
		  if(pairing[0].equals("200OK")) {
			  System.out.println("Server:" + pairing[1]);
		  }
		  else if(pairing[0].equals("300ERROR")) {
			  System.out.println("Invalid expression. Please try again.");
			  System.out.println("Server:" + pairing[1]);
		  }
		  
      }
      System.out.println("Goodbye!");
      clientSocket.close();
   }
}