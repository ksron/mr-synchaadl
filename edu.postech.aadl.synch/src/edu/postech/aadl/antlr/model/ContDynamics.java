package edu.postech.aadl.antlr.model;

import java.util.List;

public interface ContDynamics extends ContDynamicsElem {

	public void addItem(ContDynamicsItem item);

	public List<ContDynamicsItem> getItems();
}
