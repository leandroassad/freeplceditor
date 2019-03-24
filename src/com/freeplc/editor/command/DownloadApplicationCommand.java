package com.freeplc.editor.command;

public class DownloadApplicationCommand extends AbstractCommand {

	public int getCommandId() {
		return PLCCommand.DOWNLOAD_APPLICATION;
	}
	
	public void process(byte[] data) {
		int index = 0;
		int startIndex = 0;
		
		StringBuffer ladder = new StringBuffer();
		System.out.println("Processando comando DownloadAplication");
		while (index < data.length) {
			while (data[index] != 0x0A) {
				index++;
			}
			String rungLine = new String(data, startIndex, index-startIndex);
			ladder.append(rungLine).append("\n");
			System.out.println("Linha do Ladder: " + rungLine);
			index++;
			startIndex = index;
		}
	}
	
//	public void prepareCommand(byte[] data) {
//		int dataLen = data.length + 1;	// o +1 é por conta do comando
//		int rawDataLen = dataLen + 5; // DataLen + STX + Len(2 Bytes) + CRC + ETX 
//		rawData = new byte[rawDataLen];	
//		rawData[0] = 0x02;		// STX
//		
//		byte[] dataLenBytes = new byte[2];		// LEN
//		Util.intToByte(dataLen, dataLenBytes, Util.MSB_LSB_ORDER);
//		rawData[1] = dataLenBytes[0];
//		rawData[2] = dataLenBytes[1];
//		
//		rawData[3] = (byte)getCommandId();
//		System.arraycopy(data, 0, rawData, 4, data.length);
//		
//		rawData[rawDataLen-2] = calculateCRC(rawData, 3, dataLen);	// CRC
//		rawData[rawDataLen-1] = 0x03;			// ETX
//		
//	}
}
