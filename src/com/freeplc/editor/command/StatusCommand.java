package com.freeplc.editor.command;

import com.freeplc.editor.project.Rung;
import com.freeplc.editor.project.component.Branch;
import com.freeplc.editor.project.component.LadderComponent;

public class StatusCommand extends AbstractCommand {

	public int getCommandId() {
		return PLCCommand.STATUS;
	}
	
	public void process(byte[] data) {
		System.out.println("Processando comando Status");

		int index = 0;
		while (index < data.length) {
			byte fileType = data[index++];
			byte slot = data[index++];
			byte bit = data[index++];
			int value = data[index++]&0xFF;
			
			StringBuffer id = new StringBuffer();
			switch(fileType) {
			case 0x01:
				id.append("I:");
				break;
			case 0x02:
				id.append("O:");
				break;
			case 0x03:
				id.append("M:");
				break;
			}
			id.append(slot).append("/").append(bit);
			updateLadderComponentValue(id.toString(), value);
		}
	}
	
	protected void updateLadderComponentValue(String id, int value) {
		System.out.println("Updating component " + id + " with value: " + value);
		
		for (Rung rung : editor.getCurrentProject().getRungList()) {
			for (LadderComponent comp : rung.getComponentList()) {
				if (!comp.isDelimiter()) {
					if (comp.isBranch()) {
						Branch branch = (Branch)comp;
						for (LadderComponent comp1 : branch.getFirstComponentList()) {								
							if (!comp1.isDelimiter() && comp1.getVariableName().equals(id)) {
								comp1.setBitValue(value);
							}
						}
						for (LadderComponent comp2 : branch.getSecondComponentList()) {
							if (!comp2.isDelimiter() && comp2.getVariableName().equals(id)) {
								comp2.setBitValue(value);
							}
						}
					}
					else {
						if (comp.getVariableName().equals(id)) {
							comp.setBitValue(value);
						}
					}
				}
			}
		}
		editor.updateEditorCanvas();
	}
		

}
