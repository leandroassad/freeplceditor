package com.freeplc.editor.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.freeplc.editor.command.PLCCommand;
import com.freeplc.editor.command.PLCCommandMap;
import com.freeplc.editor.components.FooterPanel;
import com.freeplc.editor.components.LadderCanvas;
import com.freeplc.editor.components.NewProjectWindow;
import com.freeplc.editor.components.ProjectExplorerWindow;
import com.freeplc.editor.components.ToolBarPanel;
import com.freeplc.editor.config.Command;
import com.freeplc.editor.config.FreePLCConfig;
import com.freeplc.editor.global.Constants;
import com.freeplc.editor.global.GlobalProperties;
import com.freeplc.editor.info.PLCConfiguration;
import com.freeplc.editor.project.FreePLCProject;
import com.freeplc.editor.project.LadderFile;
import com.freeplc.editor.project.Rung;
import com.freeplc.editor.project.component.Branch;
import com.freeplc.editor.project.component.Delimiter;
import com.freeplc.editor.project.component.ExamineIfClosed;
import com.freeplc.editor.project.component.ExamineIfOpen;
import com.freeplc.editor.project.component.LadderComponent;
import com.freeplc.editor.project.component.OutputEnergize;
import com.freeplc.editor.util.Util;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
public class FreePLCEditor extends JFrame implements ActionListener {
	
	// MenuItem IDs para chamada à callback
	private static final String NEW_PROJECT = "NEW_PROJECT";
	private static final String LOAD_PROJECT = "LOAD_PROJECT";
	private static final String SAVE_PROJECT = "SAVE_PROJECT";
	private static final String SAVE_AS_PROJECT = "SAVE_AS_PROJECT";
	private static final String EXIT = "EXIT";
	private static final String DISCOVERY = "DISCOVERY";
	private static final String CONFIG = "CONFIG";
	private static final String PROPERTIES = "PROPERTIES";
	private static final String COMPILE = "COMPILE";
	private static final String ABOUT = "ABOUT";
	private static final String CONNECT = "CONNECT";
	private static final String DISCONNECT = "DISCONNECT";
	private static final String DOWNLOAD = "DOWNLOAD";
	private static final String EXECUTE = "EXECUTE";
	
	private static final String DEFAULT_TITLE = "FreePLCEditor - " + Constants.FREE_PLC_EDITOR_VERSION + "." + Constants.FREE_PLC_EDITOR_RELEASE;
	
	private Dimension screenSize;
	private LadderCanvas ladderCanvas;
	private FooterPanel footerPanel;
	private ToolBarPanel toolBarPanel;
	@Getter private ProjectExplorerWindow projectExplorer;
	JScrollPane scrollPane;
	JDesktopPane desktop;
	
	JMenuBar menuBar;
	JMenu fileMenu;
	JMenuItem newProject;
	JMenuItem loadProject;
	JMenuItem saveProject;
	JMenuItem saveAsProject;
	JMenuItem properties;
	JMenuItem exit;
	JMenu projectMenu;
	JMenuItem compile;
	JMenuItem config;
	JMenu toolsMenu;
	JMenuItem discovery;
	JMenu about;
	JMenuItem connect;
	JMenuItem disconnect;
	JMenuItem download;
	JMenuItem execute;
	
	FreePLCProject currentProject = null;
	
	@Getter @Setter Command selectedCommand = null;
	
	private JFrame myself;
	
	@Getter Socket plcSocket = null;
	@Getter DataInputStream plcInputStream = null;
	@Getter DataOutputStream plcOutputStream = null;
	
	public FreePLCEditor() {
		myself = this;
		createUserInterface();
	}
	
	public JDesktopPane getDesktop() {
		return desktop;
	}
	
