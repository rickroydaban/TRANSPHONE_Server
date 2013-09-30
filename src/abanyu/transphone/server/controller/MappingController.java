package abanyu.transphone.server.controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import abanyu.transphone.server.model.MappingData;
import abanyu.transphone.server.model.ServerData;
import abanyu.transphone.server.view.ActionMenu;
import abanyu.transphone.server.view.MapPanel;
import abanyu.transphone.server.view.NoConnectionView;
import abanyu.transphone.server.view.ServerFrame;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserFunction;

public class MappingController implements ActionListener{
	private ServerFrame serverFrame;
	private ServerData serverData;
	private MapPanel mapView;
	private MappingData mappingData;
	private String[] taxiArray;
	private int zoomLevel = 14;
	private JDialog dlg;
	
	public MappingController(ServerData pServerData, ServerFrame pServerFrame){
		mapView = new MapPanel();
		mapView.getButtonOn().addActionListener(this);
		mapView.getButtonOff().addActionListener(this);
		mapView.getZoomInButton().addActionListener(this);
		mapView.getZoomOutButton().addActionListener(this);
		mapView.getManageButton().addActionListener(this);
		
		serverFrame = pServerFrame;
		mappingData = pServerData.getMappingData();
		serverData = pServerData;
	}

	public void operate(){
		serverFrame.setContentPane(mapView.getMapView());

		mapView.getWebBrowser().registerFunction(new WebBrowserFunction("getPreferredDimensions") {
			@Override
			public Object invoke(JWebBrowser webBrowser, Object... arg1) {
				return new Object[] {mapView.getScreenWidth(), mapView.getScreenHeight()};
			}
		});	
		
 		mapView.getWebBrowser().registerFunction(new WebBrowserFunction("getMarkers") {
 			@Override
 			public String[] invoke(JWebBrowser webBrowser, Object... arg1) {
 				return taxiArray;
 			}
 		});
				
 		mapView.getWebBrowser().registerFunction(new WebBrowserFunction("getZoomLevel") {
 			@Override
 			public Object invoke(JWebBrowser webBrowser, Object... arg1) {
 				return zoomLevel;
 			}
 		});
		saveDBDriverList();
		saveDBTaxiList();

 		serverFrame.invalidate();
		serverFrame.validate();				
	}
	
	public void setWebMarkers(String [] pTaxiArray){     
		if(pTaxiArray.length<1)
			taxiArray = null;
		else
			taxiArray = pTaxiArray;
	}
		
