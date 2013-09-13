package abanyu.transphone.server.view;

import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class ServerFrame extends JFrame{

	public ServerFrame(){	
	  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  setSize( (int)GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth(), 
	  		     (int)GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getHeight() );
	  setLocationByPlatform(true);
	  setVisible(true);
	  setLocationRelativeTo(null);
	  setResizable(false);
	}
}
