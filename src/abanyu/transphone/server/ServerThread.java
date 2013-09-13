//package abanyu.transphone.server;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.Reader;
//import java.net.InetAddress;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.net.URL;
//import java.nio.charset.Charset;
//import java.util.Hashtable;
//
//import actors.*;
//import connections.MyConnection;
//
//public class ServerThread implements Runnable{
//  public static ServerSocket serverSocket;
//  public static Hashtable<String, MyPassenger> passengerList;
//  public static Hashtable<String, MyTaxi> taxiList;
//  protected String jsonText;  
//  private MyConnection conn;
//  private Thread thread;
//  private ConnectionListenerThread connectionListener;
//  
//  @Override
//  public void run(){
//    try{
//      conn = new MyConnection();	
//      serverSocket = new ServerSocket(conn.getServerPort());
//      passengerList=new Hashtable<String, MyPassenger>();
//      taxiList=new Hashtable<String, MyTaxi>();
//      
//      InetAddress ip=InetAddress.getLocalHost();
//      conn.setServerIp(ip.getHostAddress());
//      String url = "http://testphone.freetzi.com/thesis/dbmanager.php?fname=setServerIP&arg1="+ip.getHostAddress();
//      InputStream is = new URL(url).openStream();
//      try {
//        ServerMap.statusField.setText("WORKING. CONFIGURING SERVER IP... ");
//        BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
//        jsonText = readAll(rd);
//      } finally {
//        is.close();
//      }        
//    }catch (IOException e) {
//      System.out.println("FAILED TO LISTEN TO SERVERPORT...");
//      e.printStackTrace();
//    }
//    
//    //While server socket is not closed
//	while(ServerMap.isServerThreadRunning){
//      try{
//    	//updates listener for units on operation
//      ServerMap.statusField.setText("ONLINE. LISTENING FOR REQUESTS... ");
//		  ServerMap.webBrowserPanel.setVisible(true);
//    	Socket clientSocket = serverSocket.accept(); 
//    	System.out.println("a new connection accepted!");
//    	connectionListener = new ConnectionListenerThread(clientSocket, conn);
//    	thread = new Thread(connectionListener);
//    	thread.start();
//    	//waits until thread is finish
//    	thread.join();
//      }catch (IOException e) {
//        System.out.println(e.getMessage()+" "+e.getCause());
//	  } catch (InterruptedException e) {
//	  	  //exception for thread.join()
//				e.printStackTrace();
//			}
//	}
//	
////	try {
////	  if(connectionListener != null && connectionListener.clientInputStream != null)
////		  connectionListener.clientInputStream.close();
////	} catch (IOException e) {
////	    e.printStackTrace();
////	}
//	
//	//process continues if the server has been established then generate a server-client connection	
//	ServerMap.statusField.setText("Server is offline!");
//  }	
//  //NOTE: client is a general term as us CLIENT-SERVER connections NOT CLIENT,TAXI,SERVER..
//  
//  private static String readAll(Reader rd) throws IOException {
//	    StringBuilder sb = new StringBuilder();
//	    int cp;
//	    while ((cp = rd.read()) != -1) {
//	      sb.append((char) cp);
//	    }
//	    return sb.toString();
//	  }
//}