	protected void createUserInterface() {
		
		setLookAndFeel();
		
		setInitialDimensions();

		setLayout(new BorderLayout());
		
		desktop = new JDesktopPane();
		//setContentPane(desktop);
		desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		
		scrollPane = new JScrollPane(desktop);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		setTitle(DEFAULT_TITLE);
		//setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				exitFreePLCEditor();
			}
		});
		
		FreePLCConfig cfg = GlobalProperties.getInstance().getFreePLCConfig();
		ImageIcon imageIcon = new ImageIcon(cfg.getFreePLC().getIcon());
		setIconImage(imageIcon.getImage());
		
		createMenu();
		createToolBar();
		
		pack();
		setVisible(true);
	}

	private void setInitialDimensions() {
		int inset = 0;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                  screenSize.width  - inset*2,
                  screenSize.height - inset*2);
        
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		setPreferredSize(new Dimension((int)screenSize.getWidth(), (int)screenSize.getHeight()));
		setResizable(true);
	}

	private void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}
	
	public void setFreePLCProject(FreePLCProject project) {
		currentProject = project;
		if (currentProject != null) {
			saveProject.setEnabled(true);
			saveAsProject.setEnabled(true);
			compile.setEnabled(true);
			config.setEnabled(true);
			connect.setEnabled(true);
			setTitle(DEFAULT_TITLE + " - " + currentProject.getProjectName());
		}
	}
	
	public FreePLCProject getCurrentProject() {
		return currentProject;
	}
	
	public void exitFreePLCEditor() {
		if (currentProject != null) {
			saveCurrentProject();
			disconnectFromPLC();
		}
		System.exit(0);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String item = (String)((JMenuItem)e.getSource()).getClientProperty("MENUITEMID");
		
		switch (item) {
		case NEW_PROJECT:
			newProject();
			break;
		case LOAD_PROJECT:
			loadProject();
			break;
		case SAVE_PROJECT:
			saveProject();
			break;
		case SAVE_AS_PROJECT:
			saveCurrentProject();
			break;
		case EXIT:
			exitFreePLCEditor();
			break;
		case COMPILE:
			compileProject();
			break;
		case CONFIG:
			showPLCConfiguration();
			break;
		case CONNECT:
			System.out.println("Conectando no PLC " + currentProject.getPlcConfig().getAddress() + ":" + currentProject.getPlcConfig().getPort());
			if (connectToPLC()) {
				currentProject.setConnected(true);
				connect.setEnabled(false);
				download.setEnabled(true);
				execute.setEnabled(true);
				disconnect.setEnabled(true);
				projectExplorer.updateStatus();
			}
			break;
		case DISCONNECT:
			System.out.println("Desconectando");
			disconnectFromPLC();
			currentProject.setConnected(false);
			connect.setEnabled(true);
			download.setEnabled(false);
			execute.setEnabled(false);
			disconnect.setEnabled(false);
			projectExplorer.updateStatus();
			break;
		case DOWNLOAD:
			System.out.println("Download do projeto: " + currentProject.getProjectName());
			downloadProject();
			break;
		case EXECUTE:
			System.out.println("Executando projeto: " + currentProject.getProjectName());
			executeProject();
		case DISCOVERY:
			break;
		case ABOUT:
			StringBuffer message = new StringBuffer();
			message.append("FREEPlcEditor\n")
			.append("Versão: ")
			.append(Constants.FREE_PLC_EDITOR_VERSION)
			.append(".")
			.append(Constants.FREE_PLC_EDITOR_RELEASE)
			.append("\n\n")
			.append("Licenciado sobre GPL");
			JOptionPane.showMessageDialog(this, message.toString(), "Sobre", JOptionPane.PLAIN_MESSAGE);
			break;
		}
		
	}
	
	private void createMenu() {
		menuBar = new JMenuBar();
		fileMenu = new JMenu("Arquivo");
		newProject = new JMenuItem("Novo Projeto");
		loadProject = new JMenuItem("Carregar Projeto");
		saveProject = new JMenuItem("Salvar Projeto");
		saveAsProject = new JMenuItem("Salvar Projeto Como");
		properties = new JMenuItem("Propriedades");
		exit = new JMenuItem("Sair");
		projectMenu = new JMenu("Projeto");
		compile = new JMenuItem("Compilar");
		config = new JMenuItem("Configuração");
		connect = new JMenuItem("Conectar ao PLC");
		disconnect = new JMenuItem("Desconectar do PLC");
		download = new JMenuItem("Download Ladder");
		execute = new JMenuItem("Executar Ladder");
//		toolsMenu = new JMenu("Ferramentas");
//		discovery = new JMenuItem("Buscar PLCs");
		about = new JMenu("Sobre");
		
		fileMenu.add(newProject);
		fileMenu.add(loadProject);
		fileMenu.add(saveProject);
		fileMenu.add(saveAsProject);
//		fileMenu.add(new JSeparator());
//		fileMenu.add(properties);
		fileMenu.add(new JSeparator());
		fileMenu.add(exit);
		
		saveProject.setEnabled(false);
		saveAsProject.setEnabled(false);
		
		projectMenu.add(compile);
		projectMenu.add(config);
		projectMenu.add(new JSeparator());
		projectMenu.add(connect);
		projectMenu.add(disconnect);
		projectMenu.add(download);
		projectMenu.add(execute);
		
		compile.setEnabled(false);
		config.setEnabled(false);
		connect.setEnabled(false);
		disconnect.setEnabled(false);
		download.setEnabled(false);
		execute.setEnabled(false);
		
//		toolsMenu.add(discovery);
		
		menuBar.add(fileMenu);
		menuBar.add(projectMenu);
//		menuBar.add(toolsMenu);
		//menuBar.add(Box.createHorizontalGlue());
		menuBar.add(about);
		
		setJMenuBar(menuBar);
		
		newProject.putClientProperty("MENUITEMID", NEW_PROJECT);
		loadProject.putClientProperty("MENUITEMID", LOAD_PROJECT);
		saveProject.putClientProperty("MENUITEMID", SAVE_PROJECT);
		saveAsProject.putClientProperty("MENUITEMID", SAVE_AS_PROJECT);
		exit.putClientProperty("MENUITEMID", EXIT);
//		discovery.putClientProperty("MENUITEMID", DISCOVERY);
		properties.putClientProperty("MENUITEMID", PROPERTIES);
		compile.putClientProperty("MENUITEMID", COMPILE);
		config.putClientProperty("MENUITEMID", CONFIG);
		connect.putClientProperty("MENUITEMID", CONNECT);
		disconnect.putClientProperty("MENUITEMID", DISCONNECT);
		download.putClientProperty("MENUITEMID", DOWNLOAD);
		execute.putClientProperty("MENUITEMID", EXECUTE);
		
		newProject.addActionListener(this);
		loadProject.addActionListener(this);
		saveProject.addActionListener(this);
		exit.addActionListener(this);
//		discovery.addActionListener(this);
		properties.addActionListener(this);
		compile.addActionListener(this);
		config.addActionListener(this);
		connect.addActionListener(this);
		disconnect.addActionListener(this);
		download.addActionListener(this);
		execute.addActionListener(this);
		about.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				StringBuffer message = new StringBuffer();
				message.append("FreePLCEditor\n")
				.append("Versão: ")
				.append(Constants.FREE_PLC_EDITOR_VERSION)
				.append(".")
				.append(Constants.FREE_PLC_EDITOR_RELEASE)
				.append("\n\n")
				.append("Autor: Leandro Assad Martins\n\n") 
				.append("Licenciado sobre GPL");
				JOptionPane.showMessageDialog(myself, message.toString(), "Sobre", JOptionPane.PLAIN_MESSAGE);				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				
				
			}
			
		});
		
	}
	
	private void createToolBar() {
		toolBarPanel = new ToolBarPanel(this, "Tools");
	}
	
	private void createFooterPanel() {
		footerPanel = new FooterPanel(this);
	}
	
	public void createProjectExplorerWindow() {
		projectExplorer = new ProjectExplorerWindow(this);
		desktop.add(projectExplorer);
	}
	
	public void createDrawWindow() {
		ladderCanvas = new LadderCanvas(this);
		desktop.add(ladderCanvas);
	}
	
	String lastPath = null;

	
	protected void saveCurrentProject() {
		if (currentProject != null) {
			int ret = JOptionPane.showConfirmDialog(this, "Deseja salvar projeto " + currentProject.getProjectName() + " ?");
			if (ret == JOptionPane.OK_OPTION) {
				saveProject();
			}
			
			projectExplorer.dispose();
			ladderCanvas.dispose();
			setTitle(DEFAULT_TITLE);
			
			currentProject = null;
		}		
	}
	
	public void newProject() {
		saveCurrentProject();
		
		new NewProjectWindow(this);

	}
	public void loadProject() {
		saveCurrentProject();
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("FreePLC Extensions (.xml)", "xml"));
		fileChooser.setDialogTitle("Selecione um projeto");
		if (lastPath == null) {
			try {
			lastPath = new File(".").getCanonicalPath();
			} catch (IOException e){}
		}
		fileChooser.setCurrentDirectory(new File(lastPath));
		
		int ret = fileChooser.showOpenDialog(this);
		if (ret != JFileChooser.APPROVE_OPTION) {
			return;
		}
		
		
		File projFile = fileChooser.getSelectedFile();
		lastPath = projFile.getPath();
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			FileInputStream fis = new FileInputStream(projFile);
			InputSource is = new InputSource(fis);
			Document doc = docBuilder.parse(is);
			
			Element rootElement = doc.getDocumentElement(); // FreePLCProject
			if (rootElement != null && rootElement.getNodeName().equals("FreePLCProject")) {			
				String projectName = rootElement.getAttribute("name");
				String projectType = rootElement.getAttribute("plcType");
				String address= null, os=null, version=null, type=null;
				int port = 0, numberOfDigitalInputPorts=0, numberOfDigitalOutputPorts=0, numberOfAnalogInputPorts=0, numberOfAnalogOutputPorts=0;
				
				System.out.println("Projeto: " + projectName + " Tipo: " + projectType);
				
				if (projectType.equals("1")) {
					NodeList remoteNodes = rootElement.getElementsByTagName("RemotePLC");
					Node remoteNode = remoteNodes.item(0);
					
					NodeList attrList = remoteNode.getChildNodes();
					for (int i=0; i<attrList.getLength(); i++) {
						Node attrNode = attrList.item(i);
						if (attrNode.getNodeType() == Node.ELEMENT_NODE) {
							String nodeName = attrNode.getNodeName();
							if (nodeName.equals("address")) {
								address = attrNode.getTextContent();
							}
							else if (nodeName.equals("port")) {
								port = Integer.parseInt(attrNode.getTextContent());
							}
							else if (nodeName.equals("type")) {
								type = attrNode.getTextContent();
							}
							else if (nodeName.equals("numberOfDigitalInputPorts")) {
								numberOfDigitalInputPorts = Integer.parseInt(attrNode.getTextContent());
							}
							else if (nodeName.equals("numberOfDigitalOutputPorts")) {
								numberOfDigitalOutputPorts = Integer.parseInt(attrNode.getTextContent());
							}
							else if (nodeName.equals("numberOfAnalogInputPorts")) {
								numberOfAnalogInputPorts = Integer.parseInt(attrNode.getTextContent());
							}
							else if (nodeName.equals("numberOfAnalogOutputPorts")) {
								numberOfAnalogOutputPorts = Integer.parseInt(attrNode.getTextContent());
							}
							else if (nodeName.equals("os")) {
								os = attrNode.getTextContent();
							}
							else if (nodeName.equals("version")) {
								version = attrNode.getTextContent();
							}
						}
					}
				}
				
				NodeList nodes = rootElement.getElementsByTagName("Ladder");
				Node ladderNode = nodes.item(0);
				NodeList rungNodes = ladderNode.getChildNodes();
				ArrayList<Rung> rungList = new ArrayList<Rung>();
				for (int i=0; i<rungNodes.getLength(); i++) {
					Node rungNode = rungNodes.item(i);
					if (rungNode.getNodeType() == Node.ELEMENT_NODE) {
						Element rungElement = (Element)rungNode;
						
						// faz o parser de cada Rung para criar o projeto
						System.out.println("Rung: " + rungElement.getAttribute("index") + " = " + rungElement.getTextContent());
						String rungLine = rungElement.getTextContent();
						Rung rung = new Rung();
						
						String[] token = rungLine.split(" ");
						int index = 0;
						String varName;
						LadderComponent component;
						Branch branchComponent=null;
						int branchList = -1;
						while (index < token.length) {
							switch(token[index]) {
							case "":
							case " ":
								index++;
								break;
							case "XIC":
								index++;
								varName = token[index++];
								component = new ExamineIfClosed();
								component.setVariableName(varName);
								if (branchComponent != null) {
									component.setMyBranch(branchComponent);
									component.setUnderBranch(true);
									Delimiter del = new Delimiter();
									del.setMyBranch(branchComponent);
									del.setUnderBranch(true);
									if (branchList == 1) {
										branchComponent.getFirstComponentList().add(component);
										branchComponent.getFirstComponentList().add(del);
									}
									else if (branchList == 2) {
										branchComponent.getSecondComponentList().add(component);
										branchComponent.getSecondComponentList().add(del);										
									}
								}
								else {
									rung.addComponent(component);
									rung.addComponent(new Delimiter());
								}
								break;
							case "XIO":
								index++;
								varName = token[index++];
								component = new ExamineIfOpen();
								component.setVariableName(varName);
								if (branchComponent != null) {
									component.setMyBranch(branchComponent);
									component.setUnderBranch(true);
									Delimiter del = new Delimiter();
									del.setMyBranch(branchComponent);
									del.setUnderBranch(true);
									if (branchList == 1) {
										branchComponent.getFirstComponentList().add(component);
										branchComponent.getFirstComponentList().add(del);
									}
									else if (branchList == 2) {
										branchComponent.getSecondComponentList().add(component);
										branchComponent.getSecondComponentList().add(del);										
									}
								}
								else {
									rung.addComponent(component);
									rung.addComponent(new Delimiter());
								}
								break;
							case "OTE":
								index++;
								varName = token[index++];
								component = new OutputEnergize();
								component.setVariableName(varName);
								rung.addComponent(component);
								break;
							case "BST":
								branchComponent = new Branch();
								branchList = 1;
								index++;
								break;
							case "NXB":
								index++;
								branchList = 2;
								break;
							case "BND":
								index++;
								branchList = -1;
								rung.addComponent(branchComponent);
								rung.addComponent(new Delimiter());
								break;
							default:
								index++;
							}
						}
						rungList.add(rung);
					}
				}
				
				FreePLCProject project = new FreePLCProject(rungList);
				project.setProjectName(projectName);
				project.setPlcType(Integer.parseInt(projectType));
				project.setProjectFilename(projFile.getAbsolutePath());
				PLCConfiguration plcConfig = new PLCConfiguration();
				plcConfig.setType(type);
				plcConfig.setAddress(address);
				plcConfig.setPort(port);
				plcConfig.setNumberOfDigitalInputPorts(numberOfDigitalInputPorts);
				plcConfig.setNumberOfDigitalOutputPorts(numberOfDigitalOutputPorts);
				plcConfig.setNumberOfAnalogInputPorts(numberOfAnalogInputPorts);
				plcConfig.setNumberOfAnalogOutputPorts(numberOfAnalogOutputPorts);
				plcConfig.setOs(os);
				plcConfig.setVersion(version);
				project.setPlcConfig(plcConfig);
				
				this.setFreePLCProject(project);
				this.createProjectExplorerWindow();
				this.createDrawWindow();
			}
			
			fis.close();
			
		} catch (ParserConfigurationException | SAXException | IOException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(this, "Falha carregando projeto");
		}
	}
	
	public void saveProject() {
		
		File projectFile = null;
		
		if (currentProject == null) return;
		
		if (currentProject.getProjectFilename() == null || currentProject.getProjectFilename().isEmpty()) {
			JFileChooser fileChooser = new JFileChooser();
			
			fileChooser.setDialogTitle("Selecione a pasta destino");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("FreePLC Extensions (.xml)", "xml"));
			if (lastPath == null) {
				try {
				lastPath = new File(".").getCanonicalPath();
				} catch (IOException e){}
			}
			fileChooser.setCurrentDirectory(new File(lastPath));
			
			int ret = fileChooser.showSaveDialog(this);
			if (ret != JFileChooser.APPROVE_OPTION) {
				return;
			}
			
			projectFile = fileChooser.getSelectedFile();
			currentProject.setProjectFilename(projectFile.getAbsolutePath());
		}
		else {
			projectFile = new File(currentProject.getProjectFilename());
		}
		
		
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("FreePLCProject");
			doc.appendChild(rootElement);
			
			Attr version = doc.createAttribute("version");
			version.setValue(Constants.FREE_PLC_EDITOR_VERSION + "." + Constants.FREE_PLC_EDITOR_RELEASE);
			rootElement.setAttributeNode(version);
			
			Attr name = doc.createAttribute("name");
			name.setValue(currentProject.getProjectName());
			rootElement.setAttributeNode(name);
			
			Attr plcType = doc.createAttribute("plcType");
			plcType.setValue(String.valueOf(currentProject.getPlcType()));
			rootElement.setAttributeNode(plcType);
			
			if (currentProject.getPlcType() == Constants.PLC_TYPE_REMOTE_DISCOVERY) {
				
				Element remotePlc = doc.createElement("RemotePLC");
				rootElement.appendChild(remotePlc);
				
				Element address = doc.createElement("address");
				address.appendChild(doc.createTextNode(currentProject.getPlcConfig().getAddress()));
				remotePlc.appendChild(address);

				Element port = doc.createElement("port");
				port.appendChild(doc.createTextNode(String.valueOf(currentProject.getPlcConfig().getPort())));
				remotePlc.appendChild(port);

				Element type = doc.createElement("type");
				type.appendChild(doc.createTextNode(currentProject.getPlcConfig().getType()));
				remotePlc.appendChild(type);

				Element numberOfDigitalInputPorts = doc.createElement("numberOfDigitalInputPorts");
				numberOfDigitalInputPorts.appendChild(doc.createTextNode(String.valueOf(currentProject.getPlcConfig().getNumberOfDigitalInputPorts())));
				remotePlc.appendChild(numberOfDigitalInputPorts);

				Element numberOfDigitalOutputPorts = doc.createElement("numberOfDigitalOutputPorts");
				numberOfDigitalOutputPorts.appendChild(doc.createTextNode(String.valueOf(currentProject.getPlcConfig().getNumberOfDigitalOutputPorts())));
				remotePlc.appendChild(numberOfDigitalOutputPorts);

				Element numberOfAnalogInputPorts = doc.createElement("numberOfAnalogInputPorts");
				numberOfAnalogInputPorts.appendChild(doc.createTextNode(String.valueOf(currentProject.getPlcConfig().getNumberOfAnalogInputPorts())));
				remotePlc.appendChild(numberOfAnalogInputPorts);

				Element numberOfAnalogOutputPorts = doc.createElement("numberOfAnalogOutputPorts");
				numberOfAnalogOutputPorts.appendChild(doc.createTextNode(String.valueOf(currentProject.getPlcConfig().getNumberOfAnalogOutputPorts())));
				remotePlc.appendChild(numberOfAnalogOutputPorts);

				Element os = doc.createElement("os");
				os.appendChild(doc.createTextNode(currentProject.getPlcConfig().getOs()));
				remotePlc.appendChild(os);

				Element plcVersion = doc.createElement("version");
				plcVersion.appendChild(doc.createTextNode(currentProject.getPlcConfig().getVersion()));
				remotePlc.appendChild(plcVersion);

			}
			
			int rungIndex = 0;
			Element ladderElement = doc.createElement("Ladder");
			for (Rung rung : currentProject.getRungList()) {
				Element rungElement = doc.createElement("Rung");
				Attr rungNumber = doc.createAttribute("index");
				rungNumber.setValue(Integer.toString(rungIndex++));
				rungElement.setAttributeNode(rungNumber);
				
				StringBuffer ladderBuffer = new StringBuffer();
				for (LadderComponent component : rung.getComponentList()) {
					if (component.isComputed()) {
						if (component.isBranch()) {
							Branch branch = (Branch)component;
							ladderBuffer.append("BST ");
							for(LadderComponent c : branch.getFirstComponentList()) {
								if (c.isComputed())
									ladderBuffer.append(c.getId()).append(' ').append(c.getVariableName()).append(' ');
							}
							ladderBuffer.append("NXB ");
							for(LadderComponent c : branch.getSecondComponentList()) {
								if (c.isComputed())
									ladderBuffer.append(c.getId()).append(' ').append(c.getVariableName()).append(' ');
							}
							ladderBuffer.append("BND ");
						}
						else 
							ladderBuffer.append(component.getId()).append(' ').append(component.getVariableName()).append(' ');
					}
				}
				rungElement.setTextContent(ladderBuffer.toString());
				ladderElement.appendChild(rungElement);
			}
			rootElement.appendChild(ladderElement);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(projectFile);

			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);
			
			JOptionPane.showMessageDialog(this, "Projeto salvo no arquivo " + currentProject.getProjectFilename(), "FreePLC", JOptionPane.INFORMATION_MESSAGE);
			
		} catch (ParserConfigurationException | TransformerException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(this, "Falha salvando projeto");
			return;
		}

	}
	
	/*
	 * Compilação do projeto corrente.
	 */
	void compileProject() {
		boolean errorFlag = false;
		if (currentProject != null) {
			StringBuffer errorList = new StringBuffer();
			for (Rung rung : currentProject.getRungList()) {
				for (LadderComponent component : rung.getComponentList()) {
					if (!component.isDelimiter()) {
						if (component.isBranch()) {
							Branch branch = (Branch)component;
							for (LadderComponent comp1 : branch.getFirstComponentList()) {								
								if (!comp1.isDelimiter() && LadderFile.parseVariable(comp1.getVariableName()) == null) {
									errorList.append("Componente ").append(comp1.getId()).append(" no Rung #").append(rung.getIndex())
										.append(" não está com associado a uma entrada ou saída válida\n");
									errorFlag= true;
									comp1.setCompileError(true);
								}
								else comp1.setCompileError(false);
							}
							for (LadderComponent comp2 : branch.getSecondComponentList()) {
								if (!comp2.isDelimiter() && LadderFile.parseVariable(comp2.getVariableName()) == null) {
									errorList.append("Componente ").append(comp2.getId()).append(" no Rung #").append(rung.getIndex())
										.append(" não está com associado a uma entrada ou saída válida\n");
									errorFlag= true;
									comp2.setCompileError(true);
								}
								else comp2.setCompileError(false);
							}
						}
						else {
							if (LadderFile.parseVariable(component.getVariableName()) == null) {
								errorList.append("Componente ").append(component.getId()).append(" no Rung #").append(rung.getIndex())
									.append(" não está com associado a uma entrada ou saída válida\n");
								errorFlag= true;
								component.setCompileError(true);
							}
							else component.setCompileError(false);
						}
					}
				}
			}
			
			if (errorFlag == false) {
				errorList.append("Não há erros no Programa Ladder");
				currentProject.setError(false);
			}
			else currentProject.setError(true);
			
			projectExplorer.updateStatus();
			
			JOptionPane.showMessageDialog(this, errorList.toString(), "Resultado da compilação do Projeto " + currentProject.getProjectName(), JOptionPane.INFORMATION_MESSAGE);
			
			repaint();
		}
	}
	
	public void updateEditorCanvas() {
		repaint();
	}
	
	public void unselectLastSelectedCommand() {
		toolBarPanel.unselectLastSelectedCommand();
		selectedCommand = null;
	}
	
	public boolean connectToPLC() {
		boolean ret = false;
		if (plcSocket == null) {
			try {
				plcSocket = new Socket(currentProject.getPlcConfig().getAddress(), currentProject.getPlcConfig().getPort());
				plcInputStream = new DataInputStream(plcSocket.getInputStream());
				plcOutputStream = new DataOutputStream(plcSocket.getOutputStream());
				
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							handleNewConnection();
						} catch (IOException e) {
							disconnectFromPLC();
						}
					}
					
				}).start();
				
				ret = true;
			}
			catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Falha conectando ao PLC " + currentProject.getPlcConfig().getAddress() + ":" + currentProject.getPlcConfig().getPort() , "FreePLC", JOptionPane.ERROR_MESSAGE);
				plcSocket = null;
				plcInputStream = null;
				plcOutputStream = null;
				ret = false;
			}
		}
		
		return ret;
	}
	
	
	public void handleNewConnection() throws IOException {
		
		byte[] header = new byte[3];
		byte[] dataLen = new byte[2];
		byte[] data = null;
		int nBytes;
		
		while (true) {
			Arrays.fill(header, (byte)0x00);
			// Faz a leitura do header
			nBytes = Util.readBytes(plcSocket, header);
			
			if (nBytes < 0) {
				break;
			}
			
			if (header[0] == 0x02) {
				dataLen[0] = header[1];
				dataLen[1] = header[2];
				int len = Util.byteToInt(dataLen, Util.MSB_LSB_ORDER);
				data = new byte[len];
				
				nBytes = Util.readBytes(plcSocket, data);
				if (nBytes < len) {
					break;
				}
				else {
					
					System.out.println("Bytes recebidos: " + Util.formatBytesForPrinting(data));

					// Le CRC e ETX
					byte []footer = new byte[2];
					Util.readBytes(plcSocket, footer);

					byte crcData = footer[0];
					byte etx = footer[1];
					byte crc = calcCRC(data, len);					
					if (crcData != crc) {
						System.out.println("Falha: Frame com CRC invalido.\nCalculado=" + Integer.toHexString(crc) + "\nFornecido=" + Integer.toHexString(crcData));
						break;
					}
					if (etx != 0x03) {
						System.out.println("Falha: Frame sem ETX");
						break;
					}
										
					byte commandId = data[0];
					PLCCommand command = PLCCommandMap.getPLCCommandMap().getPLCCommand(commandId, this);
					if (command != null) {
						byte [] rawData = new byte[len-1];
						System.arraycopy(data, 1, rawData, 0, len-1);
						command.process(rawData);
					}					
				}
			}
			else {
				
				break;
			}
		}
		
//		try {
//			dataIn.close();
//			dataOut.close();
//			if (currentConnection != null) {
//				currentConnection.close();
//				currentConnection = null;
//				connected = false;
//			}
//		}
//		catch (IOException e) {}
	}
	
	public byte calcCRC(byte[] data, int len) {
		byte crc = 0;
		
		for (int i=0; i<len; i++) {
			crc ^= (byte)data[i];
		}
		
		return crc;
	}
	
	public boolean disconnectFromPLC() {
		boolean ret = false;
		
		if (plcSocket != null) {
			try {
				PLCCommand disconn = PLCCommandMap.getPLCCommandMap().getPLCCommand((byte)PLCCommand.DISCONNECT, this);
				disconn.prepareCommand(null);
				plcOutputStream.write(disconn.getBytes());
				plcOutputStream.flush();
				
				plcInputStream.close();
				plcOutputStream.close();
				plcOutputStream.flush();
				
				plcSocket.close();
			}
			catch (IOException e) {}
			finally {
				plcSocket = null;
				plcInputStream = null;
				plcOutputStream = null;
			}
		}

		return ret;
	}
	
	public void downloadProject() {
		// Obtem o lader completo do projeto, em formato texto e com os codigos dos comandos
		StringBuffer ladderBuffer = new StringBuffer();
		for (Rung rung : currentProject.getRungList()) {			
			for (LadderComponent component : rung.getComponentList()) {
				if (component.isComputed()) {
					if (component.isBranch()) {
						Branch branch = (Branch)component;
						ladderBuffer.append("BST ");
						for(LadderComponent c : branch.getFirstComponentList()) {
							if (c.isComputed())
								ladderBuffer.append(c.getId()).append(' ').append(c.getVariableName()).append(' ');
						}
						ladderBuffer.append("NXB ");
						for(LadderComponent c : branch.getSecondComponentList()) {
							if (c.isComputed())
								ladderBuffer.append(c.getId()).append(' ').append(c.getVariableName()).append(' ');
						}
						ladderBuffer.append("BND ");
					}
					else 
						ladderBuffer.append(component.getId()).append(' ').append(component.getVariableName()).append(' ');
				}
			}
			ladderBuffer.append("\n");
		}

		System.out.println(ladderBuffer.toString());
		PLCCommand dap = PLCCommandMap.getPLCCommandMap().getPLCCommand((byte)PLCCommand.DOWNLOAD_APPLICATION, this);
		dap.prepareCommand(ladderBuffer.toString().getBytes());
		byte[] rawData = dap.getBytes();
		try {
			plcOutputStream.write(rawData);
			plcOutputStream.flush();
			
			JOptionPane.showMessageDialog(this, "Download do Projeto efetuado com sucesso", "FreePLC Editor", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	JButton playButton = null;
	JButton stopButton = null;
	JButton stepButton = null;
	public void executeProject() {
		JPanel runPanel = new JPanel(new BorderLayout());
//		GridLayout layout = new GridLayout(2, 1);
//		layout.setHgap(5);
//		layout.setVgap(5);
//		runPanel.setLayout(layout);
		
		JPanel intervalPanel = new JPanel();
		GridLayout layout = new GridLayout(2, 1);
		layout.setHgap(5);
		layout.setVgap(5);
		intervalPanel.setLayout(layout);
		
		JLabel label = new JLabel("Intervalo (ms)", JLabel.CENTER);
		JSlider interval = new JSlider(JSlider.HORIZONTAL, 0, 20000, 1000);
		interval.setMajorTickSpacing(5000);
		interval.setMinorTickSpacing(1000);
		interval.setPaintTicks(true);
		interval.setPaintLabels(true);
		Font font = new Font("Serif", Font.ITALIC, 15);
        interval.setFont(font);
        label.setFont(font);
		
		intervalPanel.add(label);
		intervalPanel.add(interval);
		
		JPanel buttonsPanel = new JPanel();
		layout = new GridLayout(1, 3);
		layout.setHgap(5);
		layout.setVgap(5);
		buttonsPanel.setLayout(layout);

		Image playImg=null, stopImg=null, stepImg=null;
		try {
			playImg = ImageIO.read(getClass().getResource("/toolbarButtonGraphics/media/Play24.gif"));
			stopImg = ImageIO.read(getClass().getResource("/toolbarButtonGraphics/media/Stop24.gif"));
			stepImg = ImageIO.read(getClass().getResource("/toolbarButtonGraphics/media/StepForward24.gif"));
		}
		catch(IOException e) {}

		playButton = new JButton(new ImageIcon(playImg));
		playButton.setToolTipText("Executar Ladder com o intervalo acima");
		
		stopButton = new JButton(new ImageIcon(stopImg));		
		stopButton.setToolTipText("Parar execução do Ladder");
		stopButton.setEnabled(false);
		
		stepButton = new JButton(new ImageIcon(stepImg));
		stepButton.setToolTipText("Executar Ladder somente 1 vez");

		playButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				runCommand(interval.getValue());
			}
		});
		
		stopButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {				
				stopCommand();
			}
		});
		
		stepButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				stepCommand();				
			}
		});
		
		buttonsPanel.add(playButton);
		buttonsPanel.add(stopButton);
		buttonsPanel.add(stepButton);		

		runPanel.add(intervalPanel, BorderLayout.CENTER);
		runPanel.add(buttonsPanel, BorderLayout.PAGE_END);
		
		
		JDialog runDialog = new JDialog(this, "Execução do Projeto [" + currentProject.getProjectName() + "]", true);
		runDialog.getContentPane().add(runPanel);
		
		runDialog.setPreferredSize(new Dimension(400, 200));
		
		runDialog.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("Fechando Run Dialog...");
			}
		});

		runDialog.pack();
		runDialog.setVisible(true);
	}
	
	public void runCommand(int interval) {		
		if (plcSocket != null) {
			byte[] intervalBytes = new byte[2];
			Util.intToByte(interval, intervalBytes, Util.MSB_LSB_ORDER);
			
			PLCCommand command = PLCCommandMap.getPLCCommandMap().getPLCCommand((byte)PLCCommand.RUN, this);
			command.prepareCommand(intervalBytes);
			
			try {
				plcOutputStream.write(command.getBytes());
				plcOutputStream.flush();
			}
			catch (IOException e) {
				e.printStackTrace();
			}			
		}
		
		playButton.setEnabled(false);
		stopButton.setEnabled(true);
		stepButton.setEnabled(false);
	}
	
	public void stopCommand() {		
		if (plcSocket != null) {
			PLCCommand command = PLCCommandMap.getPLCCommandMap().getPLCCommand((byte)PLCCommand.STOP, this);
			command.prepareCommand(null);
			
			try {
				plcOutputStream.write(command.getBytes());
				plcOutputStream.flush();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		playButton.setEnabled(true);
		stopButton.setEnabled(false);
		stepButton.setEnabled(true);
	}
	

	public void stepCommand() {		
		if (plcSocket != null) {
			PLCCommand command = PLCCommandMap.getPLCCommandMap().getPLCCommand((byte)PLCCommand.STEP, this);
			command.prepareCommand(null);
			
			try {
				plcOutputStream.write(command.getBytes());
				plcOutputStream.flush();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void showPLCConfiguration() {
		StringBuffer buffer = new StringBuffer();
		PLCConfiguration config = currentProject.getPlcConfig();
		
		buffer.append("<html>");
		buffer.append("<font face='Calibri' size='15'>Nome do Projeto : </font><font face='Calibri' size='15' color='blue'>").append(currentProject.getProjectName()).append("</font><br>");
		buffer.append("<font face='Calibri' size='15'>Tipo de PLC : </font><font face='Calibri' size='15' color='blue'>").append(config.getType()).append("</font><br>");
		buffer.append("<font face='Calibri' size='15'>Endereço:Porta : </font><font face='Calibri' size='15' color='blue'>").append(config.getAddress()).append(":").append(config.getPort()).append("</font><br>");
		buffer.append("<font face='Calibri' size='15'>Portas Digitais de Entrada :</font> <font face='Calibri' size='15' color='blue'>").append(config.getNumberOfDigitalInputPorts()).append("</font><br>");
		buffer.append("<font face='Calibri' size='15'>Portas Digitais de Saída :</font> <font face='Calibri' size='15' color='blue'>").append(config.getNumberOfDigitalOutputPorts()).append("</font><br>");
		buffer.append("<font face='Calibri' size='15'>Portas Analógicas de Entrada : </font><font face='Calibri' size='15' color='blue'>").append(config.getNumberOfAnalogInputPorts()).append("</font><br>");
		buffer.append("<font face='Calibri' size='15'>Portas Analógicas de Saída :</font> <font face='Calibri' size='15' color='blue'>").append(config.getNumberOfAnalogOutputPorts()).append("</font><br>");
		buffer.append("<font face='Calibri' size='15'>Versão : </font><font face='Calibri' size='15' color='blue'>").append(config.getVersion()).append("</font><br>");
		buffer.append("<font face='Calibri' size='15'>Sistema Operacional : </font><font face='Calibri' size='15' color='blue'>").append(config.getOs()).append("</font><br></html>");

//		UIManager.put("OptionPane.messageFont", new Font("Arial", Font.BOLD, 14));
//		UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.PLAIN, 12));
		
		JOptionPane.showMessageDialog(this, buffer.toString(), "Configuração PLC", JOptionPane.INFORMATION_MESSAGE);
	}

}
