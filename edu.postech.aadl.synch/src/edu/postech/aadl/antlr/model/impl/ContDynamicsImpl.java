package edu.postech.aadl.antlr.model.impl;

import java.util.ArrayList;
import java.util.List;

import edu.postech.aadl.antlr.model.ContDynamics;
import edu.postech.aadl.antlr.model.ContDynamicsItem;

public class ContDynamicsImpl implements ContDynamics {
	private List<ContDynamicsItem> cdItem = null;

	public ContDynamicsImpl() {
		cdItem = new ArrayList<ContDynamicsItem>();
	}

	@Override
	public void addItem(ContDynamicsItem item) {
		cdItem.add(item);
	}

	@Override
	public List<ContDynamicsItem> getItems() {
		return cdItem;
	}

}
