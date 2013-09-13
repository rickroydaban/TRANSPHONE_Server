package abanyu.transphone.server.model;

import java.net.ServerSocket;
import java.util.Hashtable;

import actors.MyPassenger;
import actors.MyTaxi;
import connections.MyConnection;

public class MappingData {
	private MyConnection myConnection;
	private MyTaxi myTaxi;
	private MyPassenger myPassenger;
	private Hashtable<String, MyPassenger> passengerList;
	private Hashtable<String, MyTaxi> taxiList;
	private boolean isServerAlive;
	private ServerSocket serverSocket;
	
	public MappingData(){
		myConnection = new MyConnection();
		passengerList=new Hashtable<String, MyPassenger>();
		taxiList=new Hashtable<String, MyTaxi>();
		myTaxi = new MyTaxi();
		myPassenger = new MyPassenger();
		isServerAlive = false;
	}
	
	public void setServerSocket(ServerSocket pServerSocket){
		serverSocket = pServerSocket;
	}
	
	public MyConnection getConnectionData(){
		return myConnection;
	}
	
	public Hashtable<String, MyPassenger> getPassengerList(){
		return passengerList;
	}
	
	public Hashtable<String, MyTaxi> getTaxiList(){
		return taxiList;
	}	
	
	public MyTaxi getTaxi(){
		return myTaxi;
	}
	
	public MyPassenger getPassenger(){
		return myPassenger;
	}
	
	public boolean isServerAlive(){
		return isServerAlive;
	}
	
	public ServerSocket getServerSocket(){
		return serverSocket;
	}
	
	public void liveServer(){
		isServerAlive = true;
	}
	
	public void killServer(){
		isServerAlive = false;
	}
	
}
