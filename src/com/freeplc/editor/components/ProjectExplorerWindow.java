package com.freeplc.editor.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import com.freeplc.editor.global.Constants;
import com.freeplc.editor.main.FreePLCEditor;
import com.freeplc.editor.project.FreePLCProject;

@SuppressWarnings("serial")
public class ProjectExplorerWindow extends JInternalFrame  {
	JScrollPane scrollPane;
	FreePLCEditor editor;
	JTree projectTree;
	
	DefaultMutableTreeNode projectNode;
	DefaultMutableTreeNode plcNode;
	DefaultMutableTreeNode dataFilesNode;
	DefaultMutableTreeNode ladderNode;
	
	// DataFiles Nodes
	DefaultMutableTreeNode inputNode;
	DefaultMutableTreeNode outputNode;
	DefaultMutableTreeNode memoryNode;
	DefaultMutableTreeNode timerNode;
	DefaultMutableTreeNode counterNode;
	DefaultMutableTreeNode integerNode;
	DefaultMutableTreeNode floatNode;

	
	public ProjectExplorerWindow(FreePLCEditor editor) {
		super("Project Explorer", true, false, false, true);
		this.editor = editor;
		
		createUserInterface();
	}
	
	private void createUserInterface() {
		Dimension size = new Dimension(250, editor.getHeight()-70);
		setSize(size);
		setLocation(0,0);
				
		FreePLCProject project = editor.getCurrentProject();
		
		projectNode = new DefaultMutableTreeNode("Projeto: " + project.getProjectName());
		
		String plcNodeStr = "PLC Simulado (local)";
		if (project.getPlcType() == Constants.PLC_TYPE_REMOTE_DISCOVERY) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("PLC Remoto (")
			.append(project.getPlcConfig().getAddress())
			.append(":")
			.append(project.getPlcConfig().getPort())
			.append(")");
			plcNodeStr = buffer.toString();
		}
		plcNode = new DefaultMutableTreeNode(plcNodeStr);
		ladderNode = new DefaultMutableTreeNode("Ladder");
		dataFilesNode = new DefaultMutableTreeNode("Data Files");
				
		projectNode.add(plcNode);
		projectNode.add(ladderNode);
		projectNode.add(dataFilesNode);
		
		createDataFilesNodes(dataFilesNode);

		projectTree = new JTree(projectNode);
		projectTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		projectTree.setCellRenderer(new MyTreeCellRenderer());
		javax.swing.ToolTipManager.sharedInstance().registerComponent(projectTree);
		
		scrollPane = new JScrollPane(projectTree);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		setBackground(Color.WHITE);	
		
		add(scrollPane);
		
		projectTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
	            if (e.getClickCount() == 2) {
	                DefaultMutableTreeNode node = (DefaultMutableTreeNode)
	                       projectTree.getLastSelectedPathComponent();
	                if (node == null) return;
	                String nodeInfo = (String)node.getUserObject();
	                System.out.println("No selecionado: " + nodeInfo);
	                // Cast nodeInfo to your object and do whatever you want
	            }
	        }
		});
		
		setVisible(true);
	}
	
	private void createDataFilesNodes(DefaultMutableTreeNode dataFilesNode) {
		
		inputNode = new DefaultMutableTreeNode("I - Input");
		outputNode = new DefaultMutableTreeNode("O - Output");
		memoryNode = new DefaultMutableTreeNode("M - Memory");
		//timerNode = new DefaultMutableTreeNode("T - Timer");
		//counterNode = new DefaultMutableTreeNode("C - Counter");
		integerNode = new DefaultMutableTreeNode("N - Integer");
		floatNode = new DefaultMutableTreeNode("F - Float");
		
		dataFilesNode.add(inputNode);
		dataFilesNode.add(outputNode);
		dataFilesNode.add(memoryNode);
		//dataFilesNode.add(timerNode);
		//dataFilesNode.add(counterNode);
		dataFilesNode.add(integerNode);
		dataFilesNode.add(floatNode);
		
	}
	
	public void updateStatus() {
		projectTree.repaint();
	}
	
	public class MyTreeCellRenderer extends DefaultTreeCellRenderer {

	    @Override
	    public Component getTreeCellRendererComponent(JTree tree, Object value,
	            boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
	        super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);

	        // Assuming you have a tree of Strings
	        String node = (String) ((DefaultMutableTreeNode) value).getUserObject();

	        if (leaf) {
	        	if (node.contains("Ladder")) {	            
		        	if (editor.getCurrentProject().isError())
		            	setForeground(Color.RED);
		        	else
		        		setForeground(Color.GREEN);
	        	}
	        	else if (node.contains("PLC")) {
	        		if (editor.getCurrentProject().isConnected()) {
		            	setForeground(Color.GREEN);
		            	setToolTipText("Conectado");
	        		}
		        	else {
		        		setForeground(Color.RED);
		        		setToolTipText("Desconectado");
		        	}
	        	}
	        }

	        return this;
	    }
	}

}
