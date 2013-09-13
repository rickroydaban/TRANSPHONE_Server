package abanyu.transphone.server.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
       
        Object inputObject = clientInputStream.readObject();//thread stops here until a new connection arrives

        if(inputObject instanceof MyPassenger){
        	System.out.println("a passenger has connected to the system!");
          //links to the object data sent by the client connection
          MyPassenger myPassenger = (MyPassenger) inputObject;
          boolean keyFound = mappingData.getPassengerList().containsKey(myPassenger.getIp());
          
          //passenger life cycle connects to the server twice. First, when requesting a taxi and lastly, when disconnecting to the server
          if(keyFound){//disconnects the passenger to the server
          	mappingData.getPassengerList().remove(myPassenger.getIp()); 
          	int clientCount = Integer.parseInt(mappingView.getClientCountField().getText());
          	mappingView.getClientCountField().setText(String.valueOf(--clientCount));
          }else{//manage passenger request
            int clientCount = Integer.parseInt(mappingView.getClientCountField().getText());
            mappingView.getClientCountField().setText(String.valueOf(++clientCount)); //update the UI
            mappingData.getPassengerList().put(myPassenger.getIp(), myPassenger); //register the passenger to the system
            notifyClients(findShortestTaxiDistance(myPassenger),myPassenger);
          }
        }else{
        	System.out.println("a taxi has connected to the system!");
          //links to the object data sent by the client(taxi) connection
          MyTaxi currTaxiData = (MyTaxi) inputObject;
          boolean keyFound = mappingData.getTaxiList().containsKey(currTaxiData.getPlateNumber());
          
          if(keyFound){ //checks if the current unit's plate no is already registered to the system
            MyTaxi prevTaxiData = mappingData.getTaxiList().get(currTaxiData.getPlateNumber()); //get the previous data of this taxi

            if(currTaxiData.getStatus()!=TaxiStatus.disconnected){ //is only true when the unit is already in the base
              //updates the distance travelledd of the current taxi
            	if(prevTaxiData.getCurLat() != 0 && prevTaxiData.getCurLng() != 0){ //check if the received taxi data has location
              	double curDistance = distance(prevTaxiData.getCurLat(), prevTaxiData.getCurLng(), currTaxiData.getCurLat(), currTaxiData.getCurLng());
              	currTaxiData.setDistanceTraveled(prevTaxiData.getDistanceTraveled() + curDistance);
              }
          		
          		if(currTaxiData.getStatus()==TaxiStatus.unavailable){ //if there is a pending passenger request, then the server must skip on searching for this unit
              	//handles system whenever something happens on the taxi while performing the passenger request              
                if(prevTaxiData.getStatus()==TaxiStatus.requested || prevTaxiData.getStatus()==TaxiStatus.occupied){
                  MyPassenger myPassenger = mappingData.getPassengerList().get(prevTaxiData.getPassengerIP());
                  //contacts another unit to fetched the passenger
                  if(myPassenger != null){	
                    notifyClients(findShortestTaxiDistance(myPassenger),myPassenger);
                  }
                }
          		}
          	}else{
          		//prompt the server to handle disconnection request from the unit
          	}
          }else{
          	mappingData.getTaxiList().put(currTaxiData.getPlateNumber(), currTaxiData);
          }
        }
          /*****************************************************************************************************
           *************************************** OLD FILE *************************************************** 
           ****************************************************************************************************/
