package com.signavio.warehouse.configlist.business;



public class Nodes {
	
	private String name; //name of activity
	private String id;
	private String type; //activity, or, xor, and
	private boolean isConfigurable;
	
	
	public Nodes() {
		
	}
	
	
	public Nodes(String id, String name, String type, boolean isConfigurable)
	{
		this.id=id;
		this.name=name;
		this.type=type; 
		this.isConfigurable=isConfigurable;
	}


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isConfigurable() {
		return isConfigurable;
	}
	public void setConfigurable(boolean isConfigurable) {
		this.isConfigurable = isConfigurable;
	}
	
	public String getNameType(){
		if(type.equals("OR") || type.equals("XOR") || type.equals("AND"))
			return type;
		else
			return name;
	}
	
	public boolean equalNode(Nodes node)
	{
		if(this.id.equals(node.id) && this.type.equals(node.type) && this.name.equals(node.name))
			return true;
		return false;
	}

}
