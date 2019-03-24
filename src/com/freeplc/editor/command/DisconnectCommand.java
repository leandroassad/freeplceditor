package com.freeplc.editor.command;

public class DisconnectCommand extends AbstractCommand {

	public int getCommandId() {
		return PLCCommand.DISCONNECT;
	}
	
	public void process(byte[] data) {
		System.out.println("Processando comando Disconnect");
	}

}