	@Override
	public void actionPerformed(ActionEvent e) {
	 	if(e.getSource() == mapView.getButtonOn()){
	 		mapView.getButtonOff().setVisible(true);
	 		mapView.getButtonOn().setVisible(false);
	 		mapView.getButtonOn().setBackground(Color.WHITE);
	 		mapView.getButtonOff().setBackground(Color.decode("#22AA22"));
	 		mapView.getPowerButtonPanel().setBackground(Color.decode("#22AA22"));
	 		mapView.getZoomButtonPanel().setVisible(true);
	 		mapView.getDataPanel().setVisible(true);
	 		
	 		dlg = new JDialog(serverFrame, "Progress Dialog", true);
	 		JProgressBar dpb = new JProgressBar(0, 500);
	 		dlg.add(BorderLayout.CENTER, dpb);
	 		dlg.add(BorderLayout.NORTH, new JLabel("Progress..."));
	 		dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	 		dlg.setSize(300, 75);
	 		dlg.setLocationRelativeTo(serverFrame);
	 		
	 		new Thread(new ConnectionManager(mappingData, mapView, this)).start();	 		
	  }else	if(e.getSource() == mapView.getButtonOff()){
			mapView.getButtonOff().setVisible(false);
			mapView.getButtonOn().setVisible(true);
			mapView.getButtonOn().setBackground(Color.WHITE);
			mapView.getButtonOff().setBackground(Color.GREEN);
			mapView.getPowerButtonPanel().setBackground(Color.WHITE);
			mapView.getWebBrowser().setVisible(false);
	  
			//Closes the server socket
		  mappingData.killServer();

		  try {
				mappingData.getServerSocket().close();
			} catch (IOException e1) {
				mapView.getStatusField().setText("Cannot Stop the Server..");
			}
			
		  mappingData.getPassengerList().clear();
			mapView.getClientCountField().setText(String.valueOf(0));
			mappingData.getTaxiList().clear();
			mapView.getTaxiCounterField().setText(String.valueOf(0));			
		}else	if(e.getSource() == mapView.getZoomInButton()){
//			mapView.getWebBrowser().navigate(mappingData.getConnectionData().getDBUrl()+"/thesis/multiplemarkers.php?fname=zoomIn");			
			if(zoomLevel<18)	
				zoomLevel++;
		}else	if(e.getSource() == mapView.getZoomOutButton()){
//			mapView.getWebBrowser().navigate(mappingData.getConnectionData().getDBUrl()+"/thesis/multiplemarkers.php?fname=zoomOut");
			if(zoomLevel>9)	
				zoomLevel--;
		}else if(e.getSource() == mapView.getManageButton()){
			 SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
             new ActionMenu().displayGUI(serverFrame,serverData);
         }
     });			
		}
	}	
	
	public JDialog getLoadingDialog(){
		return dlg;
	}
	
	public void saveDBTaxiList(){
		InputStream inputStream = null;
	  List<String> plateNoList = new ArrayList<String>();
	  
		try {
			inputStream = new URL(serverData.getMappingData().getConnectionData().getDBUrl()+"/thesis/dbmanager.php?fname=getTaxiInfos&arg1="+serverData.getLoginData().getSelectedCompany()).openStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
      JSONArray companies = new JSONArray(readAll(rd));
      
      ArrayList<HashMap<String, String>> taxiList = new ArrayList<HashMap<String, String>>();
	  	for (int i = 0; i < companies.length(); i++) {
	  		Map<String, String> taxi = new HashMap<String, String>();
	  		JSONObject jo = companies.getJSONObject(i);
      
	  		//RETRIEVE EACH JSON OBJECT'S FIELDS
	  		taxi.put("plateNo", String.valueOf(jo.getString("plateNo")));
	  		taxi.put("bodyNo", jo.getString("bodyNo"));
	  		taxi.put("description", jo.getString("description"));
      	  		
				plateNoList.add(taxi.get("plateNo"));
	  		taxiList.add((HashMap<String, String>) taxi);
	  	}
	  	
	  	
			serverData.getLoginData().setPlateNumbers(plateNoList.toArray(new String[plateNoList.size()]));
			serverData.getLoginData().setTaxiList(taxiList);
			
		} catch (IOException e) {
			System.out.println("io exception at saveDBTaxiList: "+e.getMessage());
			serverFrame.setContentPane(new NoConnectionView(serverFrame, serverData).getErrorPanel());
			serverFrame.invalidate();
			serverFrame.validate();
		} catch (JSONException e) {
			System.out.println("json exception at saveDBTaxiList: "+e.getMessage());
		}     

	}
	
	public void saveDBDriverList(){
		InputStream inputStream = null;
	  List<String> driverNameList = new ArrayList<String>();
	  
		try {
			inputStream = new URL(serverData.getMappingData().getConnectionData().getDBUrl()+"/thesis/dbmanager.php?fname=getDriverInfos&arg1="+serverData.getLoginData().getSelectedCompany()).openStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
      JSONArray companies = new JSONArray(readAll(rd));
      
      ArrayList<HashMap<String, String>> driverList = new ArrayList<HashMap<String, String>>();
	  	for (int i = 0; i < companies.length(); i++) {
	  		Map<String, String> driver = new HashMap<String, String>();
	  		JSONObject jo = companies.getJSONObject(i);
      
	  		//RETRIEVE EACH JSON OBJECT'S FIELDS
	  		driver.put("name", jo.getString("name"));
	  		driver.put("license", jo.getString("license"));
	  		driver.put("password", jo.getString("password"));
      	  		
				driverNameList.add(jo.getString("name"));
				System.out.println(jo.getString("name"));
	  		driverList.add((HashMap<String, String>) driver);
	  	}
	  	
	  	
	  	serverData.getLoginData().setDriverNames(driverNameList.toArray(new String[driverNameList.size()]));
	  	serverData.getLoginData().setDriverList(driverList);
			
		} catch (IOException e) {
			System.out.println("io exception at saveDBTaxiList: "+e.getMessage());
			serverFrame.setContentPane(new NoConnectionView(serverFrame, serverData).getErrorPanel());
			serverFrame.invalidate();
			serverFrame.validate();
		} catch (JSONException e) {
			System.out.println("json exception at saveDBTaxiList: "+e.getMessage());
		}     

	}

  private String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

}
