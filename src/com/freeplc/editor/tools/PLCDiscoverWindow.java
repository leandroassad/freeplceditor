package com.freeplc.editor.tools;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.freeplc.editor.global.Constants;
import com.freeplc.editor.info.PLCConfiguration;
import com.freeplc.editor.main.FreePLCEditor;
import com.freeplc.editor.project.FreePLCProject;

@SuppressWarnings("serial")
public class PLCDiscoverWindow extends JInternalFrame implements ActionListener {	
	static String defaultTitle = "FreePLC Remote Discovery";
	public static final String OK_OPTION = "OK";
	public static final String CANCEL_OPTION = "CANCEL";
	
	public class MyTableModel extends DefaultTableModel {
		public boolean isCellEditable(int row, int col) {
			return false;
		}
	}
	
	JPanel buttonsPanel;
	MyTableModel tableModel;
	JTable table;
	JButton okButton;
	JButton cancelButton;
	
	Vector<PLCConfiguration> plcConfig = new Vector<PLCConfiguration>();
	FreePLCEditor editor;
	String projectName;
	public PLCDiscoverWindow(FreePLCEditor editor, String projectName) {
		super(defaultTitle, true, false, false, true);
		
		this.editor = editor;
		this.projectName = projectName;
		
		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		
		int width = 500;
		int height = 300;
		
		Dimension size = new Dimension(width, height);
		setSize(size);
		setLocation((editor.getWidth()-width)/2, (editor.getHeight()-height)/2);
		
		tableModel = new MyTableModel();
		tableModel.addColumn("Tipo PLC");
		tableModel.addColumn("Endereço");
		tableModel.addColumn("Versão");
		table = new JTable(tableModel);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		for (int columnIndex = 0; columnIndex < tableModel.getColumnCount(); columnIndex++)
        {
			table.getColumnModel().getColumn(columnIndex).setCellRenderer(centerRenderer);
        }
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createTitledBorder("PLCs Descobertos"));
		scrollPane.setPreferredSize(new Dimension(180,600));
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		add(scrollPane, BorderLayout.CENTER);
		
		buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout());
		okButton = new JButton("Confirmar");
		cancelButton = new JButton("Cancelar");
		
		buttonsPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);
		okButton.setEnabled(false);
		cancelButton.setEnabled(false);
		
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		okButton.putClientProperty("ITEMID", OK_OPTION);
		cancelButton.putClientProperty("ITEMID", CANCEL_OPTION);
		
		add(buttonsPanel, BorderLayout.SOUTH);
		
		PLCDiscover discover = new PLCDiscover(this);
		discover.start();
		
		setVisible(true);
	}
	
	public void updateStatus(String status) {
		setTitle(defaultTitle + " - " + status);
	}
	
	
	public boolean tableContainsAddress(String newAdress) {
		int numberOfRows = tableModel.getRowCount();
		for (int i=0; i<numberOfRows; i++) {
			String adress = (String)tableModel.getValueAt(i, 1);
			if (adress.equals(newAdress)) return true;	
		}
		return false;
		
	}

	public void addPLCToTable(PLCConfiguration config) {
		
		if (!tableContainsAddress(config.getAddress() + ":" + config.getPort())) {
			Vector<Object>  data = new Vector<Object>();
			
			data.add(config.getType());
			data.add(config.getAddress() + ":" + String.valueOf(config.getPort()));
			data.add(config.getVersion());

			tableModel.addRow(data);
			
			plcConfig.add(config);
		}
		
	}
	
	public void setDiscoveryFinished() {
		okButton.setEnabled(true);
		cancelButton.setEnabled(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String item = (String)((JButton)e.getSource()).getClientProperty("ITEMID");
		FreePLCProject project = null;
		
		System.out.println("Selected Row: " + table.getSelectedRow());
		switch (item) {
		case OK_OPTION:
			if (tableModel.getRowCount() == 0) {
				JOptionPane.showMessageDialog(this, "Não há PLCs disponíveis","PLCDiscover", JOptionPane.INFORMATION_MESSAGE);
				return;
//				int ret = JOptionPane.showConfirmDialog(this, "Não há PLCs disponíveis\nUsar um Simulador Local?");
//				if (ret == JOptionPane.YES_OPTION) {
//					project = new FreePLCProject();
//					project.setPlcType(Constants.PLC_TYPE_LOCAL_SIMULATOR);
//				}
			}
			else if (table.getSelectedRow() < 0) {
				JOptionPane.showMessageDialog(this, "Selecione um PLC", "PLCDiscover", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			else {
				int index = table.getSelectedRow();
				
				project = new FreePLCProject();
				project.setPlcType(Constants.PLC_TYPE_REMOTE_DISCOVERY);
				PLCConfiguration config = plcConfig.get(index);				
				project.setPlcConfig(config);
				
			}
			
			project.setProjectName(projectName);
			project.setProjectFilename("");
			editor.setFreePLCProject(project);
			editor.createProjectExplorerWindow();
			editor.createDrawWindow();
			
			break;
		case CANCEL_OPTION:
			break;
		}
		
		dispose();
		
	}
	

}
