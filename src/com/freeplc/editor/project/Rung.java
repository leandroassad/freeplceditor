package com.freeplc.editor.project;

import java.util.ArrayList;

import com.freeplc.editor.project.component.Delimiter;
import com.freeplc.editor.project.component.ExamineIfClosed;
import com.freeplc.editor.project.component.ExamineIfOpen;
import com.freeplc.editor.project.component.LadderComponent;
import com.freeplc.editor.project.component.OutputEnergize;

import lombok.Getter;
import lombok.Setter;

/**
 * Classe Rung, que armazenana os componentes
 * @author leand
 *
 */
public class Rung {
	@Getter ArrayList<LadderComponent> componentList = new ArrayList<LadderComponent>();
	
	@Getter @Setter int startX;
	@Getter @Setter int startY;
	@Getter @Setter int width;
	@Getter @Setter int height;
	@Getter @Setter int index;
	
	public void addComponent(LadderComponent component) {
		componentList.add(component);
	}
	
	public void removeComponent(LadderComponent component) {
		componentList.remove(component);
	}
	
	public void clearComponentList() {
		componentList.clear();
	}
	
	public boolean hasBranch() {
		boolean ret = false;
		
		for ( LadderComponent ladderComponent : componentList) {
			if (ladderComponent.isBranch()) {
				ret = true;
				break;
			}
		}
		
		return ret;
	}
	

	public Rung() {
		
		// Adiciona pelo menos um delimitador ao Rung
		Delimiter delimiter = new Delimiter();
		componentList.add(new Delimiter());
	}
}
