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

class OnClientConnectManagerOld implements Runnable {		
    private Socket clientSocket;
    private MappingData mappingData;
    private MapPanel mappingView;
    private MappingController mappingController;
    private ObjectOutputStream clientOutputStream;
    private ObjectInputStream clientInputStream;
    private List<String> myTaxiList;
    
    private Socket passengerSocket, taxiSocket;
    private ObjectOutputStream passengerOutputStream, taxiOutputStream;
		private Object inputObject;
		private String receiver = "taxi";
		private String sendingFailed,sendingParameter;
		private MyTaxi sendingTaxi;
		private MyPassenger sendingPassenger;
    public OnClientConnectManagerOld(Socket pClientSocket, MappingData pMappingData, MapPanel pMappingView, MappingController pMappingController) {
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
          System.out.println("Passenger has Connected. Adding new Passenger Data in the Request Queue...");
          System.out.println("@ IP: "+myPassenger.getIp());
          System.out.println("Name: "+myPassenger.getPassengerName());
          System.out.println("Taxi: "+myPassenger.getRequestedTaxi());
          mappingData.getPassengerList().put(myPassenger.getIp(), myPassenger);
          MyTaxi nearestTaxi = mappingData.getTaxiList().get(myPassenger.getRequestedTaxi());
          nearestTaxi.setPassengerIp(myPassenger.getIp());
          mappingData.getTaxiList().put(myPassenger.getRequestedTaxi(), nearestTaxi);
          
        	System.out.println("Taxi Status at Passenger Connect: "+nearestTaxi.getStatus());
      		if(nearestTaxi.getStatus()==TaxiStatus.vacant){ //if there is a pending passenger request, then the server must skip on searching for this unit
            System.out.println("Sending Taxi object to passenger with passenger ip: "+myPassenger.getIp()+" @ port: "+mappingData.getConnectionData().getPassengerPort());
            mappingData.addPassenger((MyPassenger)inputObject);
            System.out.println("Found nearest vacant taxi with Plate No: "+myPassenger.getRequestedTaxi());
          	
            sendingFailed = "updateTaxi";
          	sendingTaxi=nearestTaxi;
          	sendingPassenger=myPassenger;
          	updateTaxi(nearestTaxi, myPassenger);
      		}else{
      			if(mappingData.getTaxiList().containsKey(nearestTaxi.getPlateNumber())){
      				System.out.println("The requested taxi seems to be vacant in the db but is not vacant in the server");
      			}else{
      				System.out.println("Error. The requested taxi is not listed in the server anymore");
      			}
      			sendCancelRequest(nearestTaxi,myPassenger,"pCancel"); //passenger cancelled his request
      		}          
        }else if(inputObject instanceof MyTaxi){
          //links to the object data sent by the client(taxi) connection
          MyTaxi newTaxiInfos = (MyTaxi) inputObject;
        	MyPassenger myPassenger = null;
        	boolean keyFound = mappingData.getTaxiList().containsKey(newTaxiInfos.getPlateNumber());
          
          if(keyFound){ //checks if the current unit's plate no is already registered to the system
            MyTaxi oldTaxiInfos = mappingData.getTaxiList().get(newTaxiInfos.getPlateNumber()); //get the previous data of this taxi
            //update the data of the old taxi to the newly sent data taxi in the server
          	
            if(newTaxiInfos.getPassengerIP()!=null){
            	if(mappingData.getPassengerList().size()>0){
            		//get the object of the passenger where this taxi is assigned to
            		System.out.println("Retrieving passenger with key: "+newTaxiInfos.getPassengerIP()+" in the passenger list");
            		myPassenger = mappingData.getPassengerList().get(newTaxiInfos.getPassengerIP());
            	}
            }
          	
            if(newTaxiInfos.getStatus()!=TaxiStatus.disconnected){ //is only true when the unit is already in the base          		
          		if(newTaxiInfos.getStatus()==TaxiStatus.unavailable){ //if there is a pending passenger request, then the server must skip on searching for this unit
              	newTaxiInfos.setPassengerIp(null);
              	mappingData.getTaxiList().put(newTaxiInfos.getPlateNumber(), newTaxiInfos);
              	
          			//handles system whenever something happens on the taxi while performing the passenger request              
                if(oldTaxiInfos.getStatus()==TaxiStatus.requested || oldTaxiInfos.getStatus()==TaxiStatus.occupied){
                  if(myPassenger != null){	
                  	System.out.println("cancel request");
                  	sendingFailed = "sendCancelRequest";
                  	sendingParameter = "tCancel";
                  	sendingTaxi=newTaxiInfos;
                  	sendingPassenger=myPassenger;
                    sendCancelRequest(newTaxiInfos,myPassenger,"tCancel"); //the taxi driver cancelled his assignment
                  }else{
                  	System.out.println("No Passenger is registered in the passenger list with such IP");
                  }
                }else{
                	if(oldTaxiInfos.getStatus()!=TaxiStatus.unavailable){
                  	if(myPassenger!=null)
                    	sendingFailed = "sendCancelRequest";
                  		sendingParameter = "tReject";
                    	sendingTaxi=newTaxiInfos;
                    	sendingPassenger=myPassenger;
                  		sendCancelRequest(newTaxiInfos,myPassenger,"tReject"); //the taxi driver rejected the request               	                		
                	}
                }
          		}else if(newTaxiInfos.getStatus()==TaxiStatus.vacant){ //if there is a pending passenger request, then the server must skip on searching for this unit
              	newTaxiInfos.setPassengerIp(null);
              	mappingData.getTaxiList().put(newTaxiInfos.getPlateNumber(), newTaxiInfos);
              	
          			//handles system whenever something happens on the taxi while performing the passenger request              
                if(oldTaxiInfos.getStatus()==TaxiStatus.occupied){
                  if(myPassenger != null){	
                  	System.out.println("cancel request");
                  	sendingFailed = "sendCancelRequest";
                  	sendingParameter = "tCancel";
                  	sendingTaxi=newTaxiInfos;
                  	sendingPassenger=myPassenger;
                    sendCancelRequest(newTaxiInfos,myPassenger,"tCancel"); //the taxi driver cancelled his assignment
                  }else{
                  	System.out.println("No Passenger is registered in the passenger list with such IP");
                  }
                }else{
//                	if(oldTaxiInfos.getStatus()!=TaxiStatus.vacant){
                  	if(myPassenger!=null)
                    	sendingFailed = "sendCancelRequest";
                  		sendingParameter = "tReject";
                    	sendingTaxi=newTaxiInfos;
                    	sendingPassenger=myPassenger;
                  		sendCancelRequest(newTaxiInfos,myPassenger,"tReject"); //the taxi driver rejected the request               	                		
                	}
//                }
          		}else{
              	sendingFailed = "updatePassenger";
              	sendingTaxi=newTaxiInfos;
              	sendingPassenger=myPassenger;
          			updatePassenger(newTaxiInfos, myPassenger);
          		}
          		
              System.out.print("Updating old taxi data with status: "+oldTaxiInfos.getStatus()+" to ");
          	}else{
              mappingData.getTaxiList().remove(newTaxiInfos.getPlateNumber());

              try {
        				System.out.println("Request Taxi Disconnect!");
                if(myPassenger != null){	
                	sendingFailed = "sendCancelRequest";
                  sendingParameter = "tCancel";
                	sendingTaxi=newTaxiInfos;
                	sendingPassenger=myPassenger;
                	sendCancelRequest(newTaxiInfos, myPassenger, "tCancel");
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
        		    
              } catch (Exception e1) {
              	e1.printStackTrace();
              }
          	}
          }else{
          	System.out.println("registering new taxi "+newTaxiInfos.getPlateNumber()+" to the system! with status:"+newTaxiInfos.getStatus());
          }
          mappingData.addTaxi((MyTaxi)newTaxiInfos);
//          mappingData.getTaxiList().put(newTaxiInfos.getPlateNumber(), newTaxiInfos);
        	System.out.println("updating taxi "+newTaxiInfos.getPlateNumber()+" to the system! with status:"+newTaxiInfos.getStatus()+" "+newTaxiInfos.getIP()+":"+mappingData.getConnectionData().getTaxiPort());
          
          Set<String> keys = mappingData.getTaxiList().keySet(); //get all the keys of the taxi record
          
          myTaxiList = new ArrayList<String>();
          for(String curKey:keys){
      			 MyTaxi curTaxi = mappingData.getTaxiList().get(curKey);
      			 System.out.println("Updating markers with status of "+convertToString(curTaxi.getStatus()));
      			 myTaxiList.add(curTaxi.getPlateNumber()+";"+curTaxi.getStatus()+";"+curTaxi.getCurLat()+";"+curTaxi.getCurLng()+";"+convertToString(curTaxi.getStatus()));
           }
          mappingController.setWebMarkers(myTaxiList.toArray(new String[mappingData.getTaxiList().size()]));      
        }else{
        	if(inputObject instanceof String){
        		String receivedString = (String)inputObject;
        		
        		if(receivedString.contains("clientDisconnect")){
        			String clientIP = receivedString.split(":")[1];

          		//remove the client instance in the server
        			mappingData.getPassengerList().remove(inputObject);
        			//tell the passenger app that server has finished removing its instance
          		Socket socket = new Socket(clientIP, mappingData.getConnectionData().getPassengerPort());
      				clientOutputStream = new ObjectOutputStream(socket.getOutputStream());
      				clientOutputStream.writeObject("exitFromServer");
      				clientOutputStream.flush();
        		}else if(receivedString.contains("taxiReject")){
        			String plateNo = receivedString.split(":")[1];
        			MyTaxi taxi = mappingData.getTaxiList().get(plateNo);
        			sendCancelRequest(taxi, mappingData.getPassengerList().get(taxi.getPassengerIP()), "tReject");
        		}
        	}
        }
        
        mappingView.getTaxiCounterField().setText(String.valueOf(mappingData.getTaxiList().size()));
        mappingView.getClientCountField().setText(String.valueOf(mappingData.getPassengerList().size()));
        
        
	  } catch (IOException e) {	  	
	  	try {
				System.out.println("Request Client Resend! :"+e.getMessage());
				//CANNOT DETERMINE OBJECT INSTANCE SINCE ON INPUT INSTANCE IS NULL LINE 38 ------------------------FIX ME PLEASE!!!
				if(inputObject == null){						
					Socket socket = new Socket(clientSocket.getInetAddress(), mappingData.getConnectionData().getTaxiPort());
					clientOutputStream = new ObjectOutputStream(socket.getOutputStream());
					clientOutputStream.writeObject("resend");
					clientOutputStream.flush();
					socket.close();						
				}else{
					if(receiver.equals("taxi")){
						if(sendingFailed.equals("sendCancelRequest")){
							sendCancelRequest(sendingTaxi, sendingPassenger, sendingParameter);
						}else if(sendingFailed.equals("updatePassenger")){
							updatePassenger(sendingTaxi, sendingPassenger);
						}
					}else if(receiver.equals("passenger")){
						if(sendingFailed.equals("updateTaxi")){
							updateTaxi(sendingTaxi, sendingPassenger);
						}						
					}		
				}
				
			  } catch (IOException e1) {
			  	System.out.println("io exception: "+e1.getMessage()+" tps: "+mappingData.getConnectionData().getTaxiPort()+" tpu: "+mappingData.getTaxi().getIP());
			  }
	  } catch (ClassNotFoundException e) {
      	e.printStackTrace();
	  } catch(Exception e){
    	e.printStackTrace();	  	
	  }
	}
    
  private void updateTaxi(MyTaxi taxi,MyPassenger passenger){
		if(taxi!=null){
			System.out.println("Sending Passenger Request to the Taxi");
  		try {
				taxiSocket = new Socket(taxi.getIP(), mappingData.getConnectionData().getTaxiPort());
	    	taxiOutputStream = new ObjectOutputStream(taxiSocket.getOutputStream());
	    	taxiOutputStream.writeObject(passenger);
	    	taxiOutputStream.flush();
	    	taxiOutputStream.close();
	    	taxiSocket.close();
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}  	
  }
  
  private void updatePassenger(MyTaxi taxi, MyPassenger passenger){
  	if(taxi.getStatus() == TaxiStatus.requested || taxi.getStatus() == TaxiStatus.occupied){
    	//updating taxi data in the passenger
    	System.out.println("Sending new Taxi Updates to the passenger's Taxi Data");
    	System.out.println("Creating new socket to connect: "+passenger.getIp()+" at "+mappingData.getConnectionData().getPassengerPort());
    	try {
				passengerSocket = new Socket(passenger.getIp(), mappingData.getConnectionData().getPassengerPort());
	    	passengerOutputStream = new ObjectOutputStream(passengerSocket.getOutputStream());
	  		passengerOutputStream.writeObject(taxi);	    		
	    	passengerOutputStream.flush();
	    	passengerOutputStream.close();
	    	passengerSocket.close();
			} catch (Exception e) {
				e.printStackTrace();
			} 
  	}  	
  }

  private void sendCancelRequest(MyTaxi taxi, MyPassenger passenger, String action){
    try {
      if(action.equals("pCancel")){ //send passenger request cancel to the taxi
	  		System.out.println("Sending passenger cancellation to the taxi");
	  	
	  		taxiSocket = new Socket(taxi.getIP(), mappingData.getConnectionData().getTaxiPort());
				taxiOutputStream = new ObjectOutputStream(taxiSocket.getOutputStream());
	    	taxiOutputStream.writeObject(action);
	    	taxiOutputStream.flush();
	    	taxiOutputStream.close();
	    	taxiSocket.close();
      }else{
      	System.out.println("Sending taxi cancellation to the passenger");
  		
	    	passengerSocket = new Socket(passenger.getIp(), mappingData.getConnectionData().getPassengerPort());
	    	passengerOutputStream = new ObjectOutputStream(passengerSocket.getOutputStream());
	  		passengerOutputStream.writeObject(action); //send taxi cancel to the passenger
	    	passengerOutputStream.flush();
	    	passengerOutputStream.close();
	    	passengerSocket.close();
	  		passenger = null;
	  		taxi.setPassengerIp(null);
      }  	
		} catch (Exception e) {
			e.printStackTrace();
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