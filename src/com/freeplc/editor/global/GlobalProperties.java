package com.freeplc.editor.global;

import java.util.Properties;

import com.freeplc.editor.config.FreePLCConfig;

import lombok.Getter;
import lombok.Setter;

public class GlobalProperties {

	protected Properties props;
	@Getter @Setter protected FreePLCConfig freePLCConfig;
	protected GlobalProperties() {
		
	}
	
	public static GlobalProperties global = new GlobalProperties();
	public static GlobalProperties getInstance() {
		return global;
	}
	
	public static void setProperties(Properties newProps) {
		global.props = newProps;
	}

	public static String getProperty(String key) {
		if (global.props != null) {
			return global.props.getProperty(key);
		}
		
		return null;
	}
	
	public static void setProperty(String key, String value) {
		if (global.props != null) {
			global.props.setProperty(key, value);
		}
	}
}
