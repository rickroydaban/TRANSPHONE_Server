package abanyu.transphone.server.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

import abanyu.transphone.server.model.MappingData;
import abanyu.transphone.server.view.MapPanel;

public class ConnectionManager implements Runnable{
	private MappingData mappingData;
	private MapPanel mappingView;
	private MappingController mappingController;
	private ServerSocket server;
	
	public ConnectionManager(MappingData pMappingData, MapPanel pMappingView, MappingController pMappingController) {
		mappingData = pMappingData;
		mappingView = pMappingView;
		mappingController = pMappingController;

		try {
			server = new ServerSocket(mappingData.getConnectionData().getServerPort());
			mappingData.setServerSocket(server);	  
			InetAddress ip=InetAddress.getLocalHost();
			mappingData.getConnectionData().setServerIp(ip.getHostAddress());
			String url = "http://testphone.freetzi.com/thesis/dbmanager.php?fname=setServerIP&arg1="+ip.getHostAddress();
			new URL(url).openStream();
	
			mappingView.getStatusField().setText("ONLINE. LISTENING FOR REQUESTS... ");
			mappingView.getWebBrowserPanel().setVisible(true);
			mappingData.liveServer();
		}catch (Exception e) {
			mappingView.getStatusField().setText("ERROR: "+e.getMessage());		
		}
	}
	
	@Override
	public void run(){
		while(mappingData.isServerAlive()){									
			try {
				Socket clientSocket = server.accept();
								
				new Thread(new OnClientConnectManager(clientSocket, mappingData, mappingView, mappingController)).start();
			} catch (IOException e) {
				System.out.println("Failed to receive the data from the client connection");
			}
		}
	}	
}
