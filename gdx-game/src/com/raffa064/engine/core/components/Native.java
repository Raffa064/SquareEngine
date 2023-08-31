package com.raffa064.engine.core.components;

import com.raffa064.engine.core.Component;
import com.raffa064.engine.core.Component.ExportedProp;

public abstract class Native extends Component {
	public Native(String name) {
		super(name);
	}
	
	public void exportProp(String name, String type) {
		Component.ExportedProp exportedProp = new ExportedProp(name, type);
		exportedProps.add(exportedProp);
	}
	
	public void exportProps(String... nameAndTypeList) {
		for (int i = 0; i < nameAndTypeList.length; i += 2) {
			exportProp(nameAndTypeList[i], nameAndTypeList[i+1]);
		}
	}
}
