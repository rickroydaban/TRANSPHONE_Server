package abanyu.transphone.server.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

import abanyu.transphone.server.model.MappingData;
import abanyu.transphone.server.view.MapPanel;
import actors.MyPassenger;
import actors.MyTaxi;
import data.TaxiStatus;

class OnClientConnectManager implements Runnable {		
    private Socket clientSocket;
    private MappingData mappingData;
    private MapPanel mappingView;
    private MappingController mappingController;
    private ObjectOutputStream clientOutputStream;
    private ObjectInputStream clientInputStream;
    List<String> myTaxiList;
    
    Socket passengerSocket, taxiSocket;
    ObjectOutputStream passengerOutputStream, taxiOutputStream;
		private Object inputObject;
    public OnClientConnectManager(Socket pClientSocket, MappingData pMappingData, MapPanel pMappingView, MappingController pMappingController) {
      clientSocket = pClientSocket;
      mappingData = pMappingData;
      mappingView = pMappingView;
      mappingController = pMappingController;
    }
		
    @Override
    public void run(){
      try {
      	//manages objects sent by the client connections
    	  clientInputStream = new ObjectInputStream(clientSocket.getInputStream());
    	  clientOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
       
        inputObject = clientInputStream.readObject();//thread stops here until a new connection arrives

        if(inputObject instanceof MyPassenger){
          //links to the object data sent by the client connection
          MyPassenger myPassenger = (MyPassenger) inputObject;
          MyTaxi nearestTaxi = mappingData.getTaxiList().get(myPassenger.getRequestedTaxi());

          //passenger life cycle connects to the server twice. First, when requesting a taxi and lastly, when disconnecting to the server
        	System.out.println("A Passenger connected to the system requesting: "+myPassenger.getRequestedTaxi());          	
          //since we had bruteforcely set the requested taxi's status in the web to avoid other passengers from 
          //referring to it, we have to vacate the status of that taxi if ever the taxi status here is also vacant
          if(nearestTaxi.getStatus() == TaxiStatus.vacant)//we cant force to vacate the taxi if it is not vacant here
          	nearestTaxi.setStatus(TaxiStatus.vacant);

          System.out.println("Taxi Status: "+nearestTaxi.getStatus());
      		if(nearestTaxi.getStatus()==TaxiStatus.vacant){ //if there is a pending passenger request, then the server must skip on searching for this unit
            System.out.println("Sending Taxi object to passenger with passenger ip: "+myPassenger.getIp()+" @ port: "+mappingData.getConnectionData().getPassengerPort());
            mappingData.addPassenger((MyPassenger)inputObject);
            System.out.println("Found nearest vacant taxi with Plate No: "+myPassenger.getRequestedTaxi());

            notifyClients(nearestTaxi,myPassenger, "send");
      		}else{
      			if(mappingData.getTaxiList().containsKey(nearestTaxi.getPlateNumber())){
      				System.out.println("The requested taxi seems to be vacant in the db but is not vacant in the server");
      			}else{
      				System.out.println("Error. The requested taxi is not listed in the server anymore");
      			}
    				notifyClients(nearestTaxi,myPassenger, "cancelRequest");
      		}          
        }else if(inputObject instanceof MyTaxi){
          //links to the object data sent by the client(taxi) connection
          MyTaxi newTaxiInfos = (MyTaxi) inputObject;
        	MyPassenger myPassenger = null;
        	boolean keyFound = mappingData.getTaxiList().containsKey(newTaxiInfos.getPlateNumber());
          
          if(keyFound){ //checks if the current unit's plate no is already registered to the system
            MyTaxi oldTaxiInfos = mappingData.getTaxiList().get(newTaxiInfos.getPlateNumber()); //get the previous data of this taxi
          	System.out.println("updating taxi "+newTaxiInfos.getPlateNumber()+" to the system! with status:"+newTaxiInfos.getStatus()+" "+newTaxiInfos.getIP()+":"+mappingData.getConnectionData().getTaxiPort());
          	
          	if(mappingData.getPassengerList().size()>0)
          		myPassenger = mappingData.getPassengerList().get(newTaxiInfos.getPassengerIP());

            if(newTaxiInfos.getStatus()!=TaxiStatus.disconnected){ //is only true when the unit is already in the base
              //updates the distance travelledd of the current taxi
            	if(oldTaxiInfos.getCurLat() != 0 && oldTaxiInfos.getCurLng() != 0){ //check if the received taxi data has location
              	double curDistance = distance(oldTaxiInfos.getCurLat(), oldTaxiInfos.getCurLng(), newTaxiInfos.getCurLat(), newTaxiInfos.getCurLng());
              	newTaxiInfos.setDistanceTraveled(oldTaxiInfos.getDistanceTraveled() + curDistance);
              }
          		
          		if(newTaxiInfos.getStatus()==TaxiStatus.unavailable){ //if there is a pending passenger request, then the server must skip on searching for this unit
              	//handles system whenever something happens on the taxi while performing the passenger request              
                if(oldTaxiInfos.getStatus()==TaxiStatus.requested || oldTaxiInfos.getStatus()==TaxiStatus.occupied){
                  if(myPassenger != null){	
                    notifyClients(newTaxiInfos,myPassenger,"cancelRequest");
                  }else{
                  	System.out.println("No Passenger is registered in the passenger list with such IP");
                  }
                }
          		}
          		
          		if(newTaxiInfos.getStatus()==TaxiStatus.occupied){
          			notifyClients(newTaxiInfos, myPassenger, "send");
          		}
          		
              System.out.print("Updating old taxi data with status: "+oldTaxiInfos.getStatus()+" to ");
              mappingData.getTaxiList().put(newTaxiInfos.getPlateNumber(), newTaxiInfos);
              System.out.println(newTaxiInfos.getStatus());
          	}else{
          		//do something on taxi disconnect
        	  	try {
        				System.out.println("Request Taxi Disconnect!");
        				//CANNOT DETERMINE OBJECT INSTANCE SINCE ON INPUT INSTANCE IS NULL LINE 38 ------------------------FIX ME PLEASE!!!
                if(myPassenger != null){	
                  notifyClients(newTaxiInfos,myPassenger,"cancelRequest");
                }
                
                mappingData.getTaxiList().remove(newTaxiInfos.getPlateNumber());
        				Socket socket = new Socket(clientSocket.getInetAddress(), mappingData.getConnectionData().getTaxiPort());
        				clientOutputStream = new ObjectOutputStream(socket.getOutputStream());
        				clientOutputStream.writeObject("disconnect");
        				clientOutputStream.flush();
        				
        		    SwingUtilities.invokeLater(new Runnable() {
        		      public void run() {
        		      	mappingView.getWebBrowser().navigate("http://localhost/thesis/multiplemarkers.php"); //refresh web browser			
        		      }
        		    });
        		    
        				socket.close();
        			  } catch (IOException e1) {
        			  	System.out.println("io exception: "+e1.getMessage()+" tps: "+mappingData.getConnectionData().getTaxiPort()+" tpu: "+mappingData.getTaxi().getIP());
        			  }
          	}
          }else{
          	System.out.println("registering new taxi "+newTaxiInfos.getPlateNumber()+" to the system! with status:"+newTaxiInfos.getStatus());
            mappingData.addTaxi((MyTaxi)newTaxiInfos);
          }
          
          Set<String> keys = mappingData.getTaxiList().keySet(); //get all the keys of the taxi record
          
          myTaxiList = new ArrayList<String>();
          for(String curKey:keys){
      			 MyTaxi curTaxi = mappingData.getTaxiList().get(curKey);
      			 myTaxiList.add(curTaxi.getPlateNumber()+";"+curTaxi.getStatus()+";"+curTaxi.getCurLat()+";"+curTaxi.getCurLng());
           }
           
          mappingView.getTaxiCounterField().setText(String.valueOf(myTaxiList.size()));
          mappingView.getClientCountField().setText(String.valueOf(mappingData.getPassengerList().size()));
          mappingController.setWebMarkers(myTaxiList.toArray(new String[myTaxiList.size()]));     
        }else{
        	if(inputObject instanceof String){
        		System.out.println("Passenger "+ mappingData.getPassengerList().get(inputObject).getPassengerName()+" is requesting to disconnect");        		
        		
    				Socket socket = new Socket((String)inputObject, mappingData.getConnectionData().getPassengerPort());
    				clientOutputStream = new ObjectOutputStream(socket.getOutputStream());
    				clientOutputStream.writeObject("disconnect");
    				clientOutputStream.flush();
        		
        		mappingData.getPassengerList().remove(inputObject);
        	}        	
        }        
	  } catch (IOException e) {	  	
	  	try {
				System.out.println("Request Client Resend!");
				//CANNOT DETERMINE OBJECT INSTANCE SINCE ON INPUT INSTANCE IS NULL LINE 38 ------------------------FIX ME PLEASE!!!
				if(inputObject == null){
					System.out.println("Cannot Determine Where to send the resend message... Please Fix me.");
				}
				Socket socket = new Socket(clientSocket.getInetAddress(), mappingData.getConnectionData().getTaxiPort());
				clientOutputStream = new ObjectOutputStream(socket.getOutputStream());
				clientOutputStream.writeObject("resend");
				clientOutputStream.flush();
				socket.close();
			  } catch (IOException e1) {
			  	System.out.println("io exception: "+e1.getMessage()+" tps: "+mappingData.getConnectionData().getTaxiPort()+" tpu: "+mappingData.getTaxi().getIP());
			  }
	  } catch (ClassNotFoundException e) {
      	System.out.println("server class not found exception"+e.getMessage());
	  }
	}
        
