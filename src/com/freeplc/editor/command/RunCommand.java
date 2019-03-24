package com.freeplc.editor.command;

import com.freeplc.editor.util.Util;

public class RunCommand extends AbstractCommand {

	public int getCommandId() {
		return PLCCommand.RUN;
	}
	
	
	public void process(byte[] data) {
		System.out.println("Processando comando Run");
	}
	
	public void prepareCommand(byte[] data) {
		int dataLen = data.length + 1;	// o +1 é por conta do comando
		int rawDataLen = dataLen + 5; // DataLen + STX + Len(2 Bytes) + CRC + ETX 
		rawData = new byte[rawDataLen];	
		rawData[0] = 0x02;		// STX
		
		byte[] dataLenBytes = new byte[2];		// LEN
		Util.intToByte(dataLen, dataLenBytes, Util.MSB_LSB_ORDER);
		rawData[1] = dataLenBytes[0];
		rawData[2] = dataLenBytes[1];
		
		rawData[3] = (byte)getCommandId();
		System.arraycopy(data, 0, rawData, 4, data.length);
		
		rawData[rawDataLen-2] = calculateCRC(rawData, 3, dataLen);	// CRC
		rawData[rawDataLen-1] = 0x03;			// ETX
		
		System.out.println("Download Command : " + Util.formatBytesForPrinting(rawData));
	}
}
