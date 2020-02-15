package edu.postech.aadl.antlr.model;

import java.util.ArrayList;
import java.util.List;

public class ContDynamics implements ContDynamicsElem {
	private List<ContDynamicsItem> cdItem;

	public ContDynamics() {
		cdItem = new ArrayList<ContDynamicsItem>();
	}

	public void addItem(ContDynamicsItem item) {
		cdItem.add(item);
	}

}
