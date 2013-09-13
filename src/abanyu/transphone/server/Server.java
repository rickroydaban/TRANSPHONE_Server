package abanyu.transphone.server;

import javax.swing.SwingUtilities;

import abanyu.transphone.server.controller.LoginController;
import abanyu.transphone.server.model.ServerData;
import abanyu.transphone.server.view.ServerFrame;
import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;

public class Server {		
	public static void main(String[] args) {
    NativeInterface.open();
    UIUtils.setPreferredLookAndFeel();
        
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
      	ServerData serverData = new ServerData();
      	ServerFrame serverFrame = new ServerFrame();
      	
      	//if there is an existing internet connection
      	new LoginController(serverData, serverFrame).operate();
      	//else show error here
      }
    });
    
    NativeInterface.runEventPump();
  }
}
