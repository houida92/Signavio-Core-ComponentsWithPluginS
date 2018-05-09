package com.signavio.warehouse.configlist.business;

import java.util.ArrayList;





public class ConfigNodes {
	
	
	private String id;
	private String processName; //used as id
	private ArrayList<Nodes> nodes;
	
	
	public ConfigNodes() {
		
		nodes = new ArrayList<Nodes>();
	}


	public ConfigNodes(String id) {
		
		nodes = new ArrayList<Nodes>();
		this.id = id;
	}


	public ConfigNodes(String id, String processName, ArrayList<Nodes> nodes) {

		this.id = id;
		this.processName = processName;
		this.nodes = nodes;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getProcessName() {
		return processName;
	}


	public void setProcessName(String processName) {
		this.processName = processName;
	}


	public ArrayList<Nodes> getNodes() {
		return nodes;
	}


	public void setNodes(ArrayList<Nodes> nodes) {
		this.nodes = nodes;
	}
	
	
	public ArrayList<String> getConfigurableElementsID(){
		ArrayList<String> configurableElements = new ArrayList<String>();

		for(Nodes f : nodes){
			if (f.isConfigurable() == true)
				configurableElements.add(f.getId());
		}
		return configurableElements;
	}
	
	public ArrayList<Nodes> getGateways()
	{
		ArrayList<Nodes> gateways = new ArrayList<Nodes>();
		for(Nodes node : nodes)
			if(node.getType().equals("XOR") 
					|| node.getType().equals("AND") 
					|| node.getType().equals("OR"))
				gateways.add(node);
		return gateways;
	}
	
	public ArrayList<Nodes> getConfigurableGateways(){
		ArrayList<Nodes> configurableGateways = new ArrayList<Nodes>();
		ArrayList<Nodes> gateways = getGateways();
		for(Nodes g : gateways){
			if(g.isConfigurable() == true)
				configurableGateways.add(g);
		}
		return configurableGateways;
	}
	
	public void addNode(Nodes n)	{
		nodes.add(n);
	}
	

}
