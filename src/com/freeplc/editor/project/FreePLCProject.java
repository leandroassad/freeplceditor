package com.freeplc.editor.project;

import java.util.ArrayList;
import java.util.List;

import com.freeplc.editor.info.PLCConfiguration;

import lombok.Data;

@Data public class FreePLCProject {

	// Valores default para os tamanhos e posições do Ladder e componentes
	public static final int LADDER_DEFAULT_ZOOM = 100;				// Zoom usado, futuramente
	public static final int LADDER_COMPONENT_WIDTH = 50;			// Largura do comando, para quando o mouse passar sobre ele
	public static final int LADDER_COMPONENT_HEIGHT = 70;			// Altura do comando, para quando o mouse passar sobre ele
	public static final int LADDER_COMMAND_WIDTH = 20;				// Largura do comando, para desenho
	public static final int LADDER_COMMAND_HEIGHT = 30;				// Altura do comando, para desenho
	public static final int LADDER_START_X = 20;					// Posição X de inicio de desenho da linha Lader (Rung)
	public static final int LADDER_START_Y = 20;					// Posição Y de inicio de desenho da linha Lader (Rung)
	
	String projectName;
	String projectFilename = null;
	int plcType;			// Simulador local ou Remote
	boolean error = false;
	boolean connected = false;
	PLCConfiguration	plcConfig;	
	
	ArrayList<Rung> rungList = new ArrayList<Rung>();
	
	public FreePLCProject() {
		Rung rung1 = new Rung();
		rungList.add(rung1);
	}
	
	public FreePLCProject(List<Rung> newRungList) {
		rungList.clear();
		rungList.addAll(newRungList);
	}
	
	
}
