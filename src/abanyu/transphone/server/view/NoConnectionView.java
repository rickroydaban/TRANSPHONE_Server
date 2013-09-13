package abanyu.transphone.server.view;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import abanyu.transphone.server.controller.LoginController;
import abanyu.transphone.server.model.ServerData;

public class NoConnectionView implements ActionListener{
	ServerFrame serverFrame;
	ServerData serverData;
	
	JButton refreshButton;
	JPanel mainPanel;
	
	public NoConnectionView(ServerFrame pServerFrame, ServerData pServerData){
		serverFrame = pServerFrame;
		serverData = pServerData;
		
		mainPanel = new JPanel(new GridBagLayout());  	
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));    
    mainPanel.setBackground(Color.white);

    /**/JPanel alignPanel = new JPanel();
				alignPanel.setLayout(new BoxLayout(alignPanel, BoxLayout.Y_AXIS));
				alignPanel.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createLineBorder(Color.decode("#ABABAB")), 
																														 		 BorderFactory.createEmptyBorder(20,20,20,20)));

				
		/**//**/JPanel labelPanel = new JPanel();
						labelPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);

    /**//**//**/JLabel msg = new JLabel("Cannot Connect To the Database.");
    						msg.setForeground(Color.RED);
    		
    /**//**/JPanel refreshButtonPanel = new JPanel();
    				refreshButtonPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
    
    /**//**//**/JButton refreshButton = new JButton("Refresh");
    						refreshButton.addActionListener(this);
    				
    				labelPanel.add(msg);
    				refreshButtonPanel.add(refreshButton);
    		alignPanel.add(labelPanel);
    		alignPanel.add(refreshButtonPanel);
    
    mainPanel.add(alignPanel);
	}
	
	public JPanel getErrorPanel(){
		return mainPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Refresh")){
    	new LoginController(serverData, serverFrame).operate();
		}
	}
	
	
}
