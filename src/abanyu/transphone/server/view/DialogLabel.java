package abanyu.transphone.server.view;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class DialogLabel extends JLabel{
	public DialogLabel(String value){
		setText(value);
		setPreferredSize(new Dimension(100,30));
		setFont(new Font("Calibri",Font.PLAIN,15));
	}
}
