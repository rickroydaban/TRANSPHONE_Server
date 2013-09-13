package abanyu.transphone.server.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

public class MapPanel {

	private double screenWidth;
	private double screenHeight;
	private JWebBrowser webBrowser;
	private JButton buttonOn;
	private JButton buttonOff;
	private JTextField statusField;
	private JTextField taxiCounterField;
	private JTextField clientCountField;
	private JButton manageButton;
	private JButton zoomInButton;
	private JButton zoomOutButton;
	private JPanel mainPanel, powerButtonPanel, webBrowserPanel;

	public MapPanel(){
  	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //manage to get size of the host computer
  	screenWidth	 = screenSize.getWidth()-35; //needed in resizing the preferred width of our embedded website
  	screenHeight = screenSize.getHeight()-140; //needed in resizing the preferred height of you embedded website
	    
    mainPanel = new JPanel(new BorderLayout());
    /**/webBrowserPanel = new JPanel(new BorderLayout());
    		webBrowserPanel.setBorder(BorderFactory.createTitledBorder("Server")); //creates a beveled border
    		webBrowserPanel.setVisible(false);
    		/**/webBrowser = new JWebBrowser(); //instantiates a new Java Web Browser
        		webBrowser.navigate("http://localhost/thesis/multiplemarkers.php"); //defines what site to be viewed on the java web browser
        		webBrowser.setBarsVisible(false); //hides urls and other native browser elements
    
    /**/JPanel controlPanel = new JPanel(new BorderLayout());
    /**//**/powerButtonPanel = new JPanel(new BorderLayout());
						powerButtonPanel.setPreferredSize(new Dimension(95,30));
						powerButtonPanel.setBackground(Color.WHITE);
						powerButtonPanel.setBorder(BorderFactory.createLoweredBevelBorder());

    /**//**//**/buttonOn=new JButton(" Turn On"); 
								buttonOn.setVisible(true);    
    /**//**//**/buttonOff=new JButton("Turn Off");
								buttonOff.setVisible(false); 

    /**//**/JPanel dataPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
    /**//**//**/JLabel statusLabel=new JLabel("Status: ");
    						statusLabel.setPreferredSize(new Dimension(70,30));
    						statusLabel.setHorizontalAlignment(JLabel.RIGHT);
    						statusLabel.setFont(new Font("Calibri",Font.BOLD,15));
    
    /**//**//**/statusField=new JTextField();
    						statusField.setEditable(false);
    						statusField.setPreferredSize(new Dimension(500,30));
    						statusField.setText("OFFLINE. Please switch on to start server...");
    						statusField.setFont(new Font("Calibri",Font.PLAIN,15));
    						statusField.setBackground(Color.WHITE);
    						statusField.setBorder(BorderFactory.createCompoundBorder( statusField.getBorder(), 
                          																								BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    
    /**//**//**/JLabel taxiCountLabel=new JLabel("Units: ");
    						taxiCountLabel.setPreferredSize(new Dimension(50,30));
    						taxiCountLabel.setHorizontalAlignment(JLabel.RIGHT);
    						taxiCountLabel.setFont(new Font("Calibri",Font.BOLD,15));
    						
    /**//**//**/taxiCounterField = new JTextField("0");
    						taxiCounterField.setEditable(false);
    						taxiCounterField.setPreferredSize(new Dimension(50,30));
    						taxiCounterField.setHorizontalAlignment(JTextField.CENTER);
    						taxiCounterField.setFont(new Font("Calibri",Font.PLAIN,14));
    						taxiCounterField.setBackground(Color.WHITE);
    
    /**//**//**/JLabel clientCountLabel = new JLabel("Clients: ");
    						clientCountLabel.setPreferredSize(new Dimension(60,30));
    						clientCountLabel.setHorizontalAlignment(JLabel.RIGHT);
    						clientCountLabel.setFont(new Font("Calibri",Font.BOLD,15));
    
    /**//**//**/clientCountField = new JTextField("0");
    						clientCountField.setEditable(false);
    						clientCountField.setPreferredSize(new Dimension(50,30));
    						clientCountField.setHorizontalAlignment(JTextField.CENTER);
    						clientCountField.setFont(new Font("Calibri",Font.PLAIN,14));
    						clientCountField.setBackground(Color.WHITE);

    				manageButton = new JButton("Manage");
    				manageButton.setPreferredSize(new Dimension(60,33));
    				manageButton.setBorder(BorderFactory.createEmptyBorder());
    						
    /**//**/JPanel zoomButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
    				zoomButtonPanel.setBackground(Color.WHITE);
    				zoomButtonPanel.setBorder(BorderFactory.createLineBorder(Color.decode("#BBBBBB")));

    /**//**//**/zoomInButton = new JButton(new ImageIcon("assets/zoomin.png"));
    /**//**//**/zoomOutButton = new JButton(new ImageIcon("assets/zoomout.png"));

    mainPanel.add(webBrowserPanel, BorderLayout.CENTER);
    /**/webBrowserPanel.add(webBrowser, BorderLayout.CENTER);
    mainPanel.add(controlPanel, BorderLayout.SOUTH);    
		/**/controlPanel.add(powerButtonPanel,BorderLayout.WEST);;
		/**//**/powerButtonPanel.add(buttonOn,BorderLayout.WEST);
		/**//**/powerButtonPanel.add(buttonOff,BorderLayout.EAST);
    /**/controlPanel.add(dataPanel,BorderLayout.CENTER);
		/**//**/dataPanel.add(statusLabel);
		/**//**/dataPanel.add(statusField);
		/**//**/dataPanel.add(taxiCountLabel);
		/**//**/dataPanel.add(taxiCounterField);
		/**//**/dataPanel.add(clientCountLabel);
		/**//**/dataPanel.add(clientCountField);
		
    /**/controlPanel.add(zoomButtonPanel,BorderLayout.EAST);
    /**//**/zoomButtonPanel.add(manageButton);    
    /**//**/zoomButtonPanel.add(zoomInButton);
    /**//**/zoomButtonPanel.add(zoomOutButton);
	}
	
	public double getScreenWidth(){
		return screenWidth;
	}
	
	public double getScreenHeight(){
		return screenHeight;
	}
	
	public JWebBrowser getWebBrowser(){
		return webBrowser;
	}
	
	public JButton getButtonOn(){
		return buttonOn;
	}
	
	public JButton getButtonOff(){
		return buttonOff;
	}
	
	public JTextField getStatusField(){
		return statusField;
	}
	
	public JTextField getTaxiCounterField(){
		return taxiCounterField;
	}
	
	public JTextField getClientCountField(){
		return clientCountField;
	}
	
	public JButton getZoomInButton(){
		return zoomInButton;
	}
	
	public JButton getZoomOutButton(){
		return zoomOutButton;
	}
	
	public JPanel getMapView(){
		return mainPanel;
	}
	
	public JPanel getPowerButtonPanel(){
		return powerButtonPanel;
	}
	
	public JPanel getWebBrowserPanel(){
		return webBrowserPanel;
	}
}
