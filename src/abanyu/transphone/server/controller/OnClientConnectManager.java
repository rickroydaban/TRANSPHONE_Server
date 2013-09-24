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

            notifyClients(nearestTaxi,myPassenger, "sendObject");
      		}else{
      			if(mappingData.getTaxiList().containsKey(nearestTaxi.getPlateNumber())){
      				System.out.println("The requested taxi seems to be vacant in the db but is not vacant in the server");
      			}else{
      				System.out.println("Error. The requested taxi is not listed in the server anymore");
      			}
    				notifyClients(nearestTaxi,myPassenger, "pCancel"); //passenger cancelled his request
      		}          
        }else if(inputObject instanceof MyTaxi){
          //links to the object data sent by the client(taxi) connection
          MyTaxi newTaxiInfos = (MyTaxi) inputObject;
        	MyPassenger myPassenger = null;
        	boolean keyFound = mappingData.getTaxiList().containsKey(newTaxiInfos.getPlateNumber());
          
          if(keyFound){ //checks if the current unit's plate no is already registered to the system
            MyTaxi oldTaxiInfos = mappingData.getTaxiList().get(newTaxiInfos.getPlateNumber()); //get the previous data of this taxi
          	System.out.println("updating taxi "+newTaxiInfos.getPlateNumber()+" to the system! with status:"+newTaxiInfos.getStatus()+" "+newTaxiInfos.getIP()+":"+mappingData.getConnectionData().getTaxiPort());
          	
          	if(mappingData.getPassengerList().size()>0){
          		//get the object of the passenger where this taxi is assigned to
          		myPassenger = mappingData.getPassengerList().get(newTaxiInfos.getPassengerIP());
          	}
          	
            if(newTaxiInfos.getStatus()!=TaxiStatus.disconnected){ //is only true when the unit is already in the base          		
          		if(newTaxiInfos.getStatus()==TaxiStatus.unavailable){ //if there is a pending passenger request, then the server must skip on searching for this unit
              	//handles system whenever something happens on the taxi while performing the passenger request              
                if(oldTaxiInfos.getStatus()==TaxiStatus.requested || oldTaxiInfos.getStatus()==TaxiStatus.occupied){
                  if(myPassenger != null){	
                  	System.out.println("cancel request");
                    notifyClients(newTaxiInfos,myPassenger,"tCancel"); //the taxi driver cancelled his assignment
                  }else{
                  	System.out.println("No Passenger is registered in the passenger list with such IP");
                  }
                }else{
                	if(myPassenger!=null)
                		notifyClients(newTaxiInfos,myPassenger,"tReject"); //the taxi driver rejected the request               	
                }
          		}else{
          			notifyClients(newTaxiInfos, myPassenger, "sendObject");
          		}
          		
              System.out.print("Updating old taxi data with status: "+oldTaxiInfos.getStatus()+" to ");
              //update the data of the old taxi to the newly sent data taxi in the server
              mappingData.getTaxiList().put(newTaxiInfos.getPlateNumber(), newTaxiInfos);
          	}else{
              mappingData.getTaxiList().remove(newTaxiInfos.getPlateNumber());

              try {
        				System.out.println("Request Taxi Disconnect!");
                if(myPassenger != null){	
                  notifyClients(newTaxiInfos,myPassenger,"tCancel"); //the taxi disconnected
                }else{
                	System.out.println("Sending disconnection acceptance to the requesting taxi");
          				Socket socket = new Socket(clientSocket.getInetAddress(), mappingData.getConnectionData().getTaxiPort());
          				clientOutputStream = new ObjectOutputStream(socket.getOutputStream());
          				clientOutputStream.writeObject("disconnect");
          				
          				System.out.println("Closing client sockets");
          				clientOutputStream.flush();  
          				clientOutputStream.close();
          				clientInputStream.close();
          				clientSocket.close();
                }
                
        				//refresh the server map for every taxi that has disconnected
        		    SwingUtilities.invokeLater(new Runnable() {
        		      public void run() {
        		      	mappingView.getWebBrowser().navigate(mappingData.getConnectionData().getDBUrl()+"/thesis/multiplemarkers.php"); //refresh web browser			
        		      }
        		    });
        		    
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
      			 System.out.println("Updating markers with status of "+convertToString(curTaxi.getStatus()));
      			 myTaxiList.add(curTaxi.getPlateNumber()+";"+curTaxi.getStatus()+";"+curTaxi.getCurLat()+";"+curTaxi.getCurLng()+";"+convertToString(curTaxi.getStatus()));
           }
           
          mappingView.getTaxiCounterField().setText(String.valueOf(myTaxiList.size()));
          mappingView.getClientCountField().setText(String.valueOf(mappingData.getPassengerList().size()));
          mappingController.setWebMarkers(myTaxiList.toArray(new String[myTaxiList.size()]));     
        }else{
        	if(inputObject instanceof String){
        		if(((String) inputObject).contains("clientDisconnect")){
        			String clientIP = ((String) inputObject).split(":")[1];

          		//remove the client instance in the server
        			mappingData.getPassengerList().remove(inputObject);
        			//tell the passenger app that server has finished removing its instance
          		Socket socket = new Socket(clientIP, mappingData.getConnectionData().getPassengerPort());
      				clientOutputStream = new ObjectOutputStream(socket.getOutputStream());
      				clientOutputStream.writeObject("exitFromServer");
      				clientOutputStream.flush();
        		}
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
        
  private boolean notifyClients(MyTaxi taxi, MyPassenger passenger, String action){
    if(taxi == null || passenger == null){
    	if(taxi==null)
    		System.out.println("Invalid Taxi");
    	else
    		System.out.println("Invalid Passenger");

    	return false;
    }else{
	    try{
	    	if(action.equals("sendObject")){
	    		if(taxi.getStatus()==TaxiStatus.vacant)
	    			taxi.setStatus(TaxiStatus.requested);
		    
	    		//send the request to the specified taxi
	    		System.out.println("Sending Passenger Request to the Taxi");
	    		taxiSocket = new Socket(taxi.getIP(), mappingData.getConnectionData().getTaxiPort());
		    	taxiOutputStream = new ObjectOutputStream(taxiSocket.getOutputStream());
		    	taxiOutputStream.writeObject(passenger);
		    	taxiOutputStream.flush();
		    	taxiOutputStream.close();
		    	taxiSocket.close();
		    	
		    	//updating taxi data in the passenger
		    	System.out.println("Sending new Taxi Updates to the passenger's Taxi Data");
		    	passengerSocket = new Socket(passenger.getIp(), mappingData.getConnectionData().getPassengerPort());
		    	passengerOutputStream = new ObjectOutputStream(passengerSocket.getOutputStream());
	    		passengerOutputStream.writeObject(taxi);	    		
		    	passengerOutputStream.flush();
		    	passengerOutputStream.close();
		    	passengerSocket.close();
		    }else{
		    	if(action.equals("pCancel")){ //send passenger request cancel to the taxi
		    		System.out.println("Sending passenger cancellation to the taxi");
		    		taxiSocket = new Socket(taxi.getIP(), mappingData.getConnectionData().getTaxiPort());
			    	taxiOutputStream = new ObjectOutputStream(taxiSocket.getOutputStream());
			    	taxiOutputStream.writeObject(action);
			    	taxiOutputStream.flush();
			    	taxiOutputStream.close();
			    	taxiSocket.close();
		    	}else{
		    		System.out.println("Sending taxi cancellation to the taxi");
			    	passengerSocket = new Socket(passenger.getIp(), mappingData.getConnectionData().getPassengerPort());
			    	passengerOutputStream = new ObjectOutputStream(passengerSocket.getOutputStream());
		    		passengerOutputStream.writeObject(action); //send taxi cancel to the passenger
			    	passengerOutputStream.flush();
			    	passengerOutputStream.close();
			    	passengerSocket.close();
		    	}
		    }
	    	
		    return true;
	    }catch(Exception e){
	    	System.out.println("exception on notifyclients function: "+e.getMessage());
	    	return false;
	    }
    }
  }
    
  private String convertToString(TaxiStatus t){
  	if(t == TaxiStatus.occupied)
  		return "occupied";
  	else if(t == TaxiStatus.requested)
  		return "requested";
  	else if(t == TaxiStatus.unavailable)
  		return "unavailable";
  	else if(t == TaxiStatus.disconnected)
  		return "disconnected";
  	
  	return "vacant";
  }
}