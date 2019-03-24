package com.freeplc.editor.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.freeplc.editor.main.FreePLCEditor;

@SuppressWarnings("serial")
public class FooterPanel extends JPanel {

	FreePLCEditor editor;
	JLabel mousePosition;
	JLabel connectionStatus;
	
	public FooterPanel(FreePLCEditor editor) {
		this.editor = editor;
		

		createUserInterface();
	}
	
	private void createUserInterface() {
		Dimension size = new Dimension(editor.getWidth(), 20);
		setPreferredSize(size);
		
		setLayout(new GridLayout(0, 4));

		size.setSize(200, 20);

		connectionStatus = new JLabel("");
		connectionStatus.setMinimumSize(size);
//		connectionStatus.setBorder(BorderFactory.createLoweredSoftBevelBorder());
		
		mousePosition = new JLabel("Position = [0, 0]");
		mousePosition.setMinimumSize(size);
//		mousePosition.setBorder(BorderFactory.createLoweredSoftBevelBorder());
		
		add(connectionStatus);
		add(mousePosition);
		
		editor.add(this, BorderLayout.PAGE_END);
		
		setConnectionStatusOffline();
	}
	
	
	public void setConnectionStatusOnline() {
		connectionStatus.setText("ONLINE");
		connectionStatus.setForeground(Color.GREEN);
	}
	
	public void setConnectionStatusOffline() {
		connectionStatus.setText("OFFLINE");
		connectionStatus.setForeground(Color.RED);		
	}
	
	public void setConnectionStatus(String status) {
		connectionStatus.setText(status);
	}
	
	public void setMousePosition(int x, int y) {
		mousePosition.setText("Position = [" + x + ", " + y + "]");
	}
	
}
