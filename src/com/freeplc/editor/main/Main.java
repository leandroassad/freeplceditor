package com.freeplc.editor.main;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import com.freeplc.editor.config.FreePLCConfig;
import com.freeplc.editor.global.Constants;
import com.freeplc.editor.global.GlobalProperties;
import com.google.gson.Gson;

public class Main {

	public static void main(String[] args) {
        try {
        	System.out.println("FreePLCEditor - " + Constants.FREE_PLC_EDITOR_VERSION + "." + Constants.FREE_PLC_EDITOR_RELEASE);
        	System.out.println("Starting...");        	
        	
        	Gson gson = new Gson();
        	FreePLCConfig freePLCConfig = gson.fromJson(new FileReader(args[0]), FreePLCConfig.class);        	
        	
			GlobalProperties.getInstance().setFreePLCConfig(freePLCConfig);
		
			FreePLCEditor editor = new FreePLCEditor();
        }
        catch(IOException e) {
        	System.out.println("Falha iniciando FreePLCEditor: "  + e.getMessage());
        }
	}

}