  private boolean notifyClients(MyTaxi nearestTaxi, MyPassenger passenger, String action){
    if(nearestTaxi == null || passenger == null){
    	if(nearestTaxi==null)
    		System.out.println("cannot find any nearest taxi");
    	else
    		System.out.println("passenger is null");
    	return false;
    }else{
	    try{
		    System.out.println("received status of the requested taxi: "+nearestTaxi.getStatus());
		    //updates the status of the assigned taxi
		    System.out.println("connect to passenger socket: "+passenger.getIp()+" at port: "+mappingData.getConnectionData().getPassengerPort());
	    	passengerSocket = new Socket(passenger.getIp(), mappingData.getConnectionData().getPassengerPort());
	    	passengerOutputStream = new ObjectOutputStream(passengerSocket.getOutputStream());
	    	if(nearestTaxi.getStatus()!=TaxiStatus.unavailable || nearestTaxi.getStatus()!=TaxiStatus.disconnected){
	    		if(nearestTaxi.getStatus()==TaxiStatus.vacant)
	    			nearestTaxi.setStatus(TaxiStatus.requested);
		    
		    	System.out.println("trying to connect to taxi sockety with IP: "+ nearestTaxi.getIP()+" in port: "+mappingData.getConnectionData().getTaxiPort());
		    	taxiSocket = new Socket(nearestTaxi.getIP(), mappingData.getConnectionData().getTaxiPort());
		    	taxiOutputStream = new ObjectOutputStream(taxiSocket.getOutputStream());
		    	System.out.println("connected to taxi");
		    	taxiOutputStream.writeObject(passenger);
		    	taxiOutputStream.flush();
		    	System.out.println("closing the taxi socket");
		    	taxiOutputStream.close();
		    	taxiSocket.close();
		    	
	    		passengerOutputStream.writeObject(nearestTaxi);
		    }else{
		    	System.out.println("action: "+action);
	    		passengerOutputStream.writeObject(action);
		    }

		    System.out.println("updated status of the requested taxi: "+nearestTaxi.getStatus());
	    	passengerOutputStream.flush();
	    	System.out.println("closing the passenger socket");
	    	passengerOutputStream.close();
	    	passengerSocket.close();
	    	//Send passenger data to taxi if there is one available
		    return true;
	  }catch(Exception e){
	  	System.out.println("exception on notifyclients function: "+e.getMessage());
	  	return false;
	  }
    }
  }
    
//  private MyTaxi findShortestTaxiDistance(MyPassenger pMyPassenger) {
//  	//Find the nearest taxi and send its information to the passenger
//  	double shortestDistance = 0, //the shortest distance record within the loop
//        	 curDistance = 0;      //the distance estimated by comparing the current taxi's location to the client's source location
//        
//  	MyTaxi nearestTaxi = null; //the data of the resulting nearest taxi estimation
//    Set<String> keys = mappingData.getTaxiList().keySet(); //get all the keys of the taxi record
//        
//    //loop to find the nearest taxi
//    for(String curKey:keys){
//    	MyTaxi curTaxi = mappingData.getTaxiList().get(curKey);
//    	
//    	if(curTaxi.getStatus() == TaxiStatus.vacant){
//    		if(shortestDistance==0){
//    			shortestDistance= distance( pMyPassenger.getCurLat(),pMyPassenger.getCurLng(), //calculate the distance of the passenger to the current taxi's location
//            						    					curTaxi.getCurLat(),curTaxi.getCurLng());  
//    		}else{
//    			curDistance = distance( pMyPassenger.getCurLat(),pMyPassenger.getCurLng(),
//    															curTaxi.getCurLat(),curTaxi.getCurLng());
//    		}
//          
//    		if(curDistance==0 || curDistance<shortestDistance){
//    			nearestTaxi=curTaxi;  
//
//    			if(curDistance!=0)
//    				shortestDistance=curDistance; //the current distance is the shortest distance
//    		}
//    	} 
//    }
//
//    return nearestTaxi;
//  }
    
  private double distance(double lat1, double lon1, double lat2, double lon2) {
	double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		dist = dist * 1.609344;
		return (dist);
	}

	//This function converts decimal degrees to radians
	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	//This function converts radians to decimal degrees
	private double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}
  }