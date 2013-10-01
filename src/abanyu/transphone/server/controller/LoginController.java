package abanyu.transphone.server.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import abanyu.transphone.server.model.LoginData;
import abanyu.transphone.server.model.ServerData;
import abanyu.transphone.server.view.LoginPanel;
import abanyu.transphone.server.view.NoConnectionView;
import abanyu.transphone.server.view.ServerFrame;

public class LoginController implements ActionListener{
	ServerFrame serverFrame;
	ServerData serverData;
	LoginData loginData;
	LoginPanel loginView;
	boolean hasConnection = false;
//	private JDialog dlg;
	private InputStream inputStream;
	
	public LoginController(ServerData pServerData, ServerFrame pServerFrame){
		loginView = new LoginPanel();
		loginView.getLoginButton().addActionListener(this);
		loginView.getClearButton().addActionListener(this);
		serverFrame = pServerFrame;
		loginData = pServerData.getLoginData();
		serverData = pServerData;		
	}
	
	public void operate(){
		serverFrame.setContentPane(loginView.getLoginPanel());
		serverFrame.invalidate();
		serverFrame.validate();
	}
		
  private String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == loginView.getLoginButton()){
			String username = loginView.getUsernameField().getText().toString();
			String password = String.valueOf(loginView.getPasswordField().getPassword());
			String result = null;
			try {
				String url = serverData.getMappingData().getConnectionData().getDBUrl()+"/thesis/dbmanager.php?fname=serverLogin&arg1="+username+"&arg2="+password;
				inputStream = new URL(url).openStream();
	      BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
	      result = readAll(rd);
	      JSONArray ja = new JSONArray(result);
	      JSONObject jo = ja.getJSONObject(0);
	      
	      serverData.getLoginData().setSelectedCompany(jo.getInt("id"));
	      new MappingController(serverData, serverFrame).operate();
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (JSONException e1) {
				System.out.println(result);
			}			
		}
		
		if(e.getSource() == loginView.getClearButton()){
			loginView.getPasswordField().setText("");
			loginView.getMessagePanel().setVisible(false);
		}
	}

}
