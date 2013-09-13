///*
// * Christopher Deckers (chrriis@nextencia.net)
// * http://www.nextencia.net
// *
// * See the file "readme.txt" for information on usage and redistribution of
// * this file, and for a DISCLAIMER OF ALL WARRANTIES.
// */
//package abanyu.transphone.server;
//
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.FlowLayout;
//import java.awt.Font;
//import java.awt.GraphicsEnvironment;
//import java.awt.GridBagLayout;
//import java.awt.Toolkit;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.Reader;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.nio.charset.Charset;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.swing.BorderFactory;
//import javax.swing.BoxLayout;
//import javax.swing.JButton;
//import javax.swing.JComboBox;
//import javax.swing.JComponent;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.JPasswordField;
//import javax.swing.JTextField;
//import javax.swing.SwingUtilities;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import chrriis.common.UIUtils;
//import chrriis.dj.nativeswing.swtimpl.NativeInterface;
//import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
//import chrriis.dj.nativeswing.swtimpl.components.WebBrowserFunction;
//
///**
// * @author Christopher Deckers
// */
//public class ServerMap {
//  //global static declarations for needed elements
//  static JPanel powerPanel, controlPanel, webBrowserPanel, messagePanel;
//  static JTextField statusField, taxiCountField, clientCountField;
//  static JButton buttonOn, buttonOff, zoomInButton, zoomOutButton, loginButton;
//  static JComboBox companyListBox;
//  static JPasswordField passwordField;
//  static JWebBrowser webBrowser=null;
//
//  static ActionListener actionListener = getButtonListener();
//  static double screenWidth, screenHeight;
//  
//  static double taxiLat, taxiLng;
//  static boolean isServerThreadRunning = false;
//  static List<HashMap<String, String>> companyList;
//  static JFrame frame;
//  public static JComponent createLoginViewContent() {
//    InputStream is = null;
//    JSONArray companies;
//	  List<String> companyNameList = new ArrayList<String>();
//	  String [] companyNameArray;
//	  
//		try {
//			is = new URL("http://testphone.freetzi.com/thesis/dbmanager.php?fname=getCompanies").openStream();
//      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
//      companies = new JSONArray(readAll(rd));
//
//      companyList = new ArrayList<HashMap<String, String>>();
//	  	for (int i = 0; i < companies.length(); i++) {
//	  		Map<String, String> company = new HashMap<String, String>();
//	  		// GET INDIVIDUAL JSON OBJECT FROM JSON ARRAY
//	  		JSONObject jo = companies.getJSONObject(i);
//      
//	  		// RETRIEVE EACH JSON OBJECT'S FIELDS
//	  		company.put("id", String.valueOf(jo.getString("id")));
//	  		company.put("name", jo.getString("name"));
//	  		company.put("password", jo.getString("password"));
//	  		company.put("contact", jo.getString("contact"));
//	  		company.put("ip", jo.getString("serverip"));
//      	  		
//	  		companyNameList.add(company.get("name"));
//	  		companyList.add((HashMap<String, String>) company);
//	  	}
//
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//      try {
//				is.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//    }      
//  	
//		companyNameArray = new String[companyNameList.size()];
//		companyNameList.toArray(companyNameArray);
//  	  	
//  	
//  	JPanel mainPanel = new JPanel(new GridBagLayout());
//
//  	
//  	JPanel companyPanel = new JPanel();
//  	
//  	JLabel companyLabel = new JLabel("Company");
//    companyLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
//    companyLabel.setFont(new Font("Calibri",Font.BOLD,16));
//    companyLabel.setForeground(Color.decode("#343434"));
//  	
//  	companyListBox = new JComboBox(companyNameArray);
//  	companyListBox.setSelectedIndex(0);
//  	companyListBox.setPreferredSize(new Dimension(200, 35));
//  	companyListBox.setFont(new Font("Calibri",Font.PLAIN,15));
//  	companyListBox.setBorder(BorderFactory.createCompoundBorder(
//  													companyListBox.getBorder(), 
//    												BorderFactory.createEmptyBorder(5, 5, 5, 5)));
//  	
//  	companyPanel.add(companyLabel);
//  	companyPanel.add(companyListBox);
//  	
//  	JPanel passwordPanel = new JPanel();
//    
//    JLabel passwordLabel = new JLabel("Password");
//    passwordLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
//    passwordLabel.setFont(new Font("Calibri",Font.BOLD,16));
//    passwordLabel.setForeground(Color.decode("#343434"));
//    
//    passwordField = new JPasswordField();
//    passwordField.setPreferredSize(new Dimension(200, 35));
//    passwordField.setFont(new Font("Calibri",Font.PLAIN,15));
//    passwordField.setBorder(BorderFactory.createCompoundBorder(
//    												passwordField.getBorder(), 
//    												BorderFactory.createEmptyBorder(5, 5, 5, 5)));
//    
//    passwordPanel.add(passwordLabel);
//    passwordPanel.add(passwordField);
//    
//    JLabel messageLabel = new JLabel("Password is incorrect");
//    messageLabel.setForeground(Color.RED);
//    messageLabel.setPreferredSize(new Dimension(280,30));
//  	messageLabel.setBorder(BorderFactory.createCompoundBorder(
//  			BorderFactory.createLineBorder(Color.decode("#8B0000")), 
//				BorderFactory.createEmptyBorder(20,20,20,20)));
//    messageLabel.setHorizontalAlignment(JLabel.CENTER);
//  	messagePanel = new JPanel();
//  	messagePanel.add(messageLabel);
//  	messagePanel.setVisible(false);
//    JPanel loginButtonPanel = new JPanel();
//    loginButton = new JButton("Login");
//    loginButton.setPreferredSize(new Dimension(280,35));
//    //    loginButton.setPreferredSize(new Dimension(100, 35));
//    loginButton.setFont(new Font("Calibri",Font.PLAIN,15));
//    loginButton.addActionListener(actionListener);
//    
//    loginButtonPanel.add(loginButton);
//    
//  	JPanel loginPanel = new JPanel();
//  	loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
//  	loginPanel.setBorder(BorderFactory.createCompoundBorder(
//  			BorderFactory.createLineBorder(Color.decode("#ABABAB")), 
//				BorderFactory.createEmptyBorder(20,20,20,20)));
//  	loginPanel.add(companyPanel);
//    loginPanel.add(passwordPanel);
//    loginPanel.add(messagePanel);
//    loginPanel.add(loginButtonPanel);
//    
//    mainPanel.add(loginPanel);
//    mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
//    
//    mainPanel.setBackground(Color.WHITE);
//    return mainPanel;
//  }
//  
//  public static JComponent createWebViewContent() {
//  	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //manage to get size of the host computer
//  	screenWidth	 = screenSize.getWidth()-35; //needed in resizing the preferred width of our embedded website
//  	screenHeight = screenSize.getHeight()-140; //needed in resizing the preferred height of you embedded website
//	
////  	actionListener = getButtonListener();  //defines actionlisteners to server control buttons
//
//    webBrowser = new JWebBrowser(); //instantiates a new Java Web Browser
//    webBrowser.navigate("http://localhost/thesis/multiplemarkers.php"); //defines what site to be viewed on the java web browser
//    webBrowser.setBarsVisible(false); //hides urls and other native browser elements
//    
//    makeWebsiteFunctions();
//    
//    JPanel contentPane=new JPanel(new BorderLayout());
//    webBrowserPanel=new JPanel(new BorderLayout());
//    webBrowserPanel.setBorder(BorderFactory.createTitledBorder("Server")); //creates a beveled border
//    webBrowserPanel.add(webBrowser, BorderLayout.CENTER);
//    webBrowserPanel.setVisible(false);
//    
//    buttonOn=new JButton(" Turn On"); 
//    buttonOn.setVisible(true);    
//    buttonOn.addActionListener(actionListener);
//
//    buttonOff=new JButton("Turn Off");
//    buttonOff.setVisible(false); 
//    buttonOff.addActionListener(actionListener);
//
//    powerPanel=new JPanel(new BorderLayout());
//    powerPanel.setPreferredSize(new Dimension(95,30));
//    powerPanel.setBackground(Color.WHITE);
//    powerPanel.setBorder(BorderFactory.createLoweredBevelBorder());
//    powerPanel.add(buttonOn,BorderLayout.WEST);
//    powerPanel.add(buttonOff,BorderLayout.EAST);
//  
//    //panel for status
//    JLabel statusLabel=new JLabel("Status: ");
//    statusLabel.setPreferredSize(new Dimension(70,30));
//    statusLabel.setHorizontalAlignment(JLabel.RIGHT);
//    statusLabel.setFont(new Font("Calibri",Font.BOLD,15));
//    
//    statusField=new JTextField();
//    statusField.setEditable(false);
//    statusField.setPreferredSize(new Dimension(500,30));
//    statusField.setText("OFFLINE. Please switch on to start server...");
//    statusField.setFont(new Font("Calibri",Font.PLAIN,15));
//    statusField.setBackground(Color.WHITE);
//    statusField.setBorder(BorderFactory.createCompoundBorder(
//    		              		statusField.getBorder(), 
//                          BorderFactory.createEmptyBorder(5, 5, 5, 5)));
//    
//    //panel for number of taxi on operation
//    JLabel taxiCountLabel=new JLabel("Units: ");
//    taxiCountLabel.setPreferredSize(new Dimension(50,30));
//    taxiCountLabel.setHorizontalAlignment(JLabel.RIGHT);
//    taxiCountLabel.setFont(new Font("Calibri",Font.BOLD,15));
//
//    taxiCountField = new JTextField("0");
//    taxiCountField.setEditable(false);
//    taxiCountField.setPreferredSize(new Dimension(50,30));
//    taxiCountField.setHorizontalAlignment(JTextField.CENTER);
//    taxiCountField.setFont(new Font("Calibri",Font.PLAIN,14));
//    taxiCountField.setBackground(Color.WHITE);
//    
//    //panel for number of requesting passengers
//    JLabel clientCountLabel = new JLabel("Clients: ");
//    clientCountLabel.setPreferredSize(new Dimension(60,30));
//    clientCountLabel.setHorizontalAlignment(JLabel.RIGHT);
//    clientCountLabel.setFont(new Font("Calibri",Font.BOLD,15));
//    
//    clientCountField = new JTextField("0");
//    clientCountField.setEditable(false);
//    clientCountField.setPreferredSize(new Dimension(50,30));
//    clientCountField.setHorizontalAlignment(JTextField.CENTER);
//    clientCountField.setFont(new Font("Calibri",Font.PLAIN,14));
//    clientCountField.setBackground(Color.WHITE);
//
//    zoomInButton = new JButton("Zoom In");
//    zoomInButton.addActionListener(actionListener);
//    zoomOutButton = new JButton("Zoom Out");
//    zoomOutButton.addActionListener(actionListener);
//
//    JPanel zoomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
//    zoomPanel.setBackground(Color.WHITE);
//    zoomPanel.setBorder(BorderFactory.createLineBorder(Color.decode("#BBBBBB")));
//    zoomPanel.add(zoomInButton);
//    zoomPanel.add(zoomOutButton);
//    
//    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
//    buttonPanel.add(powerPanel);
//    buttonPanel.add(statusLabel);
//    buttonPanel.add(statusField);
//    buttonPanel.add(taxiCountLabel);
//    buttonPanel.add(taxiCountField);
//    buttonPanel.add(clientCountLabel);
//    buttonPanel.add(clientCountField);
//
//    JPanel controlPanel = new JPanel(new BorderLayout());
//    controlPanel.add(buttonPanel,BorderLayout.CENTER);
//    controlPanel.add(zoomPanel,BorderLayout.EAST);
//    
//    contentPane.add(webBrowserPanel, BorderLayout.CENTER);
//    contentPane.add(controlPanel, BorderLayout.SOUTH);    
//    
//    return contentPane;
//  }
//
//  /* Standard main method to try that test as a standalone application. */
//  public static void main(String[] args) {
//	//get the user screen resolution
//    NativeInterface.open();
//    UIUtils.setPreferredLookAndFeel();
//        
//    SwingUtilities.invokeLater(new Runnable() {
//      public void run() {
//        frame = new JFrame("SERVER");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setContentPane(createLoginViewContent());
//        frame.setSize((int)GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth(), 
//        		      (int)GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getHeight());
//        frame.setLocationByPlatform(true);
//        frame.setVisible(true);
//        frame.setLocationRelativeTo(null);
//        frame.setResizable(false);
//      }
//    });
//    NativeInterface.runEventPump();
//  }
//  
//  private static ActionListener getButtonListener(){
//	  ActionListener listener;
//	  
//	  listener = new ActionListener() {
//		@Override
//		public void actionPerformed(ActionEvent e) {
//			if(e.getSource() == buttonOn){
//			  buttonOff.setVisible(true);
//			  buttonOn.setVisible(false);
//			  buttonOn.setBackground(Color.WHITE);
//			  buttonOff.setBackground(Color.decode("#22AA22"));
//			  powerPanel.setBackground(Color.decode("#22AA22"));
//			  
//			  if(!isServerThreadRunning){
//				  isServerThreadRunning = true;
//				  new Thread(new ServerThread()).start();
//			  }
//			  
//			}else if(e.getSource() == buttonOff){
//			  buttonOff.setVisible(false);
//			  buttonOn.setVisible(true);
//			  buttonOn.setBackground(Color.WHITE);
//			  buttonOff.setBackground(Color.GREEN);
//			  powerPanel.setBackground(Color.WHITE);
//			  webBrowserPanel.setVisible(false);
//			  
//			  //clear passenger and taxi lists
//			  ServerThread.passengerList.clear();
//			  clientCountField.setText(String.valueOf(0));
//			  ServerThread.taxiList.clear();
//			  taxiCountField.setText(String.valueOf(0));
//			  
//			  //Closes the server socket
//			  if(isServerThreadRunning)
//			  {
//				  isServerThreadRunning = false;
//				  try {
//					ServerThread.serverSocket.close();
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
//			  }
//			
//			}else if(e.getSource() == zoomInButton){
//			  webBrowser.navigate("http://localhost/thesis/multiplemarkers.php?fname=zoomIn");
//			}else if(e.getSource() == zoomOutButton){
//				  webBrowser.navigate("http://localhost/thesis/multiplemarkers.php?fname=zoomOut");
//			}else if(e.getSource() == loginButton){
//					String companyName = (String)companyListBox.getSelectedItem();
//					String password = String.valueOf(passwordField.getPassword());
//					
//			    for(HashMap<String, String> map: companyList) {
//			    	if(map.get("name").equals(companyName)){
//			    		if(map.get("password").equals(password)){
////			    			loginButton.setText("may now proceed to server");
//			    				frame.setContentPane(createWebViewContent());
//			    				frame.invalidate();
//			    				frame.validate();
//			    		}else{
//			    			messagePanel.setVisible(true);
//			    		}
//			    			
//			    	}
//			    }
//
//			}
//		}
//	};
//	  
//	  return listener;
//  }
//
//  //we define the functions here that will be used by the website we are using
//  //this will be usedful especially when adding markers on the map
//  private static void makeWebsiteFunctions(){
//    webBrowser.registerFunction(new WebBrowserFunction("getPreferredDimensions") { //defines a callable function on the specified website
//      @Override
//      public Object invoke(JWebBrowser webBrowser, Object... args) {
//        return new Object[] {screenWidth,screenHeight};
//      }
//    });
//  }
//
//  
//  private static String readAll(Reader rd) throws IOException {
//    StringBuilder sb = new StringBuilder();
//    int cp;
//    while ((cp = rd.read()) != -1) {
//      sb.append((char) cp);
//    }
//    return sb.toString();
//  }
//
//}
