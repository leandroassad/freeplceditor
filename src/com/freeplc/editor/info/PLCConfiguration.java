package com.freeplc.editor.info;

import lombok.Data;

@Data public class PLCConfiguration {
	String type;
	String address;
	int port;
	int numberOfDigitalInputPorts;
	int numberOfDigitalOutputPorts;
	int numberOfAnalogInputPorts;
	int numberOfAnalogOutputPorts;
	String version;
	String os;
}
