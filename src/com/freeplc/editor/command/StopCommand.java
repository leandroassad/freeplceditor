package com.freeplc.editor.command;

public class StopCommand extends AbstractCommand {

	public int getCommandId() {
		return PLCCommand.STOP;
	}
	
	public void process(byte[] data) {
		System.out.println("Processando comando Stop");
	}

}
