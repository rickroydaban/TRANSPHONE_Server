package abanyu.transphone.server.controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import abanyu.transphone.server.model.MappingData;
import abanyu.transphone.server.model.ServerData;
import abanyu.transphone.server.view.ActionMenu;
import abanyu.transphone.server.view.MapPanel;
import abanyu.transphone.server.view.ServerFrame;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserFunction;

public class MappingController implements ActionListener{
	private ServerFrame serverFrame;
	private ServerData serverData;
	private MapPanel mapView;
	private MappingData mappingData;
	private String[] taxiArray;
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
//			mapView.getWebBrowser().
			mapView.getWebBrowser().navigate("http://localhost/thesis/multiplemarkers.php?fname=zoomIn");			
		}else	if(e.getSource() == mapView.getZoomOutButton()){
			mapView.getWebBrowser().navigate("http://localhost/thesis/multiplemarkers.php?fname=zoomOut");
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
}
