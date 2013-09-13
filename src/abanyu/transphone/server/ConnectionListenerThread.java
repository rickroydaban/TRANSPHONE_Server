//package abanyu.transphone.server;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.lang.reflect.InvocationTargetException;
//import java.net.Socket;
//import java.util.Set;
//
//import javax.swing.SwingUtilities;
//
//import actors.*;
//import connections.MyConnection;
//import data.TaxiStatus;
//
//
//class ConnectionListenerThread implements Runnable {		
//    private Socket clientSocket;
//    private MyConnection conn;
//    private ObjectOutputStream clientOutputStream;
//    public ObjectInputStream clientInputStream;
//		
//    public ConnectionListenerThread(Socket pClientSocket, MyConnection pConn) {
//      clientSocket = pClientSocket;
//      conn = pConn;
//    }
//		
//    @Override
//    public void run(){
//      try {
//    	  //manages objects sent by the client connections
//    	  clientInputStream = new ObjectInputStream(clientSocket.getInputStream());
//    	  clientOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
//        
//        Object inputObject = clientInputStream.readObject(); //must be stored the value here
//          
//        if(inputObject instanceof MyPassenger){
//        	System.out.println("a passenger has connected to the system!");
//          //links to the object data sent by the client connection
//          MyPassenger myPassenger = (MyPassenger) inputObject;
//          boolean keyFound = ServerThread.passengerList.containsKey(myPassenger.getIp());
//          
//          if(keyFound)
//          {
//        	  ServerThread.passengerList.remove(myPassenger.getIp());
//          	int clientCount = Integer.parseInt(ServerMap.clientCountField.getText());
//            ServerMap.clientCountField.setText(String.valueOf(--clientCount));
//          }
//        	
//          else
//          {
//            MyTaxi nearestTaxi = findShortestTaxiDistance(myPassenger);
//            if(nearestTaxi != null)
//            {
//              int clientCount = Integer.parseInt(ServerMap.clientCountField.getText());
//              ServerMap.clientCountField.setText(String.valueOf(++clientCount));
//              ServerThread.passengerList.put(myPassenger.getIp(), myPassenger);
//              nearestTaxi.setStatus(TaxiStatus.requested);
//              
//			        //Send requesting passenger data to nearest taxi
//              Socket socket = new Socket(nearestTaxi.getIP(), conn.getTaxiPort());
//			        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
//			        outputStream.writeObject(myPassenger);
//			        outputStream.flush();
//			        if(outputStream != null)
//			          outputStream.close();
//			        if(socket != null)
//		            socket.close();
//            }
//            
//            //Send nearest taxi data to requesting passenger
//            Socket passengerSocket = new Socket(myPassenger.getIp(), conn.getPassengerPort());
//            ObjectOutputStream passengerOutputStream = new ObjectOutputStream(passengerSocket.getOutputStream());
//            passengerOutputStream.writeObject(nearestTaxi);
//            passengerOutputStream.flush();
////            if(passengerOutputStream != null)
////            	passengerOutputStream.close();
////            if(passengerSocket != null)
////            	passengerSocket.close();
//          }
//        }
//        else{
//        	System.out.println("a taxi has connected to the system!");
//          //links to the object data sent by the client(taxi) connection
//          MyTaxi myTaxi = (MyTaxi) inputObject;
//          boolean keyFound = ServerThread.taxiList.containsKey(myTaxi.getPlateNumber());
//            
//          System.out.println("is on the list: "+keyFound);
//          System.out.println("plate Number: "+myTaxi.getPlateNumber());
//          System.out.println("taxi status: "+myTaxi.getStatus());
//          System.out.println("coords: "+myTaxi.getCurLat()+","+myTaxi.getCurLng());
//          if(myTaxi.getStatus() != TaxiStatus.unavailable && myTaxi.getStatus() != TaxiStatus.disconnected){
//            if(keyFound){
//              MyTaxi curTaxi = ServerThread.taxiList.get(myTaxi.getPlateNumber());
//            	
//              if(curTaxi.getCurLat() != 0 && curTaxi.getCurLng() != 0){
//            	double curDistance = distance(curTaxi.getCurLat(), curTaxi.getCurLng(), myTaxi.getCurLat(), myTaxi.getCurLng());
//            	double distanceTraveled = curTaxi.getDistance() + curDistance;
//            	myTaxi.setDistance(distanceTraveled);
//              }
//            }else{
//            	//update number of taxi count
//            	int taxiCount = Integer.parseInt(ServerMap.taxiCountField.getText());
//            	ServerMap.taxiCountField.setText(String.valueOf(++taxiCount));
//            }
//            
//            //update the taxi coordinates in the view
//            ServerMap.taxiLat = myTaxi.getCurLat();
//            ServerMap.taxiLng  = myTaxi.getCurLng();
//            
//            //update number of taxi count in the view
//          	SwingUtilities.invokeAndWait(new Runnable(){
//                public void run() {
//    		      ServerMap.webBrowser.navigate("http://localhost/thesis/multiplemarkers.php?fname=addMarker&arg1="+ServerMap.taxiLat+"&arg2="+ServerMap.taxiLng);
//                }
//          	});
//          	  
//          	//automatically updates myTaxi object pair using its plate number
//           	//if the plate number does not exist in the list, it will be automatically inserted
//          	System.out.println(myTaxi.getPlateNumber());
//          	ServerThread.taxiList.put(myTaxi.getPlateNumber(), myTaxi);          	
//          }
//            
//          else{
//            if(keyFound) {
//              MyTaxi curTaxi = ServerThread.taxiList.get(myTaxi.getPlateNumber());
//            	
//              if(curTaxi.getStatus()==TaxiStatus.requested || curTaxi.getStatus()==TaxiStatus.occupied){
//                MyPassenger curPassenger = ServerThread.passengerList.get(curTaxi.getPassengerIP());
//                
//                if(curPassenger != null){	
//                  MyTaxi nearestTaxi = findShortestTaxiDistance(curPassenger);
//                  if(nearestTaxi != null)
//                    nearestTaxi.setStatus(TaxiStatus.requested);
//                
//                  //Send new taxi data to current passenger of disconnected taxi
//                  Socket passengerSocket = new Socket(curPassenger.getIp(), conn.getPassengerPort());
//  			          ObjectOutputStream passengerOutputStream = new ObjectOutputStream(passengerSocket.getOutputStream());
//  			          passengerOutputStream.writeObject(nearestTaxi);
//  			          passengerOutputStream.flush();
//  			          if(passengerOutputStream != null)
//  			          	passengerOutputStream.close();
//  			          if(passengerSocket != null)
//  			          	passengerSocket.close();
//
//  			          //Send passenger data to taxi if there is one available
//  			          if(nearestTaxi != null){
//  			          	Socket taxiSocket = new Socket(nearestTaxi.getIP(), conn.getTaxiPort());
//  			          	ObjectOutputStream taxiOutputStream = new ObjectOutputStream(taxiSocket.getOutputStream());
//  			          	taxiOutputStream.writeObject(curPassenger);
//  			          	taxiOutputStream.flush();
////  			          	if(taxiOutputStream != null)
////  			          		taxiOutputStream.close();
////  			          	if(taxiSocket != null)
////  			          		taxiSocket.close();
//  			          }
//                }
//              }
//            	
//              if(myTaxi.getStatus() == TaxiStatus.disconnected)	//edited
//              {
//              	ServerThread.taxiList.remove(curTaxi.getPlateNumber());
//              	int taxiCount = Integer.parseInt(ServerMap.taxiCountField.getText());
//              	ServerMap.taxiCountField.setText(String.valueOf(--taxiCount));
//              }
//            }
//          }
//        }
//	  } catch (IOException e) {
//        System.out.println("server io exception: "+e.getMessage()+e.getCause());
//	  } catch (ClassNotFoundException e) {
//      	System.out.println("server class not found exception"+e.getMessage());
//	  } catch (InterruptedException e) {
//      	System.out.println("server interrupted exception"+e.getMessage());
//	  } catch (InvocationTargetException e) {
//      	System.out.println("server invocation targer exception"+e.getMessage());
//	  }
////      finally{
////		  try{
////			  if(clientInputStream!= null)
////				  clientInputStream.close();
////			  if(clientOutputStream!= null)
////				  clientOutputStream.close();
////			  if(!clientSocket.isClosed())
////			    clientSocket.close();
////          } catch (IOException e) {
////  		    System.out.println(e.getMessage());
////        }
////      }
//	}
//    
//    private MyTaxi findShortestTaxiDistance(MyPassenger pMyPassenger) {
//    	//Find the nearest taxi and send its information to the passenger
//        double shortestDistance = 0, //the shortest distance record within the loop
//        	   curDistance = 0;      //the distance estimated by comparing the current taxi's location to the client's source location
//        
//        MyTaxi nearestTaxi = null; //the data of the resulting nearest taxi estimation
//        
//        Set<String> keys = ServerThread.taxiList.keySet(); //get all the keys of the taxi record
//        
//        //loop to find the nearest taxi
//        for(String curKey:keys){
//          MyTaxi curTaxi = ServerThread.taxiList.get(curKey);
//          if(curTaxi.getStatus() == TaxiStatus.vacant)
//      	  {
//        	  if(shortestDistance==0){
//        		  shortestDistance= distance( pMyPassenger.getCurLat(),pMyPassenger.getCurLng(), //calculate the distance of the passenger to the current taxi's location
//            						    curTaxi.getCurLat(),curTaxi.getCurLng());
//        		  System.out.println("Plate Number: "+ curTaxi.getPlateNumber()+", Distance: "+shortestDistance);
//        	  }else{
//        		  curDistance = distance( pMyPassenger.getCurLat(),pMyPassenger.getCurLng(),
//				                    curTaxi.getCurLat(),curTaxi.getCurLng());
//        		  System.out.println("Plate Number: "+ curTaxi.getPlateNumber()+", Distance: "+curDistance);
//        	  }
//          
//        	  if(curDistance==0 || curDistance<shortestDistance)
//        	  {
//        		  nearestTaxi=curTaxi;  
//        		  if(curDistance!=0)
//        			  shortestDistance=curDistance; //the current distance is the shortest distance
//        	  }
//          } 
//        }
//        return nearestTaxi;
//    }
//    
//    private double distance(double lat1, double lon1, double lat2, double lon2) {
//	double theta = lon1 - lon2;
//		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
//		dist = Math.acos(dist);
//		dist = rad2deg(dist);
//		dist = dist * 60 * 1.1515;
//		dist = dist * 1.609344;
//		return (dist);
//	}
//
//	//This function converts decimal degrees to radians
//	private double deg2rad(double deg) {
//		return (deg * Math.PI / 180.0);
//	}
//
//	//This function converts radians to decimal degrees
//	private double rad2deg(double rad) {
//		return (rad * 180.0 / Math.PI);
//	}
//  }