//          //the whole if code block is usually used for initial connection of the unit to the server
//          if(currTaxiData.getStatus() != TaxiStatus.unavailable && currTaxiData.getStatus() != TaxiStatus.disconnected){
//            if(keyFound){ //check if the connected taxi is already registered in the list
//              MyTaxi curTaxi = mappingData.getTaxiList().get(currTaxiData.getPlateNumber());
//            	//records the di
//              if(curTaxi.getCurLat() != 0 && curTaxi.getCurLng() != 0){ //check if the received taxi data has location
//              	double curDistance = distance(curTaxi.getCurLat(), curTaxi.getCurLng(), currTaxiData.getCurLat(), currTaxiData.getCurLng());
//              	currTaxiData.setDistanceTraveled(curTaxi.getDistanceTraveled() + curDistance);
//              }
//            }else //register the unit if ever it has not been registered yet
//            	mappingData.getTaxiList().put(currTaxiData.getPlateNumber(), currTaxiData);
//            
//            //update the taxi coordinates in the view
//            mappingData.getTaxi().setCurrentLocation(currTaxiData.getCurLat(), currTaxiData.getCurLng());
//                      	  
//          	//automatically updates myTaxi object pair using its plate number
//           	//if the plate number does not exist in the list, it will be automatically inserted
//          	mappingData.getTaxiList().put(currTaxiData.getPlateNumber(), currTaxiData);          	
//          }else{
//            if(keyFound) {
//              MyTaxi curTaxi = mappingData.getTaxiList().get(currTaxiData.getPlateNumber());
//            	//handles system whenever something happens on the taxi while doing the request              
//              if(curTaxi.getStatus()==TaxiStatus.requested || curTaxi.getStatus()==TaxiStatus.occupied){
//                MyPassenger curPassenger = mappingData.getPassengerList().get(curTaxi.getPassengerIP());
//                
//                if(curPassenger != null){	
//                  MyTaxi nearestTaxi = findShortestTaxiDistance(curPassenger);
//                }
//              }
//            	
//              if(currTaxiData.getStatus() == TaxiStatus.disconnected)	//edited
//              {
//              	mappingData.getTaxiList().remove(curTaxi.getPlateNumber());
//              	int taxiCount = Integer.parseInt(mappingView.getTaxiCounterField().getText());
//              	mappingView.getTaxiCounterField().setText(String.valueOf(--taxiCount));
//              }
//            }
//          }          
//        }
//        
        Set<String> keys = mappingData.getTaxiList().keySet(); //get all the keys of the taxi record
        
        myTaxiList = new ArrayList<String>();
        for(String curKey:keys){
    			 MyTaxi curTaxi = mappingData.getTaxiList().get(curKey);
          myTaxiList.add(curTaxi.getPlateNumber()+";"+curTaxi.getStatus()+";"+curTaxi.getCurLat()+";"+curTaxi.getCurLng());
         }
         
        mappingView.getTaxiCounterField().setText(String.valueOf(myTaxiList.size()));
        mappingController.setWebMarkers(myTaxiList.toArray(new String[myTaxiList.size()]));     
	  } catch (IOException e) {	  	
	  	try {
				System.out.println("Request Client Resend!");
				//CANNOT DETERMINE OBJECT INSTANCE SINCE ON INPUT INSTANCE IS NULL LINE 38 ------------------------FIX ME PLEASE!!!

				Socket socket = new Socket(clientSocket.getInetAddress(), mappingData.getConnectionData().getTaxiPort());
				clientOutputStream = new ObjectOutputStream(socket.getOutputStream());
				clientOutputStream.writeObject("Resend");
				clientOutputStream.flush();
				socket.close();
			  } catch (IOException e1) {
			  	System.out.println("io exception: "+e1.getMessage()+" tps: "+mappingData.getConnectionData().getTaxiPort()+" tpu: "+mappingData.getTaxi().getIP());
			  }
	  } catch (ClassNotFoundException e) {
      	System.out.println("server class not found exception"+e.getMessage());
	  }
	}
        
  private boolean notifyClients(MyTaxi nearestTaxi, MyPassenger passenger){
    if(nearestTaxi == null || passenger == null)
    	return false;
    else{
      try{
      	//updates the status of the assigned taxi
      	nearestTaxi.setStatus(TaxiStatus.requested);
      	//Send new taxi data to current passenger of disconnected taxi
      	Socket passengerSocket = new Socket(passenger.getIp(), mappingData.getConnectionData().getPassengerPort());
      	ObjectOutputStream passengerOutputStream = new ObjectOutputStream(passengerSocket.getOutputStream());
      	passengerOutputStream.writeObject(nearestTaxi);
      	passengerOutputStream.flush();
      	passengerOutputStream.close();
      	passengerSocket.close();
      	//Send passenger data to taxi if there is one available
      	Socket taxiSocket = new Socket(nearestTaxi.getIP(), mappingData.getConnectionData().getTaxiPort());
      	ObjectOutputStream taxiOutputStream = new ObjectOutputStream(taxiSocket.getOutputStream());
      	taxiOutputStream.writeObject(passenger);
      	taxiOutputStream.flush();
      	taxiOutputStream.close();
      	taxiSocket.close();
      	return true;
      }catch(Exception e){
      	return false;
      }
    }
  }
    
  private MyTaxi findShortestTaxiDistance(MyPassenger pMyPassenger) {
  	//Find the nearest taxi and send its information to the passenger
  	double shortestDistance = 0, //the shortest distance record within the loop
        	 curDistance = 0;      //the distance estimated by comparing the current taxi's location to the client's source location
        
  	MyTaxi nearestTaxi = null; //the data of the resulting nearest taxi estimation
    Set<String> keys = mappingData.getTaxiList().keySet(); //get all the keys of the taxi record
        
    //loop to find the nearest taxi
    for(String curKey:keys){
    	MyTaxi curTaxi = mappingData.getTaxiList().get(curKey);
    	
    	if(curTaxi.getStatus() == TaxiStatus.vacant){
    		if(shortestDistance==0){
    			shortestDistance= distance( pMyPassenger.getCurLat(),pMyPassenger.getCurLng(), //calculate the distance of the passenger to the current taxi's location
            						    					curTaxi.getCurLat(),curTaxi.getCurLng());  
    		}else{
    			curDistance = distance( pMyPassenger.getCurLat(),pMyPassenger.getCurLng(),
    															curTaxi.getCurLat(),curTaxi.getCurLng());
    		}
          
    		if(curDistance==0 || curDistance<shortestDistance){
    			nearestTaxi=curTaxi;  

    			if(curDistance!=0)
    				shortestDistance=curDistance; //the current distance is the shortest distance
    		}
    	} 
    }

    return nearestTaxi;
  }
    
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