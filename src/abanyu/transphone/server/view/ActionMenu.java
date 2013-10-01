package abanyu.transphone.server.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import abanyu.transphone.server.model.ServerData;

public class ActionMenu implements ActionListener{
	ServerFrame frame;
	ServerData data;
	
	DialogButton changePassButton, changeContactButton, changeBodyNumButton, changeDescButton,
	             addTaxiButton, removeTaxiButton,
	             addDriverButton, removeDriverButton;
	
		public ActionMenu(){
			changePassButton = new DialogButton("Change Password");
			changeContactButton = new DialogButton("Change Contact");
			changeBodyNumButton = new DialogButton("Change Body Number");
			changeDescButton = new DialogButton("Change Description");
			addTaxiButton = new DialogButton("Add Taxi");
			addDriverButton = new DialogButton("Add Driver");
			removeTaxiButton = new DialogButton("Remove Taxi");
			removeDriverButton = new DialogButton("Remove Driver");

			changePassButton.addActionListener(this);
			changeContactButton.addActionListener(this);
			changeBodyNumButton.addActionListener(this);
			changeDescButton.addActionListener(this);
			addTaxiButton.addActionListener(this);
			removeTaxiButton.addActionListener(this);
			addDriverButton.addActionListener(this);
			removeDriverButton.addActionListener(this);
		}
	
    public void displayGUI(ServerFrame pFrame, ServerData pServerData) {
    	frame = pFrame;
    	data = pServerData;
        JOptionPane.showMessageDialog(frame,
                        getPanel(),
                        "Choose Action: ",
                        JOptionPane.PLAIN_MESSAGE);
    }

    private JPanel getPanel() {
      /**/JPanel mainPanel = new JPanel(new BorderLayout());
					mainPanel.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createLineBorder(Color.decode("#ABABAB")), 
																																	BorderFactory.createEmptyBorder(20,20,20,20)));    
					JPanel compPanel = new JPanel();
					JPanel taxiPanel = new JPanel();
					JPanel drivPanel = new JPanel();
					
					compPanel.setLayout(new BoxLayout(compPanel, BoxLayout.Y_AXIS));
					taxiPanel.setLayout(new BoxLayout(taxiPanel, BoxLayout.Y_AXIS));
					drivPanel.setLayout(new BoxLayout(drivPanel, BoxLayout.Y_AXIS));
  				JPanel changePassPanel = new JPanel();
  				JPanel changeContactPanel = new JPanel();
  				JPanel addTaxiPanel = new JPanel();
  				JPanel addDriverPanel = new JPanel();
  				JPanel removeTaxiPanel = new JPanel();
  				JPanel removeDriverPanel = new JPanel();
  				JPanel changeDescPanel = new JPanel();
  				JPanel changeBodyNumPanel = new JPanel();
  				
  				changePassPanel.add(changePassButton);
  				changeContactPanel.add(changeContactButton);
  				compPanel.add(changePassPanel);
  				compPanel.add(changeContactPanel);
  				
  				addTaxiPanel.add(addTaxiButton);
  				changeDescPanel.add(changeDescButton);
  				changeBodyNumPanel.add(changeBodyNumButton);
  				removeTaxiPanel.add(removeTaxiButton);
  				taxiPanel.add(addTaxiPanel);
  				taxiPanel.add(changeBodyNumPanel);
  				taxiPanel.add(changeDescPanel);
  				taxiPanel.add(removeTaxiPanel);
  				
  				addDriverPanel.add(addDriverButton);
  				removeDriverPanel.add(removeDriverButton);
  				drivPanel.add(addDriverPanel);
  				drivPanel.add(removeDriverPanel);
  				
  				mainPanel.add(compPanel,BorderLayout.WEST);
  				mainPanel.add(taxiPanel,BorderLayout.CENTER);
  				mainPanel.add(drivPanel,BorderLayout.EAST);
  				
  			return mainPanel;
    }

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == changePassButton){
				new MultiInputDialog(frame,data).displayChangePasswordGUI();												
			}else if(e.getSource() == changeContactButton){
				new MultiInputDialog(frame,data).displayChangeContactGUI();												
			}else if(e.getSource() == addTaxiButton){
				new MultiInputDialog(frame,data).displayAddTaxiGUI();												
			}else if(e.getSource() == addDriverButton){
				new MultiInputDialog(frame,data).displayAddDriverGUI();								
			}else if(e.getSource() == changeDescButton){
				new MultiInputDialog(frame, data).displayChangeDescriptionGUI();								
			}else if(e.getSource() == changeBodyNumButton){
				new MultiInputDialog(frame, data).displayChangeBodyGUI();								
			}else if(e.getSource() == removeTaxiButton){
				new MultiInputDialog(frame,data).displayRemoveTaxiGUI();								
			}else if(e.getSource() == removeDriverButton){
				new MultiInputDialog(frame,data).displayRemoveDriverGUI();								
			}
		}